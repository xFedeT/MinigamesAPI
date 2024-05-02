package it.fedet.minigames.api.game;

import it.fedet.minigames.api.game.phase.MinigamePhase;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Game<T extends JavaPlugin> {

    protected final T plugin;
    private GameStatus gameStatus;

    private final int gameId;

    public MinigamePhase<T> currentPhase = initialPhase();

    public Game(T plugin, int gameId) {
        this.plugin = plugin;
        this.gameId = gameId;
    }

    public void tick() {
        currentPhase.tick();
    }

    public void next() {
        if (currentPhase == null)
            return;

        currentPhase.end();

        currentPhase = currentPhase.getNextPhase();
        currentPhase.start();
    }

    public int getId() {
        return gameId;
    }

    public abstract MinigamePhase<T> initialPhase();

    public T getPlugin() {
        return plugin;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public MinigamePhase<T> getCurrentPhase() {
        return currentPhase;
    }
}
