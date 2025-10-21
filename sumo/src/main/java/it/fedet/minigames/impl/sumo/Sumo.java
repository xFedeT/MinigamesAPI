package it.fedet.minigames.impl.sumo;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.commands.GameCommand;
import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.team.provider.TeamProvider;
import it.fedet.minigames.api.gui.GameGui;
import it.fedet.minigames.api.items.GameInventory;
import it.fedet.minigames.api.provider.MinigamesProvider;
import it.fedet.minigames.api.services.IGameService;
import it.fedet.minigames.game.GameService;
import it.fedet.minigames.impl.sumo.commands.Commands;
import it.fedet.minigames.impl.sumo.config.ConfigFile;
import it.fedet.minigames.impl.sumo.config.LanguageFile;
import it.fedet.minigames.impl.sumo.database.Database;
import it.fedet.minigames.impl.sumo.database.WorldDatabase;
import it.fedet.minigames.impl.sumo.game.SumoGame;
import it.fedet.minigames.impl.sumo.game.team.TeamProviderTest;
import it.fedet.minigames.impl.sumo.guis.ProvaGui;
import it.fedet.minigames.impl.sumo.inventory.ProvaInventory;
import it.fedet.minigames.impl.sumo.listener.BaseListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class Sumo extends JavaPlugin implements Minigame<Sumo> {

    private MinigamesAPI minigamesAPI;
    private IGameService gameService;

    @Override
    public void onEnable() {
        minigamesAPI = MinigamesProvider.get();

        minigamesAPI.registerDatabaseProvider(new Database(minigamesAPI));
        minigamesAPI.registerWorldDbProvider(new WorldDatabase());

        minigamesAPI.registerMinigame(this);

        minigamesAPI.getConfig(ConfigFile.class).getProperty(ConfigFile.SCRITTA);

        gameService = minigamesAPI.getService(GameService.class);

        for (int id = 0; id < 1; id++) {
            gameService.registerGame(new SumoGame(this, id));
        }

        Bukkit.getPluginManager().registerEvents(new BaseListener(this), this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public MinigamesAPI getMinigamesAPI() {
        return minigamesAPI;
    }

    public Database getDatabaseService() {
        return minigamesAPI.getService(Database.class);
    }

    public IGameService getGameService() {
        return gameService;
    }

    @Override
    public List<MinigameConfig> registerConfigs() {
        return List.of(
                new ConfigFile(),
                new LanguageFile()
        );
    }


    @Override
    public Map<Class<? extends GameGui<Sumo>>, GameGui<Sumo>> registerGuis() {
        return Map.of(
                ProvaGui.class, new ProvaGui(this)
        );
    }

    @Override
    public Map<Class<? extends GameInventory>, GameInventory> registerInventorys() {
        return Map.of(
                ProvaInventory.class, new ProvaInventory()
        );
    }

    @Override
    public Map<Class<? extends GameCommand>, GameCommand> registerCommands() {
        return Map.of(
                Commands.class, new Commands()
        );
    }

    @Override
    public TeamProvider registerTeamProvider() {
        return new TeamProviderTest();
    }
}
