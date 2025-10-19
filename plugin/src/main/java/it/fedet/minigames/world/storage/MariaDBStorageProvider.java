package it.fedet.minigames.world.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.fedet.minigames.api.world.data.WorldData;
import it.fedet.minigames.api.world.storage.WorldStorageProvider;
import org.bukkit.World;

import java.sql.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MariaDBStorageProvider implements WorldStorageProvider {

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String tableName;
    private final ExecutorService executor;

    private HikariDataSource dataSource;

    public MariaDBStorageProvider(String host, int port, String database, String username, String password, String tableName) {
        this.jdbcUrl = "jdbc:mariadb://" + host + ":" + port + "/" + database;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
        this.executor = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("world-loader-mariadb");
            return t;
        });
    }

    @Override
    public void initialize() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "world_name VARCHAR(255) PRIMARY KEY," +
                "world_data LONGBLOB NOT NULL," +
                "environment VARCHAR(50) NOT NULL," +
                "seed BIGINT NOT NULL," +
                "generate_structures BOOLEAN NOT NULL," +
                "last_updated BIGINT NOT NULL" +
                ")";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Optional<WorldData>> getWorldData(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM " + tableName + " WHERE world_name = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, worldName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    byte[] worldDataBytes = rs.getBytes("world_data");
                    String envStr = rs.getString("environment");
                    long seed = rs.getLong("seed");
                    boolean generateStructures = rs.getBoolean("generate_structures");

                    World.Environment environment = World.Environment.valueOf(envStr);

                    WorldData worldData = WorldData.builder()
                            .worldName(worldName)
                            .worldData(worldDataBytes)
                            .environment(environment)
                            .seed(seed)
                            .generateStructures(generateStructures)
                            .build();

                    return Optional.of(worldData);
                }

                return Optional.empty();
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> saveWorldData(WorldData worldData) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO " + tableName + 
                    " (world_name, world_data, environment, seed, generate_structures, last_updated)" +
                    " VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE" +
                    " world_data = VALUES(world_data)," +
                    " environment = VALUES(environment)," +
                    " seed = VALUES(seed)," +
                    " generate_structures = VALUES(generate_structures)," +
                    " last_updated = VALUES(last_updated)";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, worldData.getWorldName());
                stmt.setBytes(2, worldData.getWorldData());
                stmt.setString(3, worldData.getEnvironment().name());
                stmt.setLong(4, worldData.getSeed());
                stmt.setBoolean(5, worldData.isGenerateStructures());
                stmt.setLong(6, System.currentTimeMillis());

                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Boolean> exists(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE world_name = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, worldName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }

                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteWorldData(String worldName) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM " + tableName + " WHERE world_name = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, worldName);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, executor);
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        executor.shutdown();
    }
}