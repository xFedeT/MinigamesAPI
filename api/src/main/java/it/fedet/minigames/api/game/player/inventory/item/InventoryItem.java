package it.fedet.minigames.api.game.player.inventory.item;

import org.bukkit.inventory.ItemStack;

public final class InventoryItem {

    private final int position;
    private final ItemStack itemStack;

    public InventoryItem(int position, ItemStack itemStack) {
        this.position = position;
        this.itemStack = itemStack;
    }

    public int getPosition() {
        return position;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static InventoryItemBuilder builder() {
        return new InventoryItemBuilder();
    }

    public static class InventoryItemBuilder {

        private int position;
        private ItemStack itemStack;

        public InventoryItemBuilder position(int value) {
            this.position = value;
            return this;
        }

        public InventoryItemBuilder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public InventoryItem build() {
            return new InventoryItem(position, itemStack);
        }

    }

}