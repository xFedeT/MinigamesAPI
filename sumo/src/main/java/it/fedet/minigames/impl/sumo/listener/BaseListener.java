package it.fedet.minigames.impl.sumo.listener;

import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.team.TeamManager;
import it.fedet.minigames.events.PlayerGameJoinEvent;
import it.fedet.minigames.game.GameService;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.config.ConfigFile;
import it.fedet.minigames.impl.sumo.guis.ProvaGui;
import it.fedet.minigames.impl.sumo.inventory.ProvaInventory;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BaseListener implements Listener {

    private final Sumo plugin;

    public BaseListener(Sumo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TeamManager teamService = plugin.getGameService().getTeamManager();
        for (Game<?> game : plugin.getMinigamesAPI().getService(GameService.class).getActiveGames().values()) {
            if (teamService.assignPlayerToTeam(event.getPlayer(), game)) {
                event.getPlayer().setMetadata("game-id", new FixedMetadataValue(plugin, game.getId()));
                Bukkit.getPluginManager().callEvent(new PlayerGameJoinEvent(event.getPlayer(), game));
                break;
            }
        }

        plugin.getMinigamesAPI().getInventory(ProvaInventory.class).apply(event.getPlayer());
        event.setJoinMessage(plugin.getMinigamesAPI().getConfig(ConfigFile.class).getProperty(ConfigFile.SCRITTA));
    }

}
