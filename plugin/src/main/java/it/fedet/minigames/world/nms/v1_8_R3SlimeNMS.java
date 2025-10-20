package it.fedet.minigames.world.nms;

import it.fedet.minigames.api.world.events.WorldASyncLoadEvent;
import it.fedet.minigames.api.world.events.WorldSyncLoadEvent;
import it.fedet.minigames.world.map.SlimeProperties;
import it.fedet.minigames.world.nms.bridge.CraftCLSMBridge;
import it.fedet.minigames.world.nms.world.CraftSlimeWorld;
import it.fedet.minigames.world.nms.world.CustomWorldServer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.Event;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class v1_8_R3SlimeNMS {

    private static final Logger LOGGER = LogManager.getLogger("SWM");

    private final byte worldVersion = 0x01;

    private boolean loadingDefaultWorlds = true; // If true, the addWorld method will not be skipped

    private WorldServer defaultWorld;
    private WorldServer defaultNetherWorld;
    private WorldServer defaultEndWorld;

    public v1_8_R3SlimeNMS() {
        try {
            CraftCLSMBridge.initialize(this);
        }  catch (NoClassDefFoundError ex) {
            LOGGER.error("Failed to find ClassModifier classes. Are you sure you installed it correctly?");
            System.exit(1); // No ClassModifier, no party
        }
    }

    public void setDefaultWorlds(CraftSlimeWorld normalWorld, CraftSlimeWorld netherWorld, CraftSlimeWorld endWorld) {
        if (normalWorld != null) {
            World.Environment env = World.Environment.valueOf(normalWorld.getPropertyMap().getString(SlimeProperties.ENVIRONMENT).toUpperCase());

            if (env != World.Environment.NORMAL) {
                LOGGER.warn("The environment for the default world must always be 'NORMAL'.");
            }

            defaultWorld = new CustomWorldServer((CraftSlimeWorld) normalWorld, new CustomDataManager(normalWorld), 0);
        }

        if (netherWorld != null) {
            World.Environment env = World.Environment.valueOf(netherWorld.getPropertyMap().getString(SlimeProperties.ENVIRONMENT).toUpperCase());
            defaultNetherWorld = new CustomWorldServer((CraftSlimeWorld) netherWorld, new CustomDataManager(netherWorld), env.getId());
        }

        if (endWorld != null) {
            World.Environment env = World.Environment.valueOf(endWorld.getPropertyMap().getString(SlimeProperties.ENVIRONMENT).toUpperCase());
            defaultEndWorld = new CustomWorldServer((CraftSlimeWorld) endWorld, new CustomDataManager(endWorld), env.getId());
        }

        loadingDefaultWorlds = false;
    }

    public Object createNMSWorld(CraftSlimeWorld world) {
        CustomDataManager dataManager = new CustomDataManager(world);
        MinecraftServer mcServer = MinecraftServer.getServer();

        int dimension = CraftWorld.CUSTOM_DIMENSION_OFFSET + mcServer.worlds.size();
        boolean used = false;

        do {
            for (WorldServer server : mcServer.worlds) {
                used = server.dimension == dimension;

                if (used) {
                    dimension++;
                    break;
                }
            }
        } while (used);

        return new CustomWorldServer(world, dataManager, dimension);
    }

    public CompletableFuture<World> generateWorld(CraftSlimeWorld world) {
        CompletableFuture<World> future = new CompletableFuture();
        this.addWorldToServerList(this.createNMSWorld(world)).whenComplete((value, ex) -> future.complete(value));
        return future;
    }

    public CompletableFuture<World> addWorldToServerList(Object worldObject) {
        CompletableFuture<World> future = new CompletableFuture();
        if (!(worldObject instanceof WorldServer)) {
            throw new IllegalArgumentException("World object must be an instance of WorldServer!");
        } else {
            CustomWorldServer server = (CustomWorldServer)worldObject;
            String worldName = server.getWorldData().getName();
            World w = Bukkit.getWorld(worldName);
            if (w != null) {
                future.complete(w);
            } else {
                LOGGER.info("Loading world " + worldName);
                long startTime = System.currentTimeMillis();
                server.setReady(true);
                MinecraftServer mcServer = MinecraftServer.getServer();
                mcServer.server.addWorld(server.getWorld());
                mcServer.worlds.add(server);
                List<Event> events = Arrays.asList(new WorldInitEvent(server.getWorld()), new WorldLoadEvent(server.getWorld()), new WorldSyncLoadEvent(server.getWorld()));
                if (Bukkit.isPrimaryThread()) {
                    events.forEach((event) -> Bukkit.getPluginManager().callEvent(event));
                } else {
                    Bukkit.getPluginManager().callEvent(new WorldASyncLoadEvent(server.getWorld()));
                }

                LOGGER.info("World " + worldName + " loaded in " + (System.currentTimeMillis() - startTime) + "ms.");
                future.complete(server.getWorld());
            }

            return future;
        }
    }

    public CraftSlimeWorld getSlimeWorld(World world) {
        CraftWorld craftWorld = (CraftWorld) world;

        if (!(craftWorld.getHandle() instanceof CustomWorldServer worldServer)) {
            return null;
        }

        return worldServer.getSlimeWorld();
    }

    public byte getWorldVersion() {
        return worldVersion;
    }

    public WorldServer getDefaultEndWorld() {
        return defaultEndWorld;
    }

    public WorldServer getDefaultNetherWorld() {
        return defaultNetherWorld;
    }

    public WorldServer getDefaultWorld() {
        return defaultWorld;
    }

    public boolean isLoadingDefaultWorlds() {
        return loadingDefaultWorlds;
    }
}
