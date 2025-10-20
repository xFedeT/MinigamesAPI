package it.fedet.minigames.events;

import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerGameJoinEvent extends PlayerEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Game<?> game;

    public PlayerGameJoinEvent(Player player, Game<?> game) {
        super(player);
        this.game = game;
    }

    public Game<?> getGame() {
        return game;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
