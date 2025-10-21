package it.fedet.minigames.impl.sumo.listener;

import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.services.TeamService;
import it.fedet.minigames.events.PlayerGameJoinEvent;
import it.fedet.minigames.game.GameService;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.SumoGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class BaseListener implements Listener {

    private final Sumo plugin;

    public BaseListener(Sumo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TeamService teamService = plugin.getGameService().getTeamService();
        for (Game<?> game : plugin.getMinigamesAPI().getService(GameService.class).getActiveGames().values()) {
            if (teamService.addIntoATeam(event.getPlayer(), game)) {
                event.getPlayer().setMetadata("game-id", new FixedMetadataValue(plugin, game.getId()));
                Bukkit.getPluginManager().callEvent(new PlayerGameJoinEvent(event.getPlayer(), game));
                break;
            }
        }
    }


}
