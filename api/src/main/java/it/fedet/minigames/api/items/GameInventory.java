package it.fedet.minigames.api.items;

import it.fedet.minigames.api.game.inventory.InventorySnapshot;

public abstract class GameInventory {

    private InventorySnapshot inventorySnapshot;


    public abstract InventorySnapshot getInventorySnapshot();
}
