package it.fedet.minigames.api.items;

import it.fedet.minigames.api.items.provider.InteractItem;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface IItemService {

    <T extends InteractItem> InteractItem getItem(Class<T> clazz);

    InteractItem getItem(String id);

    void removeItem(String id);

    Optional<InteractItem> getItem(ItemStack item);

    void removeItem(ItemStack item);

    void registerItem(InteractItem... items);

    void registerItems(List<InteractItem> items);

    void unregisterItems(InteractItem... items);

}