package it.fedet.minigames.items;

import com.viaversion.viaversion.libs.fastutil.Pair;
import de.tr7zw.nbtapi.NBT;
import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.items.IItemService;
import it.fedet.minigames.api.items.provider.ClickableItem;
import it.fedet.minigames.api.items.provider.InteractItem;
import it.fedet.minigames.api.services.Service;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ItemService implements Service, IItemService, Listener {

    private final Map<String, InteractItem> customItems = new HashMap<>();
    private final MinigamesCore plugin;

    public ItemService(MinigamesCore plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(this, plugin.getPlugin());
    }

    public void stop() {
        customItems.clear();
        ClickableItem.CLICKABLE_ITEMS.clear();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack eventItem = event.getItem();

        if (eventItem == null || eventItem.getType() == Material.AIR || eventItem.getAmount() == 0) return;

        Optional<InteractItem> interactItem = getItem(eventItem);

        interactItem.ifPresentOrElse(item -> {
            Player player = event.getPlayer();

            boolean result = item.onInteract(event);

            if (result && item.isVanishAfterUse()) {

                eventItem.setAmount(eventItem.getAmount() - 1);
                player.playSound(player.getLocation(), item.getBreakSound(), 0.5F, 1);
            }

            if (item.isCancelled()) event.setCancelled(true);
        }, () -> {
            String id = NBT.get(eventItem, nbt -> {
                return nbt.getOrDefault("games-api:clickable-item", "");
            });

            if (id.isEmpty()) return;

            Pair<Class<? extends Event>, Consumer<? extends Event>> pair = ClickableItem.CLICKABLE_ITEMS.get(UUID.fromString(id));
            if (pair == null) return;

            if (!pair.first().isInstance(event)) return;

            Consumer<Event> typedAction = (Consumer<Event>) pair.second();
            typedAction.accept(event);
        });
    }


    @Override
    public InteractItem getItem(String id) {
        return customItems.get(id);
    }

    public <T extends InteractItem> InteractItem getItem(Class<T> clazz) {
        return customItems.values().stream().filter(clazz::isInstance).findFirst().orElse(null);
    }

    @Override
    public void removeItem(String id) {
        customItems.remove(id);
    }

    @Override
    public Optional<InteractItem> getItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return Optional.empty();

        String id = NBT.modify(item, nbt -> {
            return nbt.getOrDefault("games-api:custom-item", "");
        });

        if (id.isEmpty()) return Optional.empty();

        return Optional.ofNullable(customItems.get(id));
    }

    @Override
    public void removeItem(ItemStack item) {
        String id = NBT.modify(item, nbt -> {
            return nbt.getOrDefault("games-api:custom-item", "");
        });

        if (id.isEmpty()) return;

        removeItem(id);
    }

    @Override
    public void registerItem(InteractItem... items) {
        Stream.of(items).forEach(item -> {
            if (customItems.containsKey(item.getId())) {
                throw new IllegalArgumentException("Duplicate item id: " + item.getId());
            }

            customItems.put(item.getId(), item);
        });
    }

    @Override
    public void registerItems(List<InteractItem> items) {
        items.forEach(item -> {
            if (customItems.containsKey(item.getId())) {
                throw new IllegalArgumentException("Duplicate item id: " + item.getId());
            }

            customItems.put(item.getId(), item);
        });
    }

    @Override
    public void unregisterItems(InteractItem... items) {
        Stream.of(items).forEach(item -> customItems.remove(item.getId()));
    }
}
