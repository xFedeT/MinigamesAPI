//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.nms.v1_8_R3;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.fedet.minigames.api.world.exceptions.UnknownWorldException;
import it.fedet.minigames.api.world.world.properties.WorldProperties;
import it.fedet.minigames.api.world.world.properties.WorldPropertyMap;
import it.fedet.minigames.world.nms.CraftSlimeWorld;
import net.minecraft.server.v1_8_R3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World.Environment;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomWorldServer extends WorldServer {
    private static final Logger LOGGER = LogManager.getLogger("SWM World");
    private static final ExecutorService WORLD_SAVER_SERVICE = Executors.newFixedThreadPool(4, (new ThreadFactoryBuilder()).setNameFormat("SWM Pool Thread #%1$d").build());
    private final CraftSlimeWorld slimeWorld;
    private final Object saveLock = new Object();
    private boolean ready = false;

    CustomWorldServer(CraftSlimeWorld world, IDataManager dataManager, int dimension) {
        super(MinecraftServer.getServer(), dataManager, dataManager.getWorldData(), dimension, MinecraftServer.getServer().methodProfiler, Environment.valueOf(world.getPropertyMap().getString(WorldProperties.ENVIRONMENT).toUpperCase()), null);
        this.b();
        this.slimeWorld = world;
        this.tracker = new EntityTracker(this);
        this.addIWorldAccess(new WorldManager(MinecraftServer.getServer(), this));
        WorldPropertyMap propertyMap = world.getPropertyMap();
        this.worldData.setDifficulty(EnumDifficulty.valueOf(propertyMap.getString(WorldProperties.DIFFICULTY).toUpperCase()));
        this.worldData.setSpawn(new BlockPosition(propertyMap.getInt(WorldProperties.SPAWN_X), propertyMap.getInt(WorldProperties.SPAWN_Y), propertyMap.getInt(WorldProperties.SPAWN_Z)));
        super.setSpawnFlags(propertyMap.getBoolean(WorldProperties.ALLOW_MONSTERS), propertyMap.getBoolean(WorldProperties.ALLOW_ANIMALS));
        super.getGameRules().set("doDaylightCycle", propertyMap.getBoolean(WorldProperties.DO_DAY_CYCLE).toString());
        this.pvpMode = propertyMap.getBoolean(WorldProperties.PVP);
        CustomChunkLoader chunkLoader = ((CustomDataManager) this.getDataManager()).getChunkLoader();
        chunkLoader.loadAllChunks(this);
    }

    public void save(boolean forceSave, IProgressUpdate progressUpdate) throws ExceptionWorldConflict {
        if (!this.slimeWorld.isReadOnly()) {
            super.save(forceSave, progressUpdate);
            if (MinecraftServer.getServer().isStopped()) {
                this.save();

                try {
                    this.slimeWorld.getLoader().unlockWorld(this.slimeWorld.getName());
                } catch (IOException ex) {
                    LOGGER.error("Failed to unlock the world " + this.slimeWorld.getName() + ". Please unlock it manually by using the command /swm manualunlock. Stack trace:");
                    ex.printStackTrace();
                } catch (UnknownWorldException var5) {
                }
            } else {
                WORLD_SAVER_SERVICE.execute(this::save);
            }
        }

    }

    private void save() {
        synchronized (this.saveLock) {
            try {
                LOGGER.info("Saving world " + this.slimeWorld.getName() + "...");
                long start = System.currentTimeMillis();
                byte[] serializedWorld = this.slimeWorld.serialize();
                this.slimeWorld.getLoader().saveWorld(this.slimeWorld.getName(), serializedWorld, false);
                LOGGER.info("World " + this.slimeWorld.getName() + " saved in " + (System.currentTimeMillis() - start) + "ms.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public CraftSlimeWorld getSlimeWorld() {
        return this.slimeWorld;
    }

    public boolean isReady() {
        return this.ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
