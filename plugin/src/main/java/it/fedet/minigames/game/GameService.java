package it.fedet.minigames.game;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.team.TeamManager;
import it.fedet.minigames.api.services.IGameService;
import it.fedet.minigames.team.TeamService;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.painting.PaintingEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameService implements IGameService, Listener {

    private final MinigamesCore plugin;
    private final Map<Integer, Game<?>> activeGames = new ConcurrentHashMap<>();
    private final TeamManager teamManager;
    private final Thread gameThread = new Thread(() -> activeGames.forEach((id, game) -> game.tick()));

    public GameService(MinigamesCore plugin) {
        this.plugin = plugin;

        this.teamManager = new TeamService(
                plugin,
                plugin.getMinigame().registerTeamProvider()
        );
    }

    @Override
    public TeamManager getTeamManager() {
        return teamManager;
    }

    @Override
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(new GameListener(this), plugin);
        gameThread.start();
    }

    @Override
    public void stop() {
        gameThread.interrupt();
        activeGames.values().forEach(Game::end);
    }

    @Override
    public void registerGame(Game<?> game) {
        int id = game.getId();
        activeGames.put(id, game);

        teamManager.initializeTeams(game);
        game.start();
    }

    @Override
    public boolean unregisterGame(Game<?> game) {
        activeGames.remove(game.getId());
        return true;
    }

    @Override
    public Game<?> getGameBy(int id) {
        return activeGames.get(id);
    }

    @Override
    public Map<Integer, Game<?>> getActiveGames() {
        return new HashMap<>(activeGames);
    }

    @Override
    public Game<?> getGameBy(Player player) {
        List<MetadataValue> values = player.getMetadata("game-id");
        if (values.isEmpty())
            return null;

        return activeGames.get(values.get(0).asInt());
    }

    @Override
    public Game<?> getGameBy(World world) {
        if (!world.getName().startsWith("game_")) return null;

        try {
            String[] worldSplit = world.getName().split("_");
            return getGameBy(Integer.parseInt(worldSplit[1]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /* -------------------------
       Generic dispatcher helper
       ------------------------- */
    protected void dispatchToGame(Event event) {
        Game<?> game = resolveGameFromEvent(event);

        // Final null-check and dispatch
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    /**
     * Risolve il gioco da un evento coprendo tutte le categorie di eventi di Bukkit/Spigot
     */
    private Game<?> resolveGameFromEvent(Event event) {
        // Player events
        if (event instanceof PlayerEvent) {
            return getGameBy(((PlayerEvent) event).getPlayer());
        }

        // Entity events
        if (event instanceof EntityEvent) {
            Entity entity = ((EntityEvent) event).getEntity();
            if (entity instanceof Player) {
                return getGameBy((Player) entity);
            }
            if (entity != null && entity.getWorld() != null) {
                return getGameBy(entity.getWorld());
            }
        }

        // Block events
        if (event instanceof BlockEvent) {
            Block block = ((BlockEvent) event).getBlock();
            if (block != null && block.getWorld() != null) {
                return getGameBy(block.getWorld());
            }
        }

        // Inventory events
        if (event instanceof InventoryEvent) {
            try {
                InventoryView view = ((InventoryEvent) event).getView();
                if (view != null && view.getPlayer() instanceof Player) {
                    return getGameBy((Player) view.getPlayer());
                }
            } catch (Throwable ignored) {}
        }

        // World events
        if (event instanceof WorldEvent) {
            World world = ((WorldEvent) event).getWorld();
            if (world != null) {
                return getGameBy(world);
            }
        }

        // Chunk events
        if (event instanceof ChunkEvent) {
            Chunk chunk = ((ChunkEvent) event).getChunk();
            if (chunk != null && chunk.getWorld() != null) {
                return getGameBy(chunk.getWorld());
            }
        }

        // Weather events
        if (event instanceof WeatherEvent) {
            World world = ((WeatherEvent) event).getWorld();
            if (world != null) {
                return getGameBy(world);
            }
        }

        // Vehicle events
        if (event instanceof VehicleEvent) {
            Vehicle vehicle = ((VehicleEvent) event).getVehicle();
            if (vehicle != null && vehicle.getWorld() != null) {
                return getGameBy(vehicle.getWorld());
            }
        }

        // Hanging events (includes PaintingEvent)
        if (event instanceof HangingEvent) {
            Entity entity = ((HangingEvent) event).getEntity();
            if (entity != null && entity.getWorld() != null) {
                return getGameBy(entity.getWorld());
            }
        }

        // Painting events (specific case of Hanging)
        if (event instanceof PaintingEvent) {
            Entity entity = ((PaintingEvent) event).getPainting().getVehicle();
            if (entity != null && entity.getWorld() != null) {
                return getGameBy(entity.getWorld());
            }
        }

        return null;
    }

}
