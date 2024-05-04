package it.fedet.minigames.impl.sumo.game.team;

import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SumoTeam extends GameTeam {

    protected SumoTeam(int id, int gameID) {
        super(id, gameID);
    }


    @Override
    public <T extends JavaPlugin & MinigamesAPI> void unregister(Player player, T plugin) {
        super.register(player, plugin);
    }
}
