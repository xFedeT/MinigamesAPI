package it.fedet.minigames.impl.sumo.game.phase;

import it.fedet.minigames.api.board.GameBoard;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.listener.GameListener;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.SumoGame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;

public class PlayingPlayerPhase extends MinigamePhase<Sumo> {

    public PlayingPlayerPhase(SumoGame game) {
        super(game);
    }

    @Override
    public MinigamePhase<Sumo> nextPhase() {
        return new EndingPlayerPhase((SumoGame) game);
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
        return new GameListener[]{
                new GameListener<AsyncPlayerPreLoginEvent>() {
                    @Override
                    public Class<AsyncPlayerPreLoginEvent> getEventClass() {
                        return AsyncPlayerPreLoginEvent.class;
                    }

                    @Override
                    public void apply(AsyncPlayerPreLoginEvent event) {

                    }
                }
        };
    }
}
