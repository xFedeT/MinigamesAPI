package it.fedet.minigames.impl.sumo.guis;

import fr.minuskube.inv.content.InventoryContents;
import it.fedet.minigames.api.gui.GameGui;
import it.fedet.minigames.impl.sumo.Sumo;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.UUID;

public class ProvaGui extends GameGui<Sumo> {

    private Sumo plugin;

    public ProvaGui(Sumo plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public String getId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getTitle() {
        return "Titolo";
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CHEST;
    }

    @Override
    public int getRows() {
        return 10;
    }

    @Override
    public int getColumns() {
        return 21;
    }

    @Override
    public boolean isCloseable() {
        return true;
    }

    @Override
    public void init(Player player, InventoryContents contents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }


}
