package it.fedet.minigames.api.items.provider;

import com.viaversion.viaversion.libs.fastutil.Pair;
import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectArrayMap;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ClickableItem {

    public static Map<UUID, Pair<Class<? extends Event>, Consumer<? extends Event>>> CLICKABLE_ITEMS = new Object2ObjectArrayMap<>();

    public static ItemStack of(ItemStack item, Consumer<PlayerInteractEvent> event) {
        UUID uuid = UUID.randomUUID();

        NBT.modify(item, nbt -> {
            nbt.setString("games-api:clickable-item", uuid.toString());
        });

        CLICKABLE_ITEMS.put(uuid, Pair.of(PlayerInteractEvent.class, event));

        return item;
    }

    public static ItemStack ofEntity(ItemStack item, Consumer<PlayerInteractEntityEvent> event) {
        UUID uuid = UUID.randomUUID();

        NBT.modify(item, nbt -> {
            nbt.setString("games-api:clickable-item", uuid.toString());
        });

        CLICKABLE_ITEMS.put(uuid, Pair.of(PlayerInteractEntityEvent.class, event));

        return item;
    }



}
