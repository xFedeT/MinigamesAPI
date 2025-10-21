package it.fedet.minigames.impl.sumo.game.team;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SumoTeam extends GameTeam {

    protected SumoTeam(int id, int gameID) {
        super(id, gameID);
    }

    @Override
    public <P extends JavaPlugin & Minigame<P>> void register(Player player, P plugin) {
        super.register(player, plugin);
    }

    @Override
    public <T extends JavaPlugin & Minigame<T>> void unregister(Player player, T plugin) {
        super.unregister(player, plugin);
    }
}
