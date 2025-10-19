package it.fedet.minigames.world.service;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.services.IWorldService;
import it.fedet.minigames.api.services.Service;
import it.fedet.minigames.api.world.WorldLoader;
import it.fedet.minigames.api.world.storage.IWorldDbProvider;
import it.fedet.minigames.api.world.storage.StorageType;
import it.fedet.minigames.api.world.storage.WorldStorageProvider;
import it.fedet.minigames.world.loader.AsyncWorldLoader;
import it.fedet.minigames.world.storage.MariaDBStorageProvider;
import it.fedet.minigames.world.storage.MongoDBStorageProvider;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class WorldService implements Service, IWorldService {

    private final MinigamesCore plugin;
    private WorldLoader worldLoader;
    private WorldStorageProvider storageProvider = null;
    private StorageType storageType;


    public WorldService(MinigamesCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        if (storageProvider == null) {
            worldLoader = new AsyncWorldLoader(plugin, storageProvider);
            plugin.getLogger().info("WorldService initialized with " + storageType + " storage");
            return;
        }

        plugin.getLogger().severe("No WorldDbProvider set, use setDbProvider() or assure that you load it before starting the WorldService");
    }

    @Override
    public void setDbProvider(IWorldDbProvider dbProvider) {
        this.storageType = dbProvider.getType();

        switch (dbProvider.getType()) {
            case MONGODB -> storageProvider = new MongoDBStorageProvider(
                    dbProvider.getConnectionOrHostString(),
                    dbProvider.getDatabaseName(),
                    dbProvider.getTableOrCollectionName()
            );
            case MARIADB -> storageProvider = new MariaDBStorageProvider(
                    dbProvider.getConnectionOrHostString(),
                    dbProvider.getPort(),
                    dbProvider.getDatabaseName(),
                    dbProvider.getUsername(),
                    dbProvider.getPassword(),
                    dbProvider.getTableOrCollectionName()
            );
        }
    }



    @Override
    public void stop() {
        if (worldLoader instanceof AsyncWorldLoader) {
            ((AsyncWorldLoader) worldLoader).shutdown();
        }
    }

    /**
     * Loads a world without game ID
     */
    @Override
    public CompletableFuture<World> loadWorld(String worldName) {
        return worldLoader.loadWorld(worldName);
    }

    /**
     * Unloads a world
     */
    @Override
    public CompletableFuture<Void> unloadWorld(World world) {
        return worldLoader.unloadWorld(world);
    }

    /**
     * Unloads a world by name
     */
    @Override
    public CompletableFuture<Void> unloadWorld(String worldName) {
        return worldLoader.unloadWorld(worldName);
    }

    /**
     * Checks if a world exists in storage
     */
    @Override
    public CompletableFuture<Boolean> worldExists(String worldName) {
        return worldLoader.worldExists(worldName);
    }

    /**
     * Saves a world to storage (for backup purposes)
     */
    @Override
    public CompletableFuture<Void> saveWorld(World world) {
        return worldLoader.saveWorld(world);
    }

    @Override
    public WorldLoader getWorldLoader() {
        return worldLoader;
    }

    @Override
    public WorldStorageProvider getStorageProvider() {
        return storageProvider;
    }
}