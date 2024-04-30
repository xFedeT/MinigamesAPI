package it.fedet.minigames;

import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.provider.MinigamesProvider;
import it.fedet.minigames.api.services.Service;
import it.fedet.minigames.board.ScoreboardService;
import it.fedet.minigames.game.GameService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MinigamesCore extends JavaPlugin implements MinigamesAPI {

    private final Map<Class<? extends Service>, Service> services = new LinkedHashMap<>();

    public static MinigamesCore instance;

    @Override
    public void onEnable() {
        MinigamesProvider.register(this);

        //Loading all services
        for (Class<?> service : getServices()) {
            try {
                /*if (!services.containsKey(DatabaseService.class)) {
                    DatabaseService databaseService = new DatabaseService(this, dictationBukkit.getSqlManager());
                    databaseService.start();

                    services.put(DatabaseService.class, databaseService);
                }*/

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

    @Override
    public <T extends Service> T getService(Class<T> service) {
        return (T) services.get(service);
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
