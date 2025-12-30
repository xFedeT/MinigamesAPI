// api/src/main/java/it/fedet/minigames/api/game/team/provider/TeamProvider.java
package it.fedet.minigames.api.game.team.provider;

import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;

import java.util.List;

public interface TeamProvider {

    /**
     * Crea una nuova istanza di team
     */
    GameTeam createTeam(int teamId, int gameId);

    /**
     * Crea una nuova istanza di team spettatore
     */
    GameTeam createSpectatorTeam(int teamId, int gameId);

    /**
     * Numero di team da creare per ogni game
     */
    int getTeamCount();

    /**
     * Numero massimo di giocatori per team
     */
    int getMaxPlayersPerTeam();

    /**
     * Strategia di assegnazione dei giocatori ai team
     */
    List<DistributionCriteria> getAssignmentStrategy();
}