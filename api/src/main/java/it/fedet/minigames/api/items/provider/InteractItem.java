package it.fedet.minigames.api.items.provider;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class InteractItem {

    protected final String id;

    private final ItemStack itemStack;

    private final boolean vanishAfterUse;


    protected InteractItem(boolean vanishAfterUse) {
        this.id = getId();
        this.vanishAfterUse = vanishAfterUse;

        this.itemStack = applyItemId(getItem());
    }

    public abstract String getId();

    protected abstract ItemStack getItem();

    public abstract boolean onInteract(PlayerInteractEvent event);

    public boolean isCancelled() {
        return true;
    }

    public Sound getBreakSound() {
        return Sound.ANVIL_LAND;
    }

    private ItemStack applyItemId(ItemStack item) {
        NBT.modify(item, nbt -> {
            nbt.setString("games-api:custom-item", id);
        });

        return item;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isVanishAfterUse() {
        return vanishAfterUse;
    }
}
