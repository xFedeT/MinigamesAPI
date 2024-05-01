package it.fedet.minigames.api;

import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.services.GameService;

import java.util.List;

public interface Minigame {
    MinigamesAPI getMinigamesAPI();

    DatabaseProvider getDatabaseService();

    GameService getGameService();

    List<MinigameConfig> getSettings();

}
