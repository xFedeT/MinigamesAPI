package it.fedet.minigames.impl.sumo.game.team;

import it.fedet.minigames.api.game.player.PlayerStatus;
import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import it.fedet.minigames.api.game.team.provider.TeamProvider;
import it.fedet.minigames.impl.sumo.player.DistributionBase;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamProviderTest implements TeamProvider {

    @Override
    public void onAddInTeam(Player player, GameTeam team) {
        team.getPlayers(PlayerStatus.INDIFFERENT).add(player);
    }

    @Override
    public void onRemoveInTeam(Player player, GameTeam team) {
        team.getPlayers(PlayerStatus.INDIFFERENT).remove(player);
    }

    @Override
    public GameTeam getTeamInstance(int id, int gameID) {
        return new SumoTeam(id, gameID);
    }

    @Override
    public int getMaxPlayerPerTeams() {
        return 1;
    }

    @Override
    public List<DistributionCriteria> getCriterias() {
        return List.of(
                new DistributionBase()
        );
    }

    @Override
    public int teamQuantity() {
        return 6;
    }
}
