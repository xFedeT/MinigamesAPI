package it.fedet.minigames;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.commands.GameCommand;
import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.game.inventory.InventorySnapshot;
import it.fedet.minigames.api.gui.GameGui;
import it.fedet.minigames.api.items.GameInventory;
import it.fedet.minigames.api.provider.MinigamesProvider;
import it.fedet.minigames.api.services.IWorldService;
import it.fedet.minigames.api.services.Service;
import it.fedet.minigames.api.swm.database.StorageType;
import it.fedet.minigames.api.swm.database.WorldDbProvider;
import it.fedet.minigames.board.ScoreboardService;
import it.fedet.minigames.commands.CommandService;
import it.fedet.minigames.commands.exception.NotLampCommandClassException;
import it.fedet.minigames.game.GameService;
import it.fedet.minigames.items.ItemService;
import it.fedet.minigames.player.PlayerService;
import it.fedet.minigames.swm.service.WorldService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.annotation.Command;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MinigamesCore extends JavaPlugin implements MinigamesAPI {
    private Minigame<?> minigame;

    private static final Map<Class<? extends SettingsHolder>, SettingsManager> files = new HashMap<>();
    private final Map<Class<? extends Service>, Service> services = new LinkedHashMap<>();
    private final Map<Class<? extends GameGui<?>>, SmartInventory> guis = new LinkedHashMap<>();
    private final Map<Class<? extends GameInventory>, GameInventory> inventorys = new LinkedHashMap<>();
    private final Map<Class<? extends GameCommand>, GameCommand> commands = new LinkedHashMap<>();

    private WorldDbProvider worldDbProvider;
    private StorageType storageType;

    private MinigamesCore instance;


    @Override
    public void onEnable() {
        MinigamesProvider.register(this);
        instance = this;
    }

    public boolean registerConfig(List<MinigameConfig> configs) {
        try {
            for (MinigameConfig setting : configs) {
                files.put(setting.getClazz(), SettingsManagerBuilder
                        .withYamlFile(
                                new File(getDataFolder().getAbsolutePath() + setting.getPath(), setting.getFileName() + ".yml")
                        )
                        .configurationData(setting.getClazz())
                        .useDefaultMigrationService()
                        .create()
                );
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerGui(Class<? extends GameGui<?>> type, GameGui gameGui) {
        guis.put(type,
                SmartInventory.builder()
                        .id(gameGui.getId())
                        .title(gameGui.getTitle())
                        .size(gameGui.getRows(), gameGui.getColumns())
                        .provider(gameGui)
                        .closeable(gameGui.isCloseable())
                        .type(gameGui.getInventoryType())
                        .manager(new InventoryManager(this))
                        .build()
        );
    }

    @Override
    public <P extends WorldDbProvider> void registerWorldDbProvider(P worldDbProvider) {
        this.worldDbProvider = worldDbProvider;
    }

    @Override
    public SmartInventory getGui(Class<? extends GameGui<?>> type) {
        return guis.get(type);
    }

    @Override
    public void openGui(Class<? extends GameGui<?>> type, Player player) {
        guis.get(type).open(player);
    }

    @Override
    public InventorySnapshot getInventory(Class<? extends GameInventory> type) {
        return inventorys.get(type).getInventorySnapshot();
    }

    @Override
    public void openInventory(Class<? extends GameInventory> type, Player player) {
        inventorys.get(type).getInventorySnapshot().apply(player);
    }

    @Override
    public SettingsManager getConfig(Class<? extends SettingsHolder> type) {
        return files.get(type);
    }

    @Override
    public <S extends Service> S getService(Class<S> service) {
        return (S) services.get(service);
    }

    @Override
    public <D extends DatabaseProvider> boolean registerDatabaseProvider(D provider) {
        try {
            services.put(DatabaseProvider.class, provider);
            provider.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public <P extends Minigame<P>> void registerMinigame(Minigame<P> minigame) {
        this.minigame = minigame;

        //Loading all services
        for (Class<?> service : getServices()) {
            try {
                if (service.isAssignableFrom(Service.class))
                    throw new RuntimeException();

                Service object = (Service) service.getConstructor(MinigamesCore.class).newInstance(this);

                if (object instanceof IWorldService) {
                    ((IWorldService) object).setProvider(worldDbProvider);
                }

                object.start();

                services.put((Class<? extends Service>) service, object);
                getLogger().info("Loaded a new service: " + service.getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().info("Cannot load a service: " + service.getSimpleName());
                getLogger().info("Instance shutdown...");
                Bukkit.shutdown();
                return;
            }
        }

        registerConfig(minigame.registerConfigs());

        //registering gui
        minigame.registerGuis().forEach(this::registerGui);


        //Saving Inventorys
        inventorys.putAll(minigame.registerInventorys());

        //Register Command
        minigame.registerCommands().forEach((clazz, command) -> {
            if (command.getClass().isAnnotationPresent(Command.class)) {
                commands.put(clazz, command);
            } else {
                try {
                    throw new NotLampCommandClassException();
                } catch (NotLampCommandClassException e) {
                    e.printStackTrace();
                }
            }
        });

        services.put(CommandService.class, new CommandService(this));
        getService(CommandService.class).start();
    }

    @Override
    public void onDisable() {
        MinigamesProvider.unregister();
    }

    public Map<Class<? extends GameCommand>, GameCommand> getCommands() {
        return commands;
    }

    public Minigame<?> getMinigame() {
        return minigame;
    }

    private Class<?>[] getServices() {
        return new Class<?>[]{
                PlayerService.class,
                ItemService.class,
                ScoreboardService.class,
                GameService.class,
                WorldService.class
        };
    }

    @Override
    public JavaPlugin getPlugin() {
        return instance;
    }
}
