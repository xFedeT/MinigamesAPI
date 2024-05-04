package it.fedet.minigames.api.game.team.criteria;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface DistributionCriteria {

    String getName();

    // Check first HIGHER teams in order to fill them
    DistributionFilter BASE_FILTER = teams -> teams.stream().filter(iTeam -> iTeam.getId() > -1)
            .sorted((Comparator.comparingInt(GameTeam::countMembers)).reversed())
            .collect(Collectors.toList());

    default DistributionFilter getFilter() {
        return BASE_FILTER;
    }

    <P extends JavaPlugin & Minigame<P>> DistributionResult distribute(Game<P> game,
                                                                       Player player,
                                                                       Collection<GameTeam> teams,
                                                                       int maxPlayersPerTeam,
                                                                       MinigamesAPI minigamesAPI);

    int getPriority();

    interface DistributionFilter {
        List<GameTeam> filter(Collection<GameTeam> teams);
    }

}
