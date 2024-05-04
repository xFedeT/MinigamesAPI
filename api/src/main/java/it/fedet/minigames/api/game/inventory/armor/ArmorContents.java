package it.fedet.minigames.api.game.inventory.armor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ArmorContents {

    public static final ArmorContents EMPTY_CONTENTS = ArmorContents.builder().build();

    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;

    public ArmorContents(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public ItemStack[] asArray() {
        return new ItemStack[]{helmet.clone(),
                chestplate.clone(),
                leggings.clone(),
                boots.clone()};
    }

    public ItemStack getLeggings() {
        return leggings.clone();
    }

    public ItemStack getHelmet() {
        return helmet.clone();
    }

    public ItemStack getBoots() {
        return boots.clone();
    }

    public ItemStack getChestplate() {
        return chestplate.clone();
    }

    public static ArmorContentsBuilder builder() {
        return new ArmorContentsBuilder();
    }

    public static class ArmorContentsBuilder {

        private ItemStack helmet = new ItemStack(Material.AIR);
        private ItemStack chestplate = new ItemStack(Material.AIR);
        private ItemStack leggings = new ItemStack(Material.AIR);
        private ItemStack boots = new ItemStack(Material.AIR);

        public ArmorContentsBuilder helmet(ItemStack helmet) {
            this.helmet = helmet;
            return this;
        }

        public ArmorContentsBuilder chestplate(ItemStack chestplate) {
            this.chestplate = chestplate;
            return this;
        }

        public ArmorContentsBuilder leggings(ItemStack leggings) {
            this.leggings = leggings;
            return this;
        }

        public ArmorContentsBuilder boots(ItemStack boots) {
            this.boots = boots;
            return this;
        }

        public ArmorContents build() {
            return new ArmorContents(helmet, chestplate, leggings, boots);
        }

    }

}