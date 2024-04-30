package it.fedet.minigames.api.game.phase;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class MinigamePhase<T extends JavaPlugin> {

    public final MinigamePhase<T> nextPhase = nextPhase();

    protected final T plugin;

    public MinigamePhase(T plugin) {
        this.plugin = plugin;
    }

    public MinigamePhase<T> getNextPhase() {
        return nextPhase;
    }

    public abstract MinigamePhase<T> nextPhase();
    public abstract void tick();

    public abstract void start();
    public abstract void end();
}
