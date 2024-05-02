package it.fedet.minigames.api;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import fr.minuskube.inv.SmartInventory;
import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.game.player.inventory.InventorySnapshot;
import it.fedet.minigames.api.gui.GameGui;
import it.fedet.minigames.api.items.GameInventory;
import it.fedet.minigames.api.services.Service;
import org.bukkit.entity.Player;

import java.util.List;

public interface MinigamesAPI {

    SmartInventory getGui(Class<? extends GameGui<?>> type);

    void openGui(Class<? extends GameGui<?>> type, Player player);

    InventorySnapshot getInventory(Class<? extends GameInventory> type);

    void openInventory(Class<? extends GameInventory> type, Player player);

    SettingsManager getSettings(Class<? extends SettingsHolder> type);

    <T extends Service> T getService(Class<T> service);

    <T extends DatabaseProvider> boolean registerDatabaseProvider(T provider);

    <T extends Minigame<T>> void registerMinigame(Minigame<T> minigame);
}
