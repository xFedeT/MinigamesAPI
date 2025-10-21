package it.fedet.minigames.api.services;

import it.fedet.minigames.api.game.Game;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;

public interface IGameService extends Service {


    TeamService getTeamService();

    void registerGame(Game<?> game);

    boolean unregisterGame(Game<?> game);

    Game<?> getGameBy(int id);

    Map<Integer, Game<?>> getActiveGames();

    Game<?> getGameBy(Player player);

    Game<?> getGameBy(World world);
}
