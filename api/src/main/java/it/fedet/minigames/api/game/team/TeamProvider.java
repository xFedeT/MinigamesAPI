// api/src/main/java/it/fedet/minigames/api/game/team/provider/TeamProvider.java
package it.fedet.minigames.api.game.team;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import org.bukkit.plugin.java.JavaPlugin;

public interface TeamProvider<P extends JavaPlugin & Minigame<P>> {
    
    /**
     * Crea una nuova istanza di team
     */
    GameTeam createTeam(int teamId, int gameId);
    
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
    DistributionCriteria[] getAssignmentStrategy();
}