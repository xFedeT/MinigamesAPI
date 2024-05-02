package it.fedet.minigames.api.game.inventory;

import it.fedet.minigames.api.game.inventory.armor.ArmorContents;
import it.fedet.minigames.api.game.inventory.item.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class InventorySnapshot implements Cloneable {

    public static final InventorySnapshot EMPTY_SNAPSHOT;

    static {
        List<InventoryItem> items = new ArrayList<>();

        for (int i = 0; i < 36; i++) {
            items.add(InventoryItem.builder()
                    .itemStack(new ItemStack(Material.AIR))
                    .position(i)
                    .build()
            );
        }

        EMPTY_SNAPSHOT = InventorySnapshot.builder()
                .exp(0)
                .level(0)
                .inventory(items)
                .build();
    }

    private final List<InventoryItem> inventory;
    private final double health;
    private final int exp;
    private final int level;
    private final float saturation;
    private final boolean clean;
    private final List<PotionEffect> potionEffects;
    private final ArmorContents armorContents;
    private final int foodLevel;

    public InventorySnapshot(List<InventoryItem> inventory, double health, int exp, int level, float saturation, int foodLevel, boolean clean, List<PotionEffect> potionEffects, ArmorContents armorContents) {
        this.inventory = inventory;
        this.health = health;
        this.exp = exp;
        this.level = level;
        this.clean = clean;
        this.saturation = saturation;
        this.foodLevel = foodLevel;
        this.potionEffects = potionEffects;
        this.armorContents = armorContents;
    }

    public static InventorySnapshotBuilder builder() {
        return new InventorySnapshotBuilder();
    }

    public static InventoryItem.InventoryItemBuilder itemBuilder() {
        return new InventoryItem.InventoryItemBuilder();
    }

    public static InventorySnapshot createFromPlayer(Player player) {
        List<InventoryItem> inventoryItems = new ArrayList<>();

        PlayerInventory playerInventory = player.getInventory();

        for (int i = 0; i < playerInventory.getContents().length; i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (stack == null || stack.getType() == Material.AIR) continue;

            inventoryItems.add(InventoryItem.builder()
                    .position(i)
                    .itemStack(stack)
                    .build());
        }

        ItemStack air = new ItemStack(Material.AIR);

        return InventorySnapshot.builder()
                .exp(player.getTotalExperience())
                .health(player.getHealth())
                .saturation(player.getSaturation())
                .potionEffects(new ArrayList<>(player.getActivePotionEffects()))
                .armorContents(
                        ArmorContents.builder()
                                .helmet(playerInventory.getHelmet() == null ? air : playerInventory.getHelmet())
                                .chestplate(playerInventory.getChestplate() == null ? air : playerInventory.getChestplate())
                                .leggings(playerInventory.getLeggings() == null ? air : playerInventory.getLeggings())
                                .boots(playerInventory.getBoots() == null ? air : playerInventory.getBoots())
                                .build()
                )
                .inventory(inventoryItems)
                .build();
    }

    public void apply(Player player) {
        if (player != null && player.isOnline()) {
            if (clean)
                player.getInventory().clear();

            player.getInventory().setHelmet(armorContents.getHelmet());
            player.getInventory().setChestplate(armorContents.getChestplate());
            player.getInventory().setLeggings(armorContents.getLeggings());
            player.getInventory().setBoots(armorContents.getBoots());

            if (inventory != null) {
                for (InventoryItem inventoryItem : inventory) {
                    player.getInventory().setItem(inventoryItem.getPosition(), inventoryItem.getItemStack().clone());
                }
            }

            player.setFireTicks(0);

            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            potionEffects.forEach(player::addPotionEffect);

            if (health != player.getMaxHealth() && health != 0)
                player.setMaxHealth(health);

            player.setHealth(health == 0 ? player.getMaxHealth() : health);
            player.setExp(exp);
            player.setLevel(level);
            player.setSaturation(saturation);
            player.setFoodLevel(foodLevel);
            player.updateInventory();
        }
    }

    public ArmorContents getArmorContents() {
        return armorContents;
    }

    public double getHealth() {
        return health;
    }

    public float getSaturation() {
        return saturation;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public InventorySnapshot clone() {
        try {
            InventorySnapshot clone = (InventorySnapshot) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class InventorySnapshotBuilder {

        private List<InventoryItem> inventory;
        private double health = 0;
        private int exp = 0;
        private int level = 0;
        private float saturation = 20;
        private int foodLevel = 20;
        private boolean clean = false;
        private List<PotionEffect> potionEffects = new ArrayList<>();
        private ArmorContents armorContents = ArmorContents.EMPTY_CONTENTS;

        public InventorySnapshotBuilder inventory(List<InventoryItem> inventory) {
            this.inventory = inventory;
            return this;
        }

        public InventorySnapshotBuilder health(double health) {
            this.health = health;
            return this;
        }

        public InventorySnapshotBuilder exp(int exp) {
            this.exp = exp;
            return this;
        }

        public InventorySnapshotBuilder level(int level) {
            this.level = level;
            return this;
        }

        public InventorySnapshotBuilder saturation(float saturation) {
            this.saturation = saturation;
            return this;
        }

        public InventorySnapshotBuilder foodLevel(int foodLevel) {
            this.foodLevel = foodLevel;
            return this;
        }

        public InventorySnapshotBuilder clean(boolean clean) {
            this.clean = clean;
            return this;
        }

        public InventorySnapshotBuilder potionEffects(List<PotionEffect> potionEffects) {
            this.potionEffects = potionEffects;
            return this;
        }

        public InventorySnapshotBuilder armorContents(ArmorContents armorContents) {
            this.armorContents = armorContents;
            return this;
        }

        public InventorySnapshot build() {
            return new InventorySnapshot(inventory, health, exp, level, saturation, foodLevel, clean, potionEffects, armorContents);
        }

    }

}