package it.fedet.minigames.game;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.services.IGameService;
import it.fedet.minigames.team.TeamService;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.painting.PaintingEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.server.ServerEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameService implements IGameService, Listener {

    private MinigamesCore plugin;

    private final Map<Integer, Game<?>> activeGames = new ConcurrentHashMap<>();

    private final TeamService teamService;

    private final Thread gameThread = new Thread(() -> activeGames.forEach((id, game) -> game.tick()));

    public GameService(MinigamesCore plugin) {
        this.plugin = plugin;

        teamService = new TeamService(
                plugin,
                plugin.getMinigame().registerTeamProvider().getMaxPlayerPerTeams(),
                plugin.getMinigame().registerTeamProvider().getCriterias()
        );
    }

    @Override
    public void start() {
        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> {
            if (event instanceof EntityEvent) {
                onEntityEvent((EntityEvent) event);
            } else if (event instanceof PlayerEvent) {
                onPlayerEvent((PlayerEvent) event);
            } else if (event instanceof EnchantItemEvent) {
                onEnchantmentEvent((EnchantItemEvent) event);
            } else if (event instanceof InventoryEvent) {
                onInventoryEvent((InventoryEvent) event);
            } else if (event instanceof WorldEvent) {
                onWorldEvent((WorldEvent) event);
            } else if (event instanceof BlockEvent) {
                onBlockEvent((BlockEvent) event);
            } else if (event instanceof VehicleEvent) {
                onVehicleEvent((VehicleEvent) event);
            } else if (event instanceof HangingEvent) {
                onHangingEvent((HangingEvent) event);
            } else if (event instanceof PaintingEvent) {
                onPaintingEvent((PaintingEvent) event);
            } else if (event instanceof WeatherEvent) {
                onWeatherEvent((WeatherEvent) event);
            }
        }, EventPriority.HIGHEST, plugin, false);

        for (HandlerList handler : HandlerList.getHandlerLists())
            handler.register(registeredListener);

        gameThread.start();
    }

    @Override
    public void stop() {
        gameThread.interrupt();
    }

    @Override
    public void registerGame(Game<?> game) {
        int id = game.getId();
        activeGames.put(id, game);

        teamService.populateTeams(plugin.getMinigame().registerTeamProvider().teamQuantity(), id);
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

    // Helper method per estrarre il game ID dal nome del mondo
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

    @EventHandler
    private void onEntityEvent(EntityEvent event) {
        Game<?> game = getGameBy(event.getEntity().getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onInventoryEvent(InventoryEvent event) {
        Game<?> game = getGameBy(event.getView().getPlayer().getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onWorldEvent(WorldEvent event) {
        Game<?> game = getGameBy(event.getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onPlayerEvent(PlayerEvent event) {
        Game<?> game = getGameBy(event.getPlayer());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onBlockEvent(BlockEvent event) {
        Game<?> game = getGameBy(event.getBlock().getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onVehicleEvent(VehicleEvent event) {
        Game<?> game = getGameBy(event.getVehicle().getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onHangingEvent(HangingEvent event) {
        Game<?> game = getGameBy(event.getEntity().getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onPaintingEvent(PaintingEvent event) {
        Game<?> game = getGameBy(event.getPainting().getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onWeatherEvent(WeatherEvent event) {
        Game<?> game = getGameBy(event.getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onEnchantmentEvent(EnchantItemEvent event) {
        Game<?> game = getGameBy(event.getView().getPlayer().getWorld());
        if (game == null || !activeGames.containsValue(game)) return;
        game.getCurrentPhase().applyEvent(event);
    }
}