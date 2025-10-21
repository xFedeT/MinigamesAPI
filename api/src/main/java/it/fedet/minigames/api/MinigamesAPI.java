package it.fedet.minigames.api;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import fr.minuskube.inv.SmartInventory;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.game.inventory.InventorySnapshot;
import it.fedet.minigames.api.gui.GameGui;
import it.fedet.minigames.api.items.GameInventory;
import it.fedet.minigames.api.services.Service;
import it.fedet.minigames.api.swm.database.WorldDbProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface MinigamesAPI {

    <P extends WorldDbProvider> void registerWorldDbProvider(P worldDbProvider);

    SmartInventory getGui(Class<? extends GameGui<?>> type);

    void openGui(Class<? extends GameGui<?>> type, Player player);

    InventorySnapshot getInventory(Class<? extends GameInventory> type);

    void openInventory(Class<? extends GameInventory> type, Player player);

    SettingsManager getConfig(Class<? extends SettingsHolder> type);

    <S extends Service> S getService(Class<S> service);

    <D extends DatabaseProvider> boolean registerDatabaseProvider(D provider);

    <P extends Minigame<P>> void registerMinigame(Minigame<P> minigame);

    JavaPlugin getPlugin();
}
