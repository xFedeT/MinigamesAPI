package it.fedet.minigames.game;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameService implements it.fedet.minigames.api.services.GameService {

    private final Map<Integer, Game> activeGames = new ConcurrentHashMap<>();

    private final Thread gameThread = new Thread(() -> activeGames.forEach((id, game) -> game.tick()));

    public GameService(MinigamesCore plugin) {}

    @Override
    public void start() {
        gameThread.start();
    }

    @Override
    public void stop() {

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
    public Game getGameBy(int id) {
        return activeGames.get(id);
    }

    @Override
    public Map<Integer, Game> getActiveGames() {
        return new HashMap<>(activeGames);
    }

    @Override
    public Game getGameBy(Player player) {
        List<MetadataValue> values = player.getMetadata("game-id");
        if (values.isEmpty())
            return null;

        return activeGames.get(values.get(0).asInt());
    }


}
