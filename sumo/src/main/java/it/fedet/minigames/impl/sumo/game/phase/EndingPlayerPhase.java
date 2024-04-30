package it.fedet.minigames.impl.sumo.game.phase;

import it.fedet.minigames.api.board.GameBoard;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.listener.GameListener;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.impl.sumo.Sumo;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EndingPlayerPhase extends MinigamePhase<Sumo> {

    public EndingPlayerPhase(Game<Sumo> game) {
        super(game);
    }

    @Override
    public @Nullable MinigamePhase<Sumo> nextPhase() {
        return null;
    }

    @Override
    public void tick() {

    }

    @Override
    public GameBoard getScoreboard(Player player) {
        return new GameBoard() {
            @Override
            public String getTitle() {
                return "";
            }

            @Override
            public List<String> getLines() {
                return List.of();
            }
        };
    }

    @Override
    public GameListener<?>[] registerListeners() {
        return new GameListener[0];
    }
}
