package it.fedet.minigames.api;

import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.gui.GameGui;
import it.fedet.minigames.api.services.GameService;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public interface Minigame {
    MinigamesAPI getMinigamesAPI();

    DatabaseProvider getDatabaseService();

    GameService getGameService();

    List<MinigameConfig> getSettings();

    Map<Class<? extends GameGui>, GameGui> getGuis();
}
