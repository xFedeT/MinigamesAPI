package it.fedet.minigames;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.provider.MinigamesProvider;
import it.fedet.minigames.api.services.Service;
import it.fedet.minigames.board.ScoreboardService;
import it.fedet.minigames.game.GameService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MinigamesCore extends JavaPlugin implements MinigamesAPI {
    private Minigame minigame;

    private static final Map<Class<? extends SettingsHolder>, SettingsManager> files = new HashMap<>();
    private final Map<Class<? extends Service>, Service> services = new LinkedHashMap<>();

    public static MinigamesCore instance;

    @Override
    public void onEnable() {
        MinigamesProvider.register(this);

        //Loading all services
        for (Class<?> service : getServices()) {
            try {
                if (service.isAssignableFrom(Service.class))
                    throw new RuntimeException();

                Service object = (Service) service.getConstructor(MinigamesCore.class).newInstance(this);
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
    }

    public boolean registerConfig(List<MinigameConfig> configs) {
        try {
            for (MinigameConfig setting : configs) {
                files.put(setting.getClazz(), SettingsManagerBuilder
                        .withYamlFile(
                                new File(getDataFolder().getAbsolutePath() + setting.getPath(), setting.getFileName())
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

    @Override
    public SettingsManager getSettings(Class<? extends SettingsHolder> type) {
        return files.get(type);
    }

    @Override
    public <T extends Service> T getService(Class<T> service) {
        return (T) services.get(service);
    }

    @Override
    public <T extends DatabaseProvider> boolean registerDatabaseProvider(T provider) {
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
    public void registerMinigame(Minigame minigame) {
        this.minigame = minigame;
        registerConfig(minigame.getSettings());
    }


    @Override
    public void onDisable() {
        MinigamesProvider.unregister();
    }

    private Class<?>[] getServices() {
        return new Class<?>[]{
                GameService.class,
                ScoreboardService.class
        };
    }

}
