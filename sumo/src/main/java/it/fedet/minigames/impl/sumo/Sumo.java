package it.fedet.minigames.impl.sumo;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.provider.MinigamesProvider;
import it.fedet.minigames.api.services.GameService;
import it.fedet.minigames.impl.sumo.config.ConfigFile;
import it.fedet.minigames.impl.sumo.config.LanguageFile;
import it.fedet.minigames.impl.sumo.database.Database;
import it.fedet.minigames.impl.sumo.game.SumoGame;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Sumo extends JavaPlugin implements Minigame {

    private MinigamesAPI minigamesAPI;
    private GameService gameService;
    private Database databaseService;

    @Override
    public void onEnable() {
        minigamesAPI = MinigamesProvider.get();
        minigamesAPI.registerMinigame(this);
        minigamesAPI.getSettings(ConfigFile.class).getProperty(ConfigFile.SCRITTA);

        this.databaseService = new Database(minigamesAPI);

        minigamesAPI.registerDatabaseProvider(databaseService);

        gameService = minigamesAPI.getService(GameService.class);


        for (int i = 0; i < 10; i++) {
            gameService.registerGame(new SumoGame(this));
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public MinigamesAPI getMinigamesAPI() {
        return minigamesAPI;
    }

    @Override
    public Database getDatabaseService() {
        return databaseService;
    }

    @Override
    public GameService getGameService() {
        return gameService;
    }


    public List<MinigameConfig> getSettings() {
        return List.of(
            new ConfigFile(),
            new LanguageFile()
        );
    }
}
