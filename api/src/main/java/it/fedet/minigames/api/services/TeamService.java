package it.fedet.minigames.api.services;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public interface TeamService {
    <P extends JavaPlugin & Minigame<P>> void addIntoATeam(Player player, Game<P> game);

    GameTeam getTeam(int id);

    Map<Integer, GameTeam> getTeams();
}
