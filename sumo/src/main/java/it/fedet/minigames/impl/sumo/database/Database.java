package it.fedet.minigames.impl.sumo.database;

import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.game.database.loader.UserDataLoader;
import it.fedet.minigames.impl.sumo.player.SumoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

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
        return "FedeT_";
    }

    @Override
    public String getPassword() {
        return "PippoPluto1234";
    }

    @Override
    public void prepareTables() {

    }

    @Override
    public void runQuerys() {

    }

    @Override
    public void createPlayer(String name) {

    }

    @Override
    public boolean existsPlayer(String name) {
        return false;
    }

    @Override
    public Optional<SumoPlayer> retrievePlayer(UUID name) {
        return Optional.empty();
    }

    @Override
    public Optional<SumoPlayer> retrievePlayer(String name) {
        return Optional.empty();
    }

    public Player getPlayerData(String playerName) {
        return executeQuery(
                """
                            SELECT * FROM A;
                        """,
                statement -> statement.setString(1, playerName),
                exception -> exception.printStackTrace(),
                resultSet -> {
                    return Bukkit.getPlayer(resultSet.getString("data"));
                }
        );
    }

    @Override
    public int setThreadCount() {
        return 4;
    }
}
