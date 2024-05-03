package it.fedet.minigames.api.gui;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.fedet.minigames.api.Minigame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public abstract class GameGui<P extends Minigame<P>> implements InventoryProvider {

    protected final P plugin;

    public GameGui(P plugin) {
        this.plugin = plugin;
    }

    public abstract String getId();

    public abstract String getTitle();

    public abstract InventoryType getInventoryType();

    public abstract int getRows();

    public abstract int getColumns();

    public abstract boolean isCloseable();

    public abstract void init(Player player, InventoryContents contents);

    public abstract void update(Player player, InventoryContents contents);
}
