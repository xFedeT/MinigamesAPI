package it.fedet.minigames.api;

import it.fedet.minigames.api.commands.GameCommand;
import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.game.team.provider.TeamProvider;
import it.fedet.minigames.api.gui.GameGui;
import it.fedet.minigames.api.items.GameInventory;
import it.fedet.minigames.api.services.IGameService;

import java.util.List;
import java.util.Map;

public interface Minigame<P extends Minigame<P>> {
    MinigamesAPI getMinigamesAPI();

    List<MinigameConfig> registerConfigs();

    Map<Class<? extends GameGui<P>>, GameGui<P>> registerGuis();

    Map<Class<? extends GameInventory>, GameInventory> registerInventorys();

    Map<Class<? extends GameCommand>, GameCommand> registerCommands();

    TeamProvider registerTeamProvider();
}
