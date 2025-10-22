package it.fedet.minigames.impl.sumo.game.phase;

import it.fedet.minigames.api.board.GameBoard;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.listener.GameListener;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.events.PlayerGameJoinEvent;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.SumoGame;
import it.fedet.minigames.impl.sumo.guis.ProvaGui;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;

public class WaitingPlayerPhase extends MinigamePhase<Sumo> {

    public WaitingPlayerPhase(SumoGame game) {
        super(game);
    }


    @Override
    public MinigamePhase<Sumo> nextPhase() {
        return new PlayingPlayerPhase((SumoGame) game);
    }

    @Override
    public void tick() {

    }

    @Override
    public void startPhase() {
        super.startPhase();
    }

    @Override
    public void endPhase() {
        super.endPhase();

        game.next();
    }

    @Override
    public GameBoard getScoreboard(Player player) {
        return new GameBoard() {
            @Override
            public String getTitle() {
                return "§6§lSUMO - Waiting Phase";
            }

            @Override
            public List<String> getLines() {
                return List.of(
                        "§7Players: §a",
                        "§7Waiting for more players...",
                        "§eJoin the fun!"
                );
            }
        };
    }

    @Override
    public GameListener<?>[] registerListeners() {
        return new GameListener[]{
                new GameListener<PlayerGameJoinEvent>() {

                    @Override
                    public Class<PlayerGameJoinEvent> getEventClass() {
                        return PlayerGameJoinEvent.class;
                    }

                    @Override
                    public void apply(PlayerGameJoinEvent event) {
                        event.getPlayer().teleport(new Location(((SumoGame) game).getGameWorld(), 8, 50, 8, 0, 0));
                    }
                }
        };
    }
}
