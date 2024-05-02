package it.fedet.minigames.impl.sumo.game.phase;

import it.fedet.minigames.api.board.GameBoard;
import it.fedet.minigames.api.game.listener.GameListener;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.SumoGame;
import it.fedet.minigames.impl.sumo.guis.ProvaGui;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class WaitingPlayerPhase extends MinigamePhase<Sumo> {

    public WaitingPlayerPhase(SumoGame game) {
        super(game);
    }


    @Override
    public MinigamePhase<Sumo> nextPhase() {
        return new PlayingPlayerPhase(game);
    }

    @Override
    public void tick() {

    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void end() {
        super.end();

        game.next();
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
            new GameListener<PlayerJoinEvent>() {

                @Override
                public Class<PlayerJoinEvent> getEventClass() {
                    return PlayerJoinEvent.class;
                }

                @Override
                public void apply(PlayerJoinEvent event) {
                    event.setJoinMessage("FUNZIONOOOOOOO WAITING PLAYER!");

                    game.getPlugin().getMinigamesAPI().openGui(ProvaGui.class, event.getPlayer());
                }
            }
        };
    }
}
