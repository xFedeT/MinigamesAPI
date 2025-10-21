//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.unlocker;

import java.io.IOException;

import it.fedet.minigames.api.world.exception.UnknownWorldException;
import it.fedet.minigames.world.nms.world.CraftSlimeWorld;
import it.fedet.minigames.world.service.WorldService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

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
        CraftSlimeWorld world = worldService.getNms().getSlimeWorld(event.getWorld());
        if (world != null) {
            Bukkit.getScheduler().runTaskAsynchronously(worldService.getPlugin(), () -> this.unlockWorld(world));
        }

    }

    private void unlockWorld(CraftSlimeWorld world) {
        try {
            world.getLoader().unlockWorld(world.getName());
        } catch (IOException ex) {
            Bukkit.getLogger().severe("Failed to unlock world " + world.getName() + ". Retrying in 5 seconds. Stack trace:");
            ex.printStackTrace();
            Bukkit.getScheduler().runTaskLaterAsynchronously(worldService.getPlugin(), () -> this.unlockWorld(world), 100L);
        } catch (UnknownWorldException var4) {
        }

    }
}
