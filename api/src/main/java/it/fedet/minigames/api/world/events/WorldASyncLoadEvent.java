package it.fedet.minigames.api.world.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldASyncLoadEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final World world;

    public WorldASyncLoadEvent(World world) {
        this.world = world;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public World getWorld() {
        return this.world;
    }
}
