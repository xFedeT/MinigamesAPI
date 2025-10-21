//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.nms.v1_8_R3;

import it.fedet.minigames.api.swm.events.SlimeWorldASyncLoadEvent;
import it.fedet.minigames.api.swm.events.SlimeWorldSyncLoadEvent;
import it.fedet.minigames.api.swm.world.SlimeWorld;
import it.fedet.minigames.api.swm.world.properties.SlimeProperties;
import it.fedet.minigames.swm.nms.CraftSlimeWorld;
import it.fedet.minigames.swm.nms.SlimeNMS;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.Event;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class v1_8_R3SlimeNMS implements SlimeNMS {
    private static final Logger LOGGER = LogManager.getLogger("SWM");
    private final byte worldVersion = 1;
    private boolean loadingDefaultWorlds = true;
    private WorldServer defaultWorld;
    private WorldServer defaultNetherWorld;
    private WorldServer defaultEndWorld;

    public v1_8_R3SlimeNMS() {
        try {
            CraftCLSMBridge.initialize(this);
        } catch (NoClassDefFoundError var2) {
            LOGGER.error("Failed to find ClassModifier classes. Are you sure you installed it correctly?");
            System.exit(1);
        }

    }

    public void setDefaultWorlds(SlimeWorld normalWorld, SlimeWorld netherWorld, SlimeWorld endWorld) {
        if (normalWorld != null) {
            World.Environment env = Environment.valueOf(normalWorld.getPropertyMap().getString(SlimeProperties.ENVIRONMENT).toUpperCase());
            if (env != Environment.NORMAL) {
                LOGGER.warn("The environment for the default world must always be 'NORMAL'.");
            }

            this.defaultWorld = new CustomWorldServer((CraftSlimeWorld) normalWorld, new CustomDataManager(normalWorld), 0);
        }

        if (netherWorld != null) {
            World.Environment env = Environment.valueOf(netherWorld.getPropertyMap().getString(SlimeProperties.ENVIRONMENT).toUpperCase());
            this.defaultNetherWorld = new CustomWorldServer((CraftSlimeWorld) netherWorld, new CustomDataManager(netherWorld), env.getId());
        }

        if (endWorld != null) {
            World.Environment env = Environment.valueOf(endWorld.getPropertyMap().getString(SlimeProperties.ENVIRONMENT).toUpperCase());
            this.defaultEndWorld = new CustomWorldServer((CraftSlimeWorld) endWorld, new CustomDataManager(endWorld), env.getId());
        }

        this.loadingDefaultWorlds = false;
    }

    public Object createNMSWorld(SlimeWorld world) {
        CustomDataManager dataManager = new CustomDataManager(world);
        MinecraftServer mcServer = MinecraftServer.getServer();
        int dimension = 10 + mcServer.worlds.size();
        boolean used = false;

        do {
            for (WorldServer server : mcServer.worlds) {
                used = server.dimension == dimension;
                if (used) {
                    ++dimension;
                    break;
                }
            }
        } while (used);

        return new CustomWorldServer((CraftSlimeWorld) world, dataManager, dimension);
    }

    public CompletableFuture<World> generateWorld(SlimeWorld world) {
        CompletableFuture<World> future = new CompletableFuture();
        this.addWorldToServerList(this.createNMSWorld(world)).whenComplete((value, ex) -> future.complete(value));
        return future;
    }

    public CompletableFuture<World> addWorldToServerList(Object worldObject) {
        CompletableFuture<World> future = new CompletableFuture();
        if (!(worldObject instanceof WorldServer)) {
            throw new IllegalArgumentException("World object must be an instance of WorldServer!");
        } else {
            CustomWorldServer server = (CustomWorldServer) worldObject;
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
                List<Event> events = Arrays.asList(new WorldInitEvent(server.getWorld()), new WorldLoadEvent(server.getWorld()), new SlimeWorldSyncLoadEvent(server.getWorld()));
                if (Bukkit.isPrimaryThread()) {
                    events.forEach((event) -> Bukkit.getPluginManager().callEvent(event));
                } else {
                    Bukkit.getPluginManager().callEvent(new SlimeWorldASyncLoadEvent(server.getWorld()));
                }

                LOGGER.info("World " + worldName + " loaded in " + (System.currentTimeMillis() - startTime) + "ms.");
                future.complete(server.getWorld());
            }

            return future;
        }
    }

    public SlimeWorld getSlimeWorld(World world) {
        CraftWorld craftWorld = (CraftWorld) world;
        if (!(craftWorld.getHandle() instanceof CustomWorldServer worldServer)) {
            return null;
        } else {
            return worldServer.getSlimeWorld();
        }
    }

    public byte getWorldVersion() {
        Objects.requireNonNull(this);
        return 1;
    }

    public boolean isLoadingDefaultWorlds() {
        return this.loadingDefaultWorlds;
    }

    public WorldServer getDefaultWorld() {
        return this.defaultWorld;
    }

    public WorldServer getDefaultNetherWorld() {
        return this.defaultNetherWorld;
    }

    public WorldServer getDefaultEndWorld() {
        return this.defaultEndWorld;
    }
}
