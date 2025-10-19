package it.fedet.minigames.impl.sumo.game.phase;

import it.fedet.minigames.api.board.GameBoard;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.listener.GameListener;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.game.GameService;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.SumoGame;
import it.fedet.minigames.world.service.WorldService;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EndingPlayerPhase extends MinigamePhase<Sumo> {

    public EndingPlayerPhase(SumoGame game) {
        super(game);

        unloadWorld();
        game.getPlugin().getMinigamesAPI().getService(GameService.class).unregisterGame(game);
    }

    @Override
    public @Nullable MinigamePhase<Sumo> nextPhase() {
        return null;
    }

    @Override
    public void tick() {

    }

    /**
     * Termina il gioco e scarica il mondo
     */
    public CompletableFuture<Void> unloadWorld() {
        WorldService worldService = game.getPlugin().getMinigamesAPI().getService(WorldService.class);

        World gameWorld = ((SumoGame) game).getGameWorld();

        if (gameWorld == null) {
            return CompletableFuture.completedFuture(null);
        }

        return worldService.unloadWorld(gameWorld)
                .thenRun(() -> {
                    ((SumoGame) game).setGameWorld(null);
                    game.getPlugin().getLogger().info("Game #" + game.getId() + " terminated and world unloaded");
                })
                .exceptionally(e -> {
                    game.getPlugin().getLogger().severe("Failed to terminate game #" + game.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
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
                        CompletableFuture<Player> player = game.getPlugin().getDatabaseService().getPlayerData(event.getName());
                    }
                }
        };
    }
}
