//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.service.loaders.mysql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.fedet.minigames.api.logging.Logging;
import it.fedet.minigames.api.world.database.WorldDbProvider;
import it.fedet.minigames.api.world.exceptions.UnknownWorldException;
import it.fedet.minigames.api.world.exceptions.WorldInUseException;
import it.fedet.minigames.world.service.WorldService;
import it.fedet.minigames.world.service.loaders.UpdatableLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MysqlLoader extends UpdatableLoader {
    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(2, (new ThreadFactoryBuilder()).setNameFormat("SWM MySQL Lock Pool Thread #%1$d").build());
    private static final int CURRENT_DB_VERSION = 1;
    private static final String CREATE_VERSIONING_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `database_version` (`id` INT NOT NULL AUTO_INCREMENT, `version` INT(11), PRIMARY KEY(id));";
    private static final String INSERT_VERSION_QUERY = "INSERT INTO `database_version` (`id`, `version`) VALUES (1, ?) ON DUPLICATE KEY UPDATE `id` = ?;";
    private static final String GET_VERSION_QUERY = "SELECT `version` FROM `database_version` WHERE `id` = 1;";
    private static final String ALTER_LOCKED_COLUMN_QUERY = "ALTER TABLE `worlds` CHANGE COLUMN `locked` `locked` BIGINT NOT NULL DEFAULT 0;";
    private static final String CREATE_WORLDS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `worlds` (`id` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(255) UNIQUE, `world` MEDIUMBLOB, `locked` BIGINT, PRIMARY KEY(id));";
    private static final String SELECT_WORLD_QUERY = "SELECT `world`, `locked` FROM `worlds` WHERE `name` = ?;";
    private static final String UPDATE_WORLD_QUERY = "INSERT INTO `worlds` (`name`, `world`, `locked`) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE `world` = ?;";
    private static final String UPDATE_LOCK_QUERY = "UPDATE `worlds` SET `locked` = ? WHERE `name` = ?;";
    private static final String DELETE_WORLD_QUERY = "DELETE FROM `worlds` WHERE `name` = ?;";
    private static final String LIST_WORLDS_QUERY = "SELECT `name` FROM `worlds`;";
    private final Map<String, ScheduledFuture> lockedWorlds = new HashMap();
    private final HikariDataSource source;

    public MysqlLoader(WorldDbProvider provider) throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(provider.getConnectionOrHostString());
        hikariConfig.setUsername(provider.getUsername());
        hikariConfig.setPassword(provider.getPassword());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        this.source = new HikariDataSource(hikariConfig);
        Connection con = this.source.getConnection();

        try {
            PreparedStatement statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `worlds` (`id` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(255) UNIQUE, `world` MEDIUMBLOB, `locked` BIGINT, PRIMARY KEY(id));");

            try {
                statement.execute();
            } catch (Throwable var11) {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Throwable var9) {
                        var11.addSuppressed(var9);
                    }
                }

                throw var11;
            }

            if (statement != null) {
                statement.close();
            }

            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `database_version` (`id` INT NOT NULL AUTO_INCREMENT, `version` INT(11), PRIMARY KEY(id));");

            try {
                statement.execute();
            } catch (Throwable var10) {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Throwable var8) {
                        var10.addSuppressed(var8);
                    }
                }

                throw var10;
            }

            if (statement != null) {
                statement.close();
            }
        } catch (Throwable var12) {
            if (con != null) {
                try {
                    con.close();
                } catch (Throwable var7) {
                    var12.addSuppressed(var7);
                }
            }

            throw var12;
        }

        if (con != null) {
            con.close();
        }

    }

    public void update() throws IOException, UpdatableLoader.NewerDatabaseException {
        try {
            Connection con = this.source.getConnection();

            try {
                PreparedStatement statement = con.prepareStatement("SELECT `version` FROM `database_version` WHERE `id` = 1;");

                int version;
                try {
                    ResultSet set = statement.executeQuery();

                    try {
                        version = set.next() ? set.getInt(1) : -1;
                    } catch (Throwable var15) {
                        if (set != null) {
                            try {
                                set.close();
                            } catch (Throwable var11) {
                                var15.addSuppressed(var11);
                            }
                        }

                        throw var15;
                    }

                    if (set != null) {
                        set.close();
                    }
                } catch (Throwable var16) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var10) {
                            var16.addSuppressed(var10);
                        }
                    }

                    throw var16;
                }

                if (statement != null) {
                    statement.close();
                }

                if (version > 1) {
                    throw new UpdatableLoader.NewerDatabaseException(1, version);
                }

                if (version < 1) {
                    Logging.warning(WorldService.class, "Your SWM MySQL database is outdated. The update process will start in 10 seconds.");
                    Logging.warning(WorldService.class, "Note that this update might make your database incompatible with older SWM versions.");
                    Logging.warning(WorldService.class, "Make sure no other servers with older SWM versions are using this database.");
                    Logging.warning(WorldService.class, "Shut down the server to prevent your database from being updated.");

                    try {
                        Thread.sleep(10000L);
                    } catch (InterruptedException var12) {
                    }

                    statement = con.prepareStatement("ALTER TABLE `worlds` CHANGE COLUMN `locked` `locked` BIGINT NOT NULL DEFAULT 0;");

                    try {
                        statement.executeUpdate();
                    } catch (Throwable var14) {
                        if (statement != null) {
                            try {
                                statement.close();
                            } catch (Throwable var9) {
                                var14.addSuppressed(var9);
                            }
                        }

                        throw var14;
                    }

                    if (statement != null) {
                        statement.close();
                    }

                    statement = con.prepareStatement("INSERT INTO `database_version` (`id`, `version`) VALUES (1, ?) ON DUPLICATE KEY UPDATE `id` = ?;");

                    try {
                        statement.setInt(1, 1);
                        statement.setInt(2, 1);
                        statement.executeUpdate();
                    } catch (Throwable var13) {
                        if (statement != null) {
                            try {
                                statement.close();
                            } catch (Throwable var8) {
                                var13.addSuppressed(var8);
                            }
                        }

                        throw var13;
                    }

                    if (statement != null) {
                        statement.close();
                    }
                }
            } catch (Throwable var17) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Throwable var7) {
                        var17.addSuppressed(var7);
                    }
                }

                throw var17;
            }

            if (con != null) {
                con.close();
            }

        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    public byte[] loadWorld(String worldName, boolean readOnly) throws UnknownWorldException, IOException, WorldInUseException {
        return this.loadWorld(worldName, readOnly, false);
    }

    public byte[] loadWorld(String worldName, boolean readOnly, boolean ignoreLocked) throws UnknownWorldException, WorldInUseException, IOException {
        try {
            Connection con = this.source.getConnection();

            byte[] var14;
            try {
                PreparedStatement statement = con.prepareStatement("SELECT `world`, `locked` FROM `worlds` WHERE `name` = ?;");

                try {
                    statement.setString(1, worldName);
                    ResultSet set = statement.executeQuery();
                    if (!set.next()) {
                        throw new UnknownWorldException(worldName);
                    }

                    if (!readOnly) {
                        long lockedMillis = ignoreLocked ? 0L : set.getLong("locked");
                        if (System.currentTimeMillis() - lockedMillis <= 300000L) {
                            throw new WorldInUseException(worldName);
                        }

                        this.updateLock(worldName, true);
                    }

                    var14 = set.getBytes("world");
                } catch (Throwable var11) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var10) {
                            var11.addSuppressed(var10);
                        }
                    }

                    throw var11;
                }

                if (statement != null) {
                    statement.close();
                }
            } catch (Throwable var12) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Throwable var9) {
                        var12.addSuppressed(var9);
                    }
                }

                throw var12;
            }

            if (con != null) {
                con.close();
            }

            return var14;
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    private void updateLock(String worldName, boolean forceSchedule) {
        try {
            Connection con = this.source.getConnection();

            try {
                PreparedStatement statement = con.prepareStatement("UPDATE `worlds` SET `locked` = ? WHERE `name` = ?;");

                try {
                    statement.setLong(1, System.currentTimeMillis());
                    statement.setString(2, worldName);
                    statement.executeUpdate();
                } catch (Throwable var9) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var8) {
                            var9.addSuppressed(var8);
                        }
                    }

                    throw var9;
                }

                if (statement != null) {
                    statement.close();
                }
            } catch (Throwable var10) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                    }
                }

                throw var10;
            }

            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            Logging.error(WorldService.class, "Failed to update the lock for world " + worldName + ":");
            ex.printStackTrace();
        }

        if (forceSchedule || this.lockedWorlds.containsKey(worldName)) {
            this.lockedWorlds.put(worldName, SERVICE.schedule(() -> this.updateLock(worldName, false), 60000L, TimeUnit.MILLISECONDS));
        }

    }

    public boolean worldExists(String worldName) throws IOException {
        try {
            Connection con = this.source.getConnection();

            boolean var5;
            try {
                PreparedStatement statement = con.prepareStatement("SELECT `world`, `locked` FROM `worlds` WHERE `name` = ?;");

                try {
                    statement.setString(1, worldName);
                    ResultSet set = statement.executeQuery();
                    var5 = set.next();
                } catch (Throwable var8) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var7) {
                            var8.addSuppressed(var7);
                        }
                    }

                    throw var8;
                }

                if (statement != null) {
                    statement.close();
                }
            } catch (Throwable var9) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Throwable var6) {
                        var9.addSuppressed(var6);
                    }
                }

                throw var9;
            }

            if (con != null) {
                con.close();
            }

            return var5;
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    public List<String> listWorlds() throws IOException {
        List<String> worldList = new ArrayList();

        try {
            Connection con = this.source.getConnection();

            try {
                PreparedStatement statement = con.prepareStatement("SELECT `name` FROM `worlds`;");

                try {
                    ResultSet set = statement.executeQuery();

                    while (set.next()) {
                        worldList.add(set.getString("name"));
                    }
                } catch (Throwable var8) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var7) {
                            var8.addSuppressed(var7);
                        }
                    }

                    throw var8;
                }

                if (statement != null) {
                    statement.close();
                }
            } catch (Throwable var9) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Throwable var6) {
                        var9.addSuppressed(var6);
                    }
                }

                throw var9;
            }

            if (con != null) {
                con.close();
            }

            return worldList;
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    public void saveWorld(String worldName, byte[] serializedWorld, boolean lock) throws IOException {
        try {
            Connection con = this.source.getConnection();

            try {
                PreparedStatement statement = con.prepareStatement("INSERT INTO `worlds` (`name`, `world`, `locked`) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE `world` = ?;");

                try {
                    statement.setString(1, worldName);
                    statement.setBytes(2, serializedWorld);
                    statement.setBytes(3, serializedWorld);
                    statement.executeUpdate();
                    if (lock) {
                        this.updateLock(worldName, true);
                    }
                } catch (Throwable var10) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var9) {
                            var10.addSuppressed(var9);
                        }
                    }

                    throw var10;
                }

                if (statement != null) {
                    statement.close();
                }
            } catch (Throwable var11) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Throwable var8) {
                        var11.addSuppressed(var8);
                    }
                }

                throw var11;
            }

            if (con != null) {
                con.close();
            }

        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    public void unlockWorld(String worldName) throws IOException, UnknownWorldException {
        ScheduledFuture future = this.lockedWorlds.remove(worldName);
        if (future != null) {
            future.cancel(false);
        }

        try {
            Connection con = this.source.getConnection();

            try {
                PreparedStatement statement = con.prepareStatement("UPDATE `worlds` SET `locked` = ? WHERE `name` = ?;");

                try {
                    statement.setLong(1, 0L);
                    statement.setString(2, worldName);
                    if (statement.executeUpdate() == 0) {
                        throw new UnknownWorldException(worldName);
                    }
                } catch (Throwable var9) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var8) {
                            var9.addSuppressed(var8);
                        }
                    }

                    throw var9;
                }

                if (statement != null) {
                    statement.close();
                }
            } catch (Throwable var10) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                    }
                }

                throw var10;
            }

            if (con != null) {
                con.close();
            }

        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    public boolean isWorldLocked(String worldName) throws IOException, UnknownWorldException {
        if (this.lockedWorlds.containsKey(worldName)) {
            return true;
        } else {
            try {
                Connection con = this.source.getConnection();

                boolean var5;
                try {
                    PreparedStatement statement = con.prepareStatement("SELECT `world`, `locked` FROM `worlds` WHERE `name` = ?;");

                    try {
                        statement.setString(1, worldName);
                        ResultSet set = statement.executeQuery();
                        if (!set.next()) {
                            throw new UnknownWorldException(worldName);
                        }

                        var5 = System.currentTimeMillis() - set.getLong("locked") <= 300000L;
                    } catch (Throwable var8) {
                        if (statement != null) {
                            try {
                                statement.close();
                            } catch (Throwable var7) {
                                var8.addSuppressed(var7);
                            }
                        }

                        throw var8;
                    }

                    if (statement != null) {
                        statement.close();
                    }
                } catch (Throwable var9) {
                    if (con != null) {
                        try {
                            con.close();
                        } catch (Throwable var6) {
                            var9.addSuppressed(var6);
                        }
                    }

                    throw var9;
                }

                if (con != null) {
                    con.close();
                }

                return var5;
            } catch (SQLException ex) {
                throw new IOException(ex);
            }
        }
    }

    public void deleteWorld(String worldName) throws IOException, UnknownWorldException {
        ScheduledFuture future = this.lockedWorlds.remove(worldName);
        if (future != null) {
            future.cancel(false);
        }

        try {
            Connection con = this.source.getConnection();

            try {
                PreparedStatement statement = con.prepareStatement("DELETE FROM `worlds` WHERE `name` = ?;");

                try {
                    statement.setString(1, worldName);
                    if (statement.executeUpdate() == 0) {
                        throw new UnknownWorldException(worldName);
                    }
                } catch (Throwable var9) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var8) {
                            var9.addSuppressed(var8);
                        }
                    }

                    throw var9;
                }

                if (statement != null) {
                    statement.close();
                }
            } catch (Throwable var10) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                    }
                }

                throw var10;
            }

            if (con != null) {
                con.close();
            }

        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }
}
