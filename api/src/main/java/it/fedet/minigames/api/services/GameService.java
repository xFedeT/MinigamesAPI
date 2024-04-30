package it.fedet.minigames.api.services;

import it.fedet.minigames.api.game.Game;
import org.bukkit.entity.Player;

import java.util.Map;

public interface GameService extends Service {


    boolean registerGame(Game game);

    boolean unregisterGame(Game game);

    Game getGameBy(int id);

    Map<Integer, Game> getActiveGames();

    Game getGameBy(Player player);
}
