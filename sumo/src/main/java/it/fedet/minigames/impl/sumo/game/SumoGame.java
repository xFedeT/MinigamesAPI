package it.fedet.minigames.impl.sumo.game;

import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.GameStatus;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.api.services.IWorldService;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.phase.WaitingPlayerPhase;
import it.fedet.minigames.world.service.WorldService;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class SumoGame extends Game<Sumo> {

    private World gameWorld;
    private final String worldName = "sumo"; // Nome del mondo template nello storage

    public SumoGame(Sumo game, int gameId) {
        super(game, gameId);

        WorldService worldService = plugin.getMinigamesAPI().getService(WorldService.class);

        worldService.loadWorldForGame(worldName, getId())
                .thenAccept(world -> {
                    this.gameWorld = world;
                    setGameStatus(GameStatus.WAITING);
                    plugin.getLogger().info("Game #" + getId() + " initialized with world: " + world.getName());
                })
                .exceptionally(e -> {
                    plugin.getLogger().severe("Failed to initialize game #" + getId() + ": " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    @Override
    public MinigamePhase<Sumo> initialPhase() {
        return new WaitingPlayerPhase(this);
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public void setGameWorld(World gameWorld) {
        this.gameWorld = gameWorld;
    }
}

