//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.service;


import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.services.IWorldService;
import it.fedet.minigames.api.swm.SlimePlugin;
import it.fedet.minigames.api.swm.database.StorageType;
import it.fedet.minigames.api.swm.database.WorldDbProvider;
import it.fedet.minigames.api.swm.exceptions.*;
import it.fedet.minigames.api.swm.loaders.SlimeLoader;
import it.fedet.minigames.api.swm.world.SlimeWorld;
import it.fedet.minigames.api.swm.world.properties.SlimeProperties;
import it.fedet.minigames.api.swm.world.properties.SlimePropertyMap;
import it.fedet.minigames.swm.service.loaders.LoaderUtils;
import it.fedet.minigames.swm.service.log.Logging;
import it.fedet.minigames.swm.service.world.WorldUnlocker;
import it.fedet.minigames.swm.service.world.importer.WorldImporter;
import it.fedet.minigames.swm.nms.CraftSlimeWorld;
import it.fedet.minigames.swm.nms.SlimeNMS;
import it.fedet.minigames.swm.nms.v1_8_R3.v1_8_R3SlimeNMS;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldService implements SlimePlugin, IWorldService {

    private final MinigamesCore plugin;
    private SlimeNMS nms;
    private final List<SlimeWorld> worlds = new ArrayList<>();
    private final ExecutorService worldGeneratorService = Executors.newFixedThreadPool(1);
    private boolean asyncWorldGen;
    private WorldDbProvider worldDbProvider;

    public WorldService(MinigamesCore plugin) {
        this.plugin = plugin;
    }


    @Override
    public void start() {
        LoaderUtils.registerLoaders(worldDbProvider);

        try {
            this.nms = this.getNMSBridge();
        } catch (InvalidVersionException ex) {
            Logging.error(ex.getMessage());
            return;
        }


        try {
            Properties props = new Properties();
            props.load(new FileInputStream("server.properties"));
            String defaultWorldName = props.getProperty("level-name");
            this.nms.setDefaultWorlds(
                    this.worlds.stream().filter((world) -> world.getName().equals(defaultWorldName)).findFirst().orElse(null),
                    plugin.getServer().getAllowNether() ? this.worlds.stream().filter((world) -> world.getName().equals(defaultWorldName + "_nether")).findFirst().orElse(null) : null,
                    plugin.getServer().getAllowEnd() ? this.worlds.stream().filter((world) -> world.getName().equals(defaultWorldName + "_the_end")).findFirst().orElse(null) : null
            );
        } catch (IOException ex) {
            Logging.error("Failed to retrieve default world name:");
            ex.printStackTrace();
        }

        if (this.nms == null) {
            Logging.error("SlimeWorldManager failed to enable due to an unsupported Minecraft version.");
        } else {
            plugin.getServer().getPluginManager().registerEvents(new WorldUnlocker(this), plugin);

            for (SlimeWorld world : this.worlds) {
                if (Bukkit.getWorld(world.getName()) == null) {
                    this.generateWorld(world);
                }
            }

            this.worlds.clear();
        }
    }

    @Override
    public void stop() {

    }

    private SlimeNMS getNMSBridge() throws InvalidVersionException {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersion = version.substring(version.lastIndexOf(46) + 1);
        if ("v1_8_R3".equals(nmsVersion)) {
            return new v1_8_R3SlimeNMS();
        } else {
            throw new InvalidVersionException(nmsVersion);
        }
    }

    public SlimeWorld loadWorld(SlimeLoader loader, String worldName, SlimeWorld.SlimeProperties properties) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException {
        Objects.requireNonNull(properties, "Properties cannot be null");
        return this.loadWorld(loader, worldName, properties.isReadOnly(), this.propertiesToMap(properties));
    }

    public SlimeWorld loadWorld(SlimeLoader loader, String worldName, boolean readOnly, SlimePropertyMap propertyMap) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException {
        return this.loadWorld(loader, worldName, readOnly, propertyMap, false);
    }

    public SlimeWorld loadWorld(SlimeLoader loader, String worldName, boolean readOnly, SlimePropertyMap propertyMap, boolean ignoreLocked) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException {
        Objects.requireNonNull(loader, "Loader cannot be null");
        Objects.requireNonNull(worldName, "World name cannot be null");
        Objects.requireNonNull(propertyMap, "Properties cannot be null");
        long start = System.currentTimeMillis();
        Logging.info("Loading world " + worldName + ".");
        byte[] serializedWorld = loader.loadWorld(worldName, readOnly, ignoreLocked);

        CraftSlimeWorld world;
        try {
            world = LoaderUtils.deserializeWorld(loader, worldName, serializedWorld, propertyMap, readOnly, ignoreLocked);
        } catch (Exception ex) {
            if (!readOnly) {
                loader.unlockWorld(worldName);
            }

            throw ex;
        }

        Logging.info("World " + worldName + " loaded in " + (System.currentTimeMillis() - start) + "ms.");
        return world;
    }

    public SlimeWorld createEmptyWorld(SlimeLoader loader, String worldName, SlimeWorld.SlimeProperties properties) throws WorldAlreadyExistsException, IOException {
        Objects.requireNonNull(properties, "Properties cannot be null");
        return this.createEmptyWorld(loader, worldName, properties.isReadOnly(), this.propertiesToMap(properties));
    }

    public SlimeWorld createEmptyWorld(SlimeLoader loader, String worldName, boolean readOnly, SlimePropertyMap propertyMap) throws WorldAlreadyExistsException, IOException {
        Objects.requireNonNull(loader, "Loader cannot be null");
        Objects.requireNonNull(worldName, "World name cannot be null");
        Objects.requireNonNull(propertyMap, "Properties cannot be null");
        if (loader.worldExists(worldName)) {
            throw new WorldAlreadyExistsException(worldName);
        } else {
            Logging.info("Creating empty world " + worldName + ".");
            long start = System.currentTimeMillis();
            CraftSlimeWorld world = new CraftSlimeWorld(loader, worldName, new HashMap(), new CompoundTag("", new CompoundMap()), new ArrayList(), this.nms.getWorldVersion(), propertyMap, readOnly, false, !readOnly);
            loader.saveWorld(worldName, world.serialize(), !readOnly);
            Logging.info("World " + worldName + " created in " + (System.currentTimeMillis() - start) + "ms.");
            return world;
        }
    }

    private SlimePropertyMap propertiesToMap(SlimeWorld.SlimeProperties properties) {
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

    public CompletableFuture<World> generateWorld(SlimeWorld world) {
        CompletableFuture<World> future = new CompletableFuture();
        Objects.requireNonNull(world, "SlimeWorld cannot be null");
        if (!world.isIgnoreLocked() && !world.isReadOnly() && !world.isLocked()) {
            throw new IllegalArgumentException("This world cannot be loaded, as it has not been locked.");
        } else {
            if (this.asyncWorldGen) {
                this.worldGeneratorService.submit(() -> this.nms.addWorldToServerList(this.nms.createNMSWorld(world)).whenComplete((value, ex) -> future.complete(value)));
            } else if (!Bukkit.isPrimaryThread()) {
                Bukkit.getScheduler().runTask(plugin, () -> this.nms.generateWorld(world).whenComplete((value, ex) -> future.complete(value)));
            } else {
                this.nms.generateWorld(world).whenComplete((value, ex) -> future.complete(value));
            }

            return future;
        }
    }

    public void migrateWorld(String worldName, SlimeLoader currentLoader, SlimeLoader newLoader) throws IOException, WorldInUseException, WorldAlreadyExistsException, UnknownWorldException {
        Objects.requireNonNull(worldName, "World name cannot be null");
        Objects.requireNonNull(currentLoader, "Current loader cannot be null");
        Objects.requireNonNull(newLoader, "New loader cannot be null");
        if (newLoader.worldExists(worldName)) {
            throw new WorldAlreadyExistsException(worldName);
        } else {
            World bukkitWorld = Bukkit.getWorld(worldName);
            boolean leaveLock = false;
            if (bukkitWorld != null) {
                CraftSlimeWorld slimeWorld = (CraftSlimeWorld) getNms().getSlimeWorld(bukkitWorld);
                if (slimeWorld != null && currentLoader.equals(slimeWorld.getLoader())) {
                    slimeWorld.setLoader(newLoader);
                    if (!slimeWorld.isReadOnly()) {
                        currentLoader.unlockWorld(worldName);
                        leaveLock = true;
                    }
                }
            }

            byte[] serializedWorld = currentLoader.loadWorld(worldName, false);
            newLoader.saveWorld(worldName, serializedWorld, leaveLock);
            currentLoader.deleteWorld(worldName);
        }
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

    public void importWorld(File worldDir, String worldName, SlimeLoader loader) throws WorldAlreadyExistsException, InvalidWorldException, WorldLoadedException, WorldTooBigException, IOException {
        Objects.requireNonNull(worldDir, "World directory cannot be null");
        Objects.requireNonNull(worldName, "World name cannot be null");
        Objects.requireNonNull(loader, "Loader cannot be null");
        if (loader.worldExists(worldName)) {
            throw new WorldAlreadyExistsException(worldName);
        } else {
            World bukkitWorld = Bukkit.getWorld(worldDir.getName());
            if (bukkitWorld != null && this.nms.getSlimeWorld(bukkitWorld) == null) {
                throw new WorldLoadedException(worldDir.getName());
            } else {
                CraftSlimeWorld world = WorldImporter.readFromDirectory(worldDir);

                byte[] serializedWorld;
                try {
                    serializedWorld = world.serialize();
                } catch (IndexOutOfBoundsException var8) {
                    throw new WorldTooBigException(worldDir.getName());
                }

                loader.saveWorld(worldName, serializedWorld, false);
            }
        }
    }

    public SlimeNMS getNms() {
        return this.nms;
    }

    @Override
    public void setProvider(WorldDbProvider provider) {
        this.worldDbProvider = provider;
    }

    public MinigamesCore getPlugin() {
        return plugin;
    }
}
