package it.fedet.minigames.impl.sumo.game.team;

import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import it.fedet.minigames.api.game.team.provider.TeamProvider;
import it.fedet.minigames.impl.sumo.player.DistributionBase;


import java.util.List;

public class TeamProviderTest implements TeamProvider {


    @Override
    public GameTeam createTeam(int teamId, int gameId) {
        return new SumoTeam(teamId, gameId);
    }

    @Override
    public GameTeam createSpectatorTeam(int teamId, int gameId) {
        return new SpectatorTeam(teamId, gameId);
    }

    @Override
    public int getTeamCount() {
        return 1;
    }

    @Override
    public int getMaxPlayersPerTeam() {
        return 1;
    }

    @Override
    public List<DistributionCriteria> getAssignmentStrategy() {
        return List.of(
                new DistributionBase()
        );
    }
}
