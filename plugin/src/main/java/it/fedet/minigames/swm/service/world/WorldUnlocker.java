//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.service.world;

import it.fedet.minigames.api.swm.exceptions.UnknownWorldException;
import it.fedet.minigames.api.swm.world.SlimeWorld;
import it.fedet.minigames.swm.service.WorldService;
import it.fedet.minigames.swm.service.log.Logging;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.IOException;

public class WorldUnlocker implements Listener {

    private final WorldService worldService;

    public WorldUnlocker(WorldService worldService) {
        this.worldService = worldService;
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onWorldUnload(WorldUnloadEvent event) {
        SlimeWorld world = worldService.getNms().getSlimeWorld(event.getWorld());
        if (world != null) {
            Bukkit.getScheduler().runTaskAsynchronously(worldService.getPlugin(), () -> this.unlockWorld(world));
        }

    }

    private void unlockWorld(SlimeWorld world) {
        try {
            world.getLoader().unlockWorld(world.getName());
        } catch (IOException ex) {
            Logging.error("Failed to unlock world " + world.getName() + ". Retrying in 5 seconds. Stack trace:");
            ex.printStackTrace();
            Bukkit.getScheduler().runTaskLaterAsynchronously(worldService.getPlugin(), () -> this.unlockWorld(world), 100L);
        } catch (UnknownWorldException var4) {
        }

    }
}
