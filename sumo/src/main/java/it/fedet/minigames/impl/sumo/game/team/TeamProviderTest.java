package it.fedet.minigames.impl.sumo.game.team;

import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import it.fedet.minigames.api.game.team.provider.TeamProvider;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamProviderTest implements TeamProvider {

    @Override
    public void onAddInTeam(Player player, GameTeam team) {

    }

    @Override
    public void onRemoveInTeam(Player player, GameTeam team) {

    }

    @Override
    public GameTeam getTeamInstance(int id, int gameID) {
        return new SumoTeam(id, gameID);
    }

    @Override
    public int getMaxPlayerPerTeams() {
        return 2;
    }

    @Override
    public List<DistributionCriteria> getCriterias() {
        return List.of(

        );
    }

    @Override
    public int teamQuantity() {
        return 6;
    }
}
