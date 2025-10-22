// plugin/src/main/java/it/fedet/minigames/team/TeamManagerImpl.java
package it.fedet.minigames.team;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.player.PlayerStatus;
import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.TeamManager;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import it.fedet.minigames.api.game.team.provider.TeamProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeamService implements TeamManager {

    private final MinigamesCore plugin;
    private final TeamProvider teamProvider;
    private final Map<Integer, Map<Integer, GameTeam>> gameTeams = new ConcurrentHashMap<>();
    private final Map<UUID, TeamAssignment> playerAssignments = new ConcurrentHashMap<>();

    public TeamService(MinigamesCore plugin, TeamProvider teamProvider) {
        this.plugin = plugin;
        this.teamProvider = teamProvider;
    }

    @Override
    public <P extends JavaPlugin & Minigame<P>> void initializeTeams(Game<P> game) {
        int gameId = game.getId();
        Map<Integer, GameTeam> teams = new HashMap<>();

        // Crea i team normali
        for (int i = 0; i < teamProvider.getTeamCount(); i++) {
            GameTeam team = teamProvider.createTeam(i, gameId);
            teams.put(i, team);
        }

        // Crea il team spectator
        GameTeam spectatorTeam = teamProvider.createSpectatorTeam(-1, gameId);
        teams.put(-1, spectatorTeam);

        gameTeams.put(gameId, teams);
    }

    @Override
    public <P extends JavaPlugin & Minigame<P>> boolean assignPlayerToTeam(Player player, Game<P> game) {
        int gameId = game.getId();
        Map<Integer, GameTeam> teams = gameTeams.get(gameId);

        if (teams == null) {
            return false;
        }

        // Determina lo status iniziale in base allo stato del game
        PlayerStatus initialStatus = switch (game.getGameStatus()) {
            case WAITING -> PlayerStatus.ALIVE;
            case PLAYING, ENDING -> PlayerStatus.SPECTATOR;
        };

        for (DistributionCriteria criteria : teamProvider.getAssignmentStrategy()) {
            Optional<GameTeam> selectedTeam = criteria.distribute(game, player, teams.values(), teamProvider.getMaxPlayersPerTeam(), plugin);
            if (selectedTeam.isEmpty()) {
                return false;
            } else {
                GameTeam team = selectedTeam.get();
                team.addPlayer(player, initialStatus);

                // Registra l'assegnazione
                playerAssignments.put(
                        player.getUniqueId(),
                        new TeamAssignment(gameId, team.getId())
                );
                return true;
            }
        }

        return false;
    }

    @Override
    public <P extends JavaPlugin & Minigame<P>> void removePlayerFromTeam(Player player, Game<P> game) {
        TeamAssignment assignment = playerAssignments.remove(player.getUniqueId());
        if (assignment == null) {
            return;
        }

        Map<Integer, GameTeam> teams = gameTeams.get(assignment.gameId);
        if (teams != null) {
            GameTeam team = teams.get(assignment.teamId);
            if (team != null) {
                team.removePlayer(player);
            }
        }
    }

    @Override
    public <P extends JavaPlugin & Minigame<P>> Optional<GameTeam> getPlayerTeam(Player player, Game<P> game) {
        TeamAssignment assignment = playerAssignments.get(player.getUniqueId());
        if (assignment == null || assignment.gameId != game.getId()) {
            return Optional.empty();
        }

        Map<Integer, GameTeam> teams = gameTeams.get(assignment.gameId);
        if (teams == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(teams.get(assignment.teamId));
    }

    @Override
    public Collection<GameTeam> getGameTeams(int gameId) {
        Map<Integer, GameTeam> teams = gameTeams.get(gameId);
        return teams != null ? teams.values() : Collections.emptyList();
    }

    @Override
    public void cleanupTeams(int gameId) {
        Map<Integer, GameTeam> teams = gameTeams.remove(gameId);
        if (teams != null) {
            // Rimuovi tutti i player assignments per questo game
            playerAssignments.entrySet().removeIf(
                    entry -> entry.getValue().gameId == gameId
            );
        }
    }

    private record TeamAssignment(int gameId, int teamId) {}
}