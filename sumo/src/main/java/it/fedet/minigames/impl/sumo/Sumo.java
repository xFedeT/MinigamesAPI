package it.fedet.minigames.impl.sumo;

import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.provider.MinigamesProvider;
import it.fedet.minigames.api.services.GameService;
import it.fedet.minigames.impl.sumo.game.SumoGame;
import org.bukkit.plugin.java.JavaPlugin;

public class Sumo extends JavaPlugin {

    private MinigamesAPI minigamesAPI;
    private GameService gameService;

    @Override
    public void onEnable() {
        minigamesAPI = MinigamesProvider.get();
        gameService = minigamesAPI.getService(GameService.class);


        for (int i = 0; i < 10; i++) {
            gameService.registerGame(new SumoGame(this));
        }
    }

    @Override
    public void onDisable() {

    }

    public MinigamesAPI getMinigamesAPI() {
        return minigamesAPI;
    }
}
