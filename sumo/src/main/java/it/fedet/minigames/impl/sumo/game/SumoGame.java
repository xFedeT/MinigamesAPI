package it.fedet.minigames.impl.sumo.game;

import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.GameStatus;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.api.logging.Logging;
import it.fedet.minigames.api.world.database.StorageType;
import it.fedet.minigames.api.world.exceptions.CorruptedWorldException;
import it.fedet.minigames.api.world.exceptions.NewerFormatException;
import it.fedet.minigames.api.world.exceptions.UnknownWorldException;
import it.fedet.minigames.api.world.exceptions.WorldInUseException;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.phase.WaitingPlayerPhase;
import it.fedet.minigames.world.nms.CraftSlimeWorld;
import it.fedet.minigames.world.service.WorldService;
import it.fedet.minigames.world.service.loaders.LoaderUtils;
import org.bukkit.World;

import java.io.IOException;

public class SumoGame extends Game<Sumo> {

    private World gameWorld;

    public SumoGame(Sumo plugin, int gameId) {
        super(plugin, gameId);

        WorldService worldService = plugin.getMinigamesAPI().getService(WorldService.class);

        try {
            worldService.generateWorld(
                            worldService.loadWorld(
                                    LoaderUtils.getLoader(StorageType.MONGODB),
                                    "sumo",
                                    CraftSlimeWorld.SlimeProperties.builder().ignoreLocked(true).readOnly(true).build()
                            ).clone("game_" + gameId)
                    ).thenAccept(world -> {
                        this.gameWorld = world;
                        setGameStatus(GameStatus.WAITING);
                        Logging.infoGame(Sumo.class, "Game #" + getId() + " initialized with world: " + world.getName());
                    })
                    .exceptionally(e -> {
                        Logging.errorGame(Sumo.class, "Failed to generate world sumo: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    });

        } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException |
                 WorldInUseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        getCurrentPhase().startPhase();
    }

    @Override
    public void end() {

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

