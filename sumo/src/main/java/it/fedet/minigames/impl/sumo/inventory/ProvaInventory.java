package it.fedet.minigames.impl.sumo.inventory;

import it.fedet.minigames.api.game.inventory.InventorySnapshot;
import it.fedet.minigames.api.game.inventory.item.InventoryItem;
import it.fedet.minigames.api.items.GameInventory;
import it.fedet.minigames.api.items.provider.ClickableItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ProvaInventory implements GameInventory {
    @Override
    public InventorySnapshot getInventorySnapshot() {
        return InventorySnapshot.builder()
                .inventory(List.of(
                        InventoryItem.builder()
                                .position(0)
                                .itemStack(ClickableItem.of(new ItemStack(Material.ACACIA_DOOR_ITEM),
                                        clickEvent -> {
                                            clickEvent.getPlayer().sendMessage("Hai cliccato sulla porta di acacia!");
                                         })
                                )
                                .build()
                ))
                .build();
    }
}
