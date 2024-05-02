package it.fedet.minigames.impl.sumo.inventory;

import it.fedet.minigames.api.game.inventory.InventorySnapshot;
import it.fedet.minigames.api.items.GameInventory;

public class ProvaInventory extends GameInventory {
    @Override
    public InventorySnapshot getInventorySnapshot() {
        return InventorySnapshot.builder().build();
    }
}
