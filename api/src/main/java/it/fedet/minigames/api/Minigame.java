package it.fedet.minigames.api;

import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.gui.GameGui;
import it.fedet.minigames.api.items.GameInventory;
import it.fedet.minigames.api.services.GameService;

import java.util.List;
import java.util.Map;

public interface Minigame<T extends Minigame<T>> {
    MinigamesAPI getMinigamesAPI();

    DatabaseProvider getDatabaseService();

    GameService getGameService();

    List<MinigameConfig> registerConfigs();

    Map<Class<? extends GameGui<T>>, GameGui<T>> registerGuis();

    Map<Class<? extends GameInventory>, GameInventory> registerInventorys();

}
