package it.fedet.minigames.api.items;

import it.fedet.minigames.api.game.player.inventory.InventorySnapshot;

public abstract class GameInventory {

    private InventorySnapshot inventorySnapshot;


    public abstract InventorySnapshot getInventorySnapshot();
}
