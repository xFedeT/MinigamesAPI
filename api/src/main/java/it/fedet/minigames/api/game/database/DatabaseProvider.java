package it.fedet.minigames.api.game.database;

import com.zaxxer.hikari.HikariDataSource;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.function.ThrowableConsumer;
import it.fedet.minigames.api.function.ThrowableFunction;
import it.fedet.minigames.api.game.database.loader.UserDataLoader;
import it.fedet.minigames.api.loadit.UserData;
import it.fedet.minigames.api.loadit.impl.Loadit;
import it.fedet.minigames.api.logging.Logging;
import it.fedet.minigames.api.services.DatabaseService;
import org.bukkit.plugin.Plugin;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class DatabaseProvider<U extends UserData> implements DatabaseService {


    ExecutorService executor = Executors.newFixedThreadPool(setThreadCount());

    private final MinigamesAPI plugin;
    private final HikariDataSource dataSource = new HikariDataSource();
    private Loadit<U> loadit;

    protected DatabaseProvider(MinigamesAPI plugin) {
        this.plugin = plugin;
    }

    public Loadit<U> getPlayerDataLoadit() {
        return loadit;
    }

    public abstract UserDataLoader<U> getUserDataLoader();

    public abstract String getJdbcURL();

    public abstract String getUser();

    public abstract String getPassword();

    public abstract void prepareTables();

    public abstract void runQuerys();

    public abstract void createPlayer(String name);

    public abstract boolean existsPlayer(String name);

    public abstract Optional<U> retrievePlayer(UUID name);

    public abstract Optional<U> retrievePlayer(String name);

    @Override
    public void start() {
        Logging.info(DatabaseService.class, "Starting database service...");
        dataSource.setJdbcUrl(getJdbcURL());
        dataSource.setUsername(getUser());
        if (!getPassword().isEmpty()) {
            dataSource.setPassword(getPassword());
        }

        loadit = Loadit.createInstance((Plugin) plugin, getUserDataLoader());

        Logging.info(DatabaseService.class, "Attempting to connect to the database...");
        try (Connection connection = getConnection()) {
        } catch (SQLException e) {
            e.printStackTrace();
            Logging.error(DatabaseService.class, "Failed to connect to the database!");
            return;
        }
        Logging.info(DatabaseService.class, "Connected to the database!");
    }

    @Override
    public void stop() {
        dataSource.close();
    }

    public abstract int setThreadCount();

    private Connection getConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Logging.info(DatabaseService.class, "Successfully connected to the database!");
            return connection;
        }
    }

    public <T> CompletableFuture<T> executeAsyncQuery(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement, Consumer<SQLException> exception, ThrowableFunction<ResultSet, T, SQLException> resultSet) {
        return supplyAsync(() -> {
            try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
                statement.accept(tempStatement);

                ResultSet tempResultSet = tempStatement.executeQuery();

                return resultSet.apply(tempResultSet);
            } catch (SQLException e) {
                if (exception != null) {
                    exception.accept(e);
                    return null;
                }

                e.printStackTrace();
                return null;
            }
        }, executor);
    }

    public <T> CompletableFuture<T> executeAsyncQuery(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement, ThrowableFunction<ResultSet, T, SQLException> resultSet) {
        return supplyAsync(() -> {
            try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
                statement.accept(tempStatement);
                ResultSet tempResultSet = tempStatement.executeQuery();

                return resultSet.apply(tempResultSet);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }, executor);
    }

    public <T> CompletableFuture<T> executeAsyncQuery(@Language("SQL") String sql, ThrowableFunction<ResultSet, T, SQLException> resultSet) {
        return supplyAsync(() -> {
            try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
                ResultSet tempResultSet = tempStatement.executeQuery();

                return resultSet.apply(tempResultSet);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }, executor);
    }

    public void executeAsyncQuery(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement) {
        runAsync(() -> {
            try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
                statement.accept(tempStatement);
                tempStatement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, executor);
    }


    public <T> T executeQuery(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement, Consumer<SQLException> exception, ThrowableFunction<ResultSet, T, SQLException> resultSet) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            statement.accept(tempStatement);
            ResultSet tempResultSet = tempStatement.executeQuery();

            return resultSet.apply(tempResultSet);
        } catch (SQLException e) {
            if (exception != null) {
                exception.accept(e);
            }
        }

        return null;
    }

    public <T> T executeQuery(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement, ThrowableFunction<ResultSet, T, SQLException> resultSet) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            statement.accept(tempStatement);
            ResultSet tempResultSet = tempStatement.executeQuery();

            return resultSet.apply(tempResultSet);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public void executeQuery(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            statement.accept(tempStatement);
            ResultSet tempResultSet = tempStatement.executeQuery();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public <T> T executeQuery(@Language("SQL") String sql, ThrowableFunction<ResultSet, T, SQLException> resultSet) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            ResultSet tempResultSet = tempStatement.executeQuery();

            return resultSet.apply(tempResultSet);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public void executeQuery(@Language("SQL") String sql) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            tempStatement.executeQuery();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void executeAsyncUpdate(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement, Consumer<SQLException> exception, Consumer<Boolean> executed) {
        runAsync(() -> {
            try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
                statement.accept(tempStatement);
                executed.accept(tempStatement.executeUpdate() != 0);
            } catch (SQLException e) {
                if (exception != null) {
                    exception.accept(e);
                }
            }
        }, executor);
    }

    public void executeAsyncUpdate(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement, Consumer<Boolean> executed) {
        runAsync(() -> {
            try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
                statement.accept(tempStatement);
                executed.accept(tempStatement.executeUpdate() != 0);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, executor);
    }

    public void executeAsyncUpdate(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement) {
        runAsync(() -> {
            try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
                statement.accept(tempStatement);
                tempStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, executor);
    }

    public void executeAsyncUpdate(@Language("SQL") String sql) {
        runAsync(() -> {
            try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
                tempStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, executor);
    }


    public void executeUpdate(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement, Consumer<SQLException> exception, Consumer<Boolean> executed) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            statement.accept(tempStatement);
            executed.accept(tempStatement.executeUpdate() != 0);
        } catch (SQLException e) {
            if (exception != null) {
                exception.accept(e);
            }
        }
    }

    public void executeUpdate(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement, Consumer<Boolean> executed) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            statement.accept(tempStatement);
            executed.accept(tempStatement.executeUpdate() != 0);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void executeUpdate(@Language("SQL") String sql, ThrowableConsumer<PreparedStatement, SQLException> statement) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            statement.accept(tempStatement);
            tempStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


    public void executeUpdate(@Language("SQL") String sql) {
        try (PreparedStatement tempStatement = getConnection().prepareStatement(sql)) {
            tempStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
