package it.fedet.minigames.api.game.phase;

import it.fedet.minigames.api.Minigame;
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

public abstract class MinigamePhase<P extends JavaPlugin & Minigame<P>> {

    public final MinigamePhase<P> nextPhase = nextPhase();
    private final ArrayList<GameListener<? extends Event>> listeners = new ArrayList<>();

    protected final Game<P> game;

    public MinigamePhase(Game<P> game) {
        this.game = game;
    }

    public MinigamePhase<P> getNextPhase() {
        return nextPhase;
    }

    public abstract @Nullable MinigamePhase<P> nextPhase();

    public abstract void tick();

    public void startPhase() {
        listeners.addAll(Arrays.asList(registerListeners()));
        listeners.sort((Comparator.comparingInt(o -> o.getPriority().getSlot())));
    }

    public void endPhase() {
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
