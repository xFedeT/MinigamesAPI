package it.fedet.minigames.api.game;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Game<P extends JavaPlugin & Minigame<P>> {

    protected final P plugin;

    private GameStatus gameStatus;

    private final int gameId;

    private MinigamePhase<P> currentPhase = initialPhase();

    public Game(P plugin, int gameId) {
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

    public abstract MinigamePhase<P> initialPhase();

    public P getPlugin() {
        return plugin;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public MinigamePhase<P> getCurrentPhase() {
        return currentPhase;
    }
}
