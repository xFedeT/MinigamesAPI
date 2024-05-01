package it.fedet.minigames.api.game.phase;

import it.fedet.minigames.api.board.GameBoard;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.listener.GameListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class MinigamePhase<T extends JavaPlugin> {

    public final MinigamePhase<T> nextPhase = nextPhase();
    private final ArrayList<GameListener<? extends Event>> listeners = new ArrayList<>();

    protected final Game<T> game;

    public MinigamePhase(Game<T> game) {
        this.game = game;
    }

    public MinigamePhase<T> getNextPhase() {
        return nextPhase;
    }

    public abstract @Nullable MinigamePhase<T> nextPhase();
    public abstract void tick();

    public void start() {
        listeners.addAll(Arrays.asList(registerListeners()));
        listeners.sort((Comparator.comparingInt(o -> o.getPriority().getSlot())));
    }

    public void end() {
        listeners.clear();
    }

    public void applyEvent(Event event) {
        List<GameListener<?>> listenersCopy = new ArrayList<>(listeners);
        for (GameListener<?> gameListener : listenersCopy) {
            gameListener.onEvent(event);
        }
    }

    public abstract GameBoard getScoreboard(Player player);

    public abstract GameListener<?>[] registerListeners();
}
