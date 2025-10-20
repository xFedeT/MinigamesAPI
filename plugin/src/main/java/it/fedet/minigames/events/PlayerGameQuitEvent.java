package it.fedet.minigames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerGameQuitEvent extends PlayerEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public PlayerGameQuitEvent(Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
