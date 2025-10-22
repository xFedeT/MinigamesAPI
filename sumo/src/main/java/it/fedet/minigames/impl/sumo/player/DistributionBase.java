package it.fedet.minigames.impl.sumo.player;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.player.PlayerStatus;
import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import it.fedet.minigames.api.game.team.criteria.DistributionResult;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class DistributionBase implements DistributionCriteria {
    @Override
    public String getName() {
        return "Base Distribution";
    }

    @Override
    public <P extends JavaPlugin & Minigame<P>> Optional<GameTeam> distribute(Game<P> game, Player player, Collection<GameTeam> teams, int maxPlayersPerTeam, MinigamesAPI minigamesAPI) {
        if (teams == null || teams.isEmpty()) {
            return Optional.empty();
        }

        return teams.stream()
                .filter(team -> team.getPlayers(PlayerStatus.INDIFFERENT).size() < maxPlayersPerTeam)
                .min(Comparator.comparingInt(team -> team.getPlayers(PlayerStatus.INDIFFERENT).size()));
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
