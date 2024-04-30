package it.fedet.minigames.api.game;

import it.fedet.minigames.api.game.phase.MinigamePhase;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Game<T extends JavaPlugin> {

    private int gameId;

    public MinigamePhase<T> currentPhase = initialPhase();

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

    public void setId(int value) {
        gameId = value;
    }

    public abstract MinigamePhase<T> initialPhase();

}
