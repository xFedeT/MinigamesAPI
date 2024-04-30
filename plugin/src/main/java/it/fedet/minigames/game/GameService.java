package it.fedet.minigames.game;

import it.fedet.minigames.api.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameService implements it.fedet.minigames.api.GameService {

    private final Map<Integer, Game> activeGames = new ConcurrentHashMap<>();

    private final Thread gameThread = new Thread(() -> activeGames.forEach((id, game) -> game.tick()));

    public GameService() {
        gameThread.start();
    }


    @Override
    public boolean registerGame(Game game) {
        activeGames.put(1, game);
        game.setId(1);

        return true;
    }

    @Override
    public boolean unregisterGame(Game game) {
        activeGames.remove(game.getId());
        return true;
    }

    @Override
    public Game getGame(int id) {
        return activeGames.get(id);
    }

    @Override
    public Map<Integer, Game> getActiveGames() {
        return new HashMap<>(activeGames);
    }

}
