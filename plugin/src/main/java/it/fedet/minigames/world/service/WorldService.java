package it.fedet.minigames.world.service;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.services.IWorldService;
import it.fedet.minigames.api.services.Service;
import it.fedet.minigames.api.world.SlimeLoader;
import it.fedet.minigames.api.world.data.SlimeWorld;
import it.fedet.minigames.api.world.exception.*;
import it.fedet.minigames.utils.LoaderUtils;
import it.fedet.minigames.world.importer.WorldImporter;
import it.fedet.minigames.world.map.SlimeProperties;
import it.fedet.minigames.world.map.SlimePropertyMap;
import it.fedet.minigames.world.nms.v1_8_R3SlimeNMS;
import it.fedet.minigames.world.nms.world.CraftSlimeWorld;
import it.fedet.minigames.world.storage.StorageType;
import it.fedet.minigames.api.world.providers.WorldDbProvider;
import it.fedet.minigames.world.unlocker.WorldUnlocker;
import org.bukkit.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldService implements Service, IWorldService {

    private final MinigamesCore plugin;

    private StorageType storageType;
    private WorldDbProvider provider;

    private final v1_8_R3SlimeNMS nms = new v1_8_R3SlimeNMS();

    private final ExecutorService worldGeneratorService = Executors.newFixedThreadPool(1);

    public WorldService(MinigamesCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(new WorldUnlocker(this), plugin);

        LoaderUtils.registerLoaders(storageType, provider);
    }

    @Override
    public void stop() {
        worldGeneratorService.shutdown();
    }


    public CompletableFuture<CraftSlimeWorld> loadWorld(SlimeLoader loader, String worldName, CraftSlimeWorld.SlimeProperties properties) throws UnknownWorldException,
            IOException, CorruptedWorldException, NewerFormatException, WorldInUseException {
        Objects.requireNonNull(properties, "Properties cannot be null");

        return loadWorld(loader, worldName, properties.isReadOnly(), properties.isIgnoreLocked(), propertiesToMap(properties));
    }


    public CompletableFuture<CraftSlimeWorld> loadWorld(SlimeLoader loader, String worldName, boolean isIgnoreLocked, boolean readOnly, SlimePropertyMap propertyMap) {

        return CompletableFuture.supplyAsync(() -> {
            Objects.requireNonNull(loader, "Loader cannot be null");
            Objects.requireNonNull(worldName, "World name cannot be null");
            Objects.requireNonNull(propertyMap, "Properties cannot be null");

            long start = System.currentTimeMillis();

            byte[] serializedWorld;
            try {
                serializedWorld = loader.loadWorld(worldName, readOnly);
            } catch (UnknownWorldException | WorldInUseException | IOException e) {
                throw new RuntimeException(e);
            }
            CraftSlimeWorld world = null;

            try {
                world = LoaderUtils.deserializeWorld(loader, worldName, serializedWorld, propertyMap, isIgnoreLocked, readOnly);

                if (world.getVersion() > nms.getWorldVersion() || world.getVersion() < nms.getWorldVersion()) {
                    Bukkit.getLogger().severe("World " + worldName + " is out of version " + nms.getWorldVersion());
                }
            } catch (Exception ex) {
                if (!readOnly) { // Unlock the world as we're not using it
                    try {
                        loader.unlockWorld(worldName);
                    } catch (UnknownWorldException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            Bukkit.getLogger().info("World " + worldName + " loaded in " + (System.currentTimeMillis() - start) + "ms.");

            return world;
        });
    }

    public CraftSlimeWorld createEmptyWorld(SlimeLoader loader, String worldName, CraftSlimeWorld.SlimeProperties properties) throws WorldAlreadyExistsException, IOException {
        Objects.requireNonNull(properties, "Properties cannot be null");

        return createEmptyWorld(loader, worldName, properties.isReadOnly(), propertiesToMap(properties));
    }


    public CraftSlimeWorld createEmptyWorld(SlimeLoader loader, String worldName, boolean readOnly, SlimePropertyMap propertyMap) throws WorldAlreadyExistsException, IOException {
        Objects.requireNonNull(loader, "Loader cannot be null");
        Objects.requireNonNull(worldName, "World name cannot be null");
        Objects.requireNonNull(propertyMap, "Properties cannot be null");

        if (loader.worldExists(worldName)) {
            throw new WorldAlreadyExistsException(worldName);
        }

        long start = System.currentTimeMillis();
        CraftSlimeWorld world = new CraftSlimeWorld(loader, worldName, new HashMap<>(), new CompoundTag("",
                new CompoundMap()), new ArrayList<>(), nms.getWorldVersion(), propertyMap, readOnly, false, !readOnly);
        loader.saveWorld(worldName, world.serialize(), !readOnly);

        return world;
    }

    private SlimePropertyMap propertiesToMap(CraftSlimeWorld.SlimeProperties properties) {
        SlimePropertyMap propertyMap = new SlimePropertyMap();

        propertyMap.setInt(SlimeProperties.SPAWN_X, (int) properties.getSpawnX());
        propertyMap.setInt(SlimeProperties.SPAWN_Y, (int) properties.getSpawnY());
        propertyMap.setInt(SlimeProperties.SPAWN_Z, (int) properties.getSpawnZ());
        propertyMap.setString(SlimeProperties.DIFFICULTY, Difficulty.getByValue(properties.getDifficulty()).name());
        propertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, properties.allowMonsters());
        propertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, properties.allowAnimals());
        propertyMap.setBoolean(SlimeProperties.PVP, properties.isPvp());
        propertyMap.setString(SlimeProperties.ENVIRONMENT, properties.getEnvironment());

        return propertyMap;
    }

    public CompletableFuture<World> generateWorld(CraftSlimeWorld world) {
        CompletableFuture<World> future = new CompletableFuture<>();
        Objects.requireNonNull(world, "SlimeWorld cannot be null");
        if (!world.isIgnoreLocked() && !world.isReadOnly() && !world.isLocked()) {
            throw new IllegalArgumentException("This world cannot be loaded, as it has not been locked.");
        } else {
            if (!Bukkit.isPrimaryThread()) {
                Bukkit.getScheduler().runTask(plugin, () -> this.nms.generateWorld(world).whenComplete((value, ex) -> future.complete(value)));
            } else {
                this.nms.generateWorld(world).whenComplete((value, ex) -> future.complete(value));
            }

            return future;
        }
    }

    public void migrateWorld(String worldName, SlimeLoader currentLoader, SlimeLoader newLoader) throws IOException,
            WorldInUseException, WorldAlreadyExistsException, UnknownWorldException {
        Objects.requireNonNull(worldName, "World name cannot be null");
        Objects.requireNonNull(currentLoader, "Current loader cannot be null");
        Objects.requireNonNull(newLoader, "New loader cannot be null");

        if (newLoader.worldExists(worldName)) {
            throw new WorldAlreadyExistsException(worldName);
        }

        World bukkitWorld = Bukkit.getWorld(worldName);

        boolean leaveLock = false;

        if (bukkitWorld != null) {
            // Make sure the loaded world really is a SlimeWorld and not a normal Bukkit world
            CraftSlimeWorld slimeWorld = nms.getSlimeWorld(bukkitWorld);

            if (slimeWorld != null && currentLoader.equals(slimeWorld.getLoader())) {
                slimeWorld.setLoader(newLoader);

                if (!slimeWorld.isReadOnly()) { // We have to manually unlock the world so no WorldInUseException is thrown
                    currentLoader.unlockWorld(worldName);
                    leaveLock = true;
                }
            }
        }

        byte[] serializedWorld = currentLoader.loadWorld(worldName, false);

        newLoader.saveWorld(worldName, serializedWorld, leaveLock);
        currentLoader.deleteWorld(worldName);
    }

    public SlimeLoader getLoader(StorageType storageType) {
        Objects.requireNonNull(storageType, "Data source cannot be null");

        return LoaderUtils.getLoader(storageType);
    }

    public void registerLoader(StorageType storageType, SlimeLoader loader) {
        Objects.requireNonNull(storageType, "Data source cannot be null");
        Objects.requireNonNull(loader, "Loader cannot be null");

        LoaderUtils.registerLoader(storageType, loader);
    }

    public void importWorld(File worldDir, String worldName, SlimeLoader loader) throws WorldAlreadyExistsException,
            InvalidWorldException, WorldLoadedException, WorldTooBigException, IOException {
        Objects.requireNonNull(worldDir, "World directory cannot be null");
        Objects.requireNonNull(worldName, "World name cannot be null");
        Objects.requireNonNull(loader, "Loader cannot be null");

        if (loader.worldExists(worldName)) {
            throw new WorldAlreadyExistsException(worldName);
        }

        World bukkitWorld = Bukkit.getWorld(worldDir.getName());

        if (bukkitWorld != null && nms.getSlimeWorld(bukkitWorld) == null) {
            throw new WorldLoadedException(worldDir.getName());
        }

        CraftSlimeWorld world = WorldImporter.readFromDirectory(worldDir);

        byte[] serializedWorld;

        try {
            serializedWorld = world.serialize();
        } catch (IndexOutOfBoundsException ex) {
            throw new WorldTooBigException(worldDir.getName());
        }

        loader.saveWorld(worldName, serializedWorld, false);
    }



    @Override
    public void setProvider(WorldDbProvider provider) {
        this.provider = provider;
        this.storageType = (StorageType) provider.getType();
    }

    public v1_8_R3SlimeNMS getNms() {
        return nms;
    }

    public MinigamesCore getPlugin() {
        return plugin;
    }
}
