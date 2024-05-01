package it.fedet.minigames.game;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameService implements it.fedet.minigames.api.services.GameService, Listener {

    private MinigamesCore plugin;

    private final Map<Integer, Game> activeGames = new ConcurrentHashMap<>();

    private final Thread gameThread = new Thread(() -> activeGames.forEach((id, game) -> game.tick()));

    public GameService(MinigamesCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> {
            if (event instanceof EntityEvent entityEvent) onEntityDamage(entityEvent);
            else if (event instanceof InventoryEvent inventoryEvent) onInventoryEvent(inventoryEvent);
            else if (event instanceof WorldEvent worldEvent) onWorldEvent(worldEvent);
            else if (event instanceof PlayerEvent playerEvent) onPlayerEvent(playerEvent);
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
    public boolean registerGame(Game game) {
        activeGames.put(1, game);
        game.setId(1);

        return true;
    }

    @Override
    public boolean unregisterGame(Game game) {
        activeGames.remove(game.getId());
        return true;
    }

    @Override
    public Game getGameBy(int id) {
        return activeGames.get(id);
    }

    @Override
    public Map<Integer, Game> getActiveGames() {
        return new HashMap<>(activeGames);
    }

    @Override
    public Game getGameBy(Player player) {
        List<MetadataValue> values = player.getMetadata("game-id");
        if (values.isEmpty())
            return null;

        return activeGames.get(values.get(0).asInt());
    }

    @EventHandler
    private void onEntityDamage(EntityEvent event) {
        String worldName = event.getEntity().getWorld().getName();
        if (!worldName.startsWith("game_")) return;

        String[] worldSplit = event.getEntity().getWorld().getName().split("_");
        int gameID = Integer.parseInt(worldSplit[1]);

        if (!activeGames.containsKey(gameID)) return;

        activeGames.get(gameID).getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onInventoryEvent(InventoryEvent event) {
        String worldName = event.getView().getPlayer().getWorld().getName();
        if (!worldName.startsWith("game_")) return;

        String[] worldSplit = event.getView().getPlayer().getWorld().getName().split("_");
        int gameID = Integer.parseInt(worldSplit[1]);

        if (!activeGames.containsKey(gameID)) return;

        activeGames.get(gameID).getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onWorldEvent(WorldEvent event) {
        String worldName = event.getWorld().getName();
        if (!worldName.startsWith("game_")) return;

        String[] worldSplit = event.getWorld().getName().split("_");
        int gameID = Integer.parseInt(worldSplit[1]);

        if (!activeGames.containsKey(gameID)) return;

        activeGames.get(gameID).getCurrentPhase().applyEvent(event);
    }

    @EventHandler
    private void onPlayerEvent(PlayerEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        if (!worldName.startsWith("game_")) return;

        String[] worldSplit = event.getPlayer().getWorld().getName().split("_");
        int gameID = Integer.parseInt(worldSplit[1]);

        if (!activeGames.containsKey(gameID)) return;

        activeGames.get(gameID).getCurrentPhase().applyEvent(event);
    }




}
