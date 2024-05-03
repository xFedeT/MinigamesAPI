package it.fedet.minigames.api.game.team;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public interface TeamService {
    <T extends JavaPlugin & Minigame<T>> void addIntoATeam(Player player, Game<T> game);

    GameTeam getTeam(int id);

    Map<Integer, GameTeam> getTeams();
}
