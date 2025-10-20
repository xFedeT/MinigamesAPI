package it.fedet.minigames.impl.sumo.game;

import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.GameStatus;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.api.services.IWorldService;
import it.fedet.minigames.api.world.exception.CorruptedWorldException;
import it.fedet.minigames.api.world.exception.NewerFormatException;
import it.fedet.minigames.api.world.exception.UnknownWorldException;
import it.fedet.minigames.api.world.exception.WorldInUseException;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.phase.WaitingPlayerPhase;
import it.fedet.minigames.utils.LoaderUtils;
import it.fedet.minigames.world.nms.world.CraftSlimeWorld;
import it.fedet.minigames.world.service.WorldService;
import it.fedet.minigames.world.storage.StorageType;
import org.bukkit.World;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SumoGame extends Game<Sumo> {

    private World gameWorld;
    private final String worldName = "sumo"; // Nome del mondo template nello storage

    public SumoGame(Sumo game, int gameId) {
        super(game, gameId);

        WorldService worldService = plugin.getMinigamesAPI().getService(WorldService.class);

        try {
            worldService.loadWorld(LoaderUtils.getLoader(StorageType.MONGODB), worldName, CraftSlimeWorld.SlimeProperties.builder().build())
                    .thenComposeAsync(slimeWorld -> worldService.generateWorld(slimeWorld)
                            .thenAccept(world -> {
                                this.gameWorld = world;
                                setGameStatus(GameStatus.WAITING);
                                plugin.getLogger().info("Game #" + getId() + " initialized with world: " + world.getName());
                            })
                            .exceptionally(e -> {
                                plugin.getLogger().severe("Failed to generate world " + worldName + ": " + e.getMessage());
                                e.printStackTrace();
                                return null;
                            })).exceptionally(e -> {
                        plugin.getLogger().severe("Failed to load world " + worldName + ": " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    });

        } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException |
                 WorldInUseException e) {
            throw new RuntimeException(e);
        }
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

