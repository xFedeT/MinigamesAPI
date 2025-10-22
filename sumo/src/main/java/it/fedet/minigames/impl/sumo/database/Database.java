package it.fedet.minigames.impl.sumo.database;

import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.game.database.loader.UserDataLoader;
import it.fedet.minigames.impl.sumo.player.SumoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Database extends DatabaseProvider<SumoPlayer> {

    public Database(MinigamesAPI minigamesAPI) {
        super(minigamesAPI);
    }

    @Override
    public UserDataLoader<SumoPlayer> getUserDataLoader() {
        return new SumoPlayerLoader(this);
    }

    @Override
    public String getJdbcURL() {
        return "jdbc:mysql://localhost:3306/sumo";
    }

    @Override
    public String getUser() {
        return "root";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public void prepareTables() {
        executeUpdate("""
                CREATE TABLE IF NOT EXISTS sumo_players (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    uuid VARCHAR(36) NOT NULL UNIQUE,
                    name VARCHAR(16) NOT NULL,
                    last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
        """);
    }

    @Override
    public void runQuerys() {

    }

    @Override
    public void createPlayer(UUID uuid, String name) {
        executeUpdate(
                "INSERT INTO sumo_players (uuid, name, last_login) VALUES (?, ?, ?);",
                statement -> {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, name);
                    statement.setTimestamp(3, Timestamp.from(Instant.now()));
                }
        );
    }

    @Override
    public boolean existsPlayer(UUID uuid, String name) {
        return executeQuery(
                "SELECT 1 FROM sumo_players WHERE name = ? AND uuid = ? LIMIT 1;",
                statement -> {
                    statement.setString(1, name);
                    statement.setString(2, uuid.toString());
                },
                ResultSet::next
        );
    }

    @Override
    public Optional<SumoPlayer> retrievePlayer(UUID uuid) {
        return executeQuery(
                "SELECT * FROM sumo_players WHERE uuid = ?;",
                statement -> statement.setString(1, uuid.toString()),
                rs -> {
                    if (!rs.next()) return Optional.empty();
                    return Optional.of(new SumoPlayer(uuid, rs.getString("name")));
                }
        );
    }

    @Override
    public Optional<SumoPlayer> retrievePlayer(String name) {
        return executeQuery(
                "SELECT * FROM sumo_players WHERE name = ?;",
                statement -> statement.setString(1, name),
                rs -> {
                    if (!rs.next()) return Optional.empty();
                    return Optional.of(new SumoPlayer(UUID.fromString(rs.getString("uuid")), name));
                }
        );
    }


    @Override
    public int setThreadCount() {
        return 4;
    }
}
