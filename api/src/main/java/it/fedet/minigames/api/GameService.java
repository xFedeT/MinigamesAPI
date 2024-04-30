package it.fedet.minigames.api;

import it.fedet.minigames.api.game.Game;

import java.util.Map;

public interface GameService {


    boolean registerGame(Game game);

    boolean unregisterGame(Game game);

    Game getGame(int id);

    Map<Integer, Game> getActiveGames();
}
