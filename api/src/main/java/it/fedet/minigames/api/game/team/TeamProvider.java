package it.fedet.minigames.api.game.team;

import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;

import java.util.List;

public interface TeamProvider {

    void onAddInTeam();
    void onRemoveInTeam();
    GameTeam getTeamInstance(int id, int gameID);
    int getMaxPlayerPerTeams();

    List<DistributionCriteria> getCriterias();

    int teamQuantity();

}
