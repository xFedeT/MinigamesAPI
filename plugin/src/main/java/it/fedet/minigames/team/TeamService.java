package it.fedet.minigames.team;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.GameStatus;
import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import it.fedet.minigames.api.game.team.criteria.DistributionResult;
import it.fedet.minigames.api.game.team.provider.TeamProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TeamService implements it.fedet.minigames.api.services.TeamService {

    private final List<DistributionCriteria> criterias;
    private final MinigamesCore plugin;
    private final Map<Integer, GameTeam> teams = new LinkedHashMap<>();
    private final int maxPlayersPerTeam;

    public TeamService(MinigamesCore plugin, int maxPlayersPerTeam, List<DistributionCriteria> criterias) {
        this.plugin = plugin;
        this.maxPlayersPerTeam = maxPlayersPerTeam;
        this.criterias = new LinkedList<>(criterias);

        this.criterias.sort((Comparator.comparingInt(DistributionCriteria::getPriority)).reversed());
    }

    public void populateTeams(int teamCount, int gameID) {
        TeamProvider teamProvider = plugin.getMinigame().registerTeamProvider();
        for (int id = 0; id < teamCount; id++) {
            GameTeam team = teamProvider.getTeamInstance(id, gameID);
            teams.put(id, team);
        }

        //SPECTATOR
        teams.put(-1, teamProvider.getTeamInstance(-1, gameID));
    }

    @Override
    public <P extends JavaPlugin & Minigame<P>> boolean addIntoATeam(Player player, Game<P> game) {
        if (game.getGameStatus() == GameStatus.WAITING) {
            for (DistributionCriteria criteria : criterias) {
                Set<GameTeam> filteredTeams = new HashSet<>(criteria.getFilter().filter(teams.values()));

                DistributionResult result = criteria.distribute(game, player, filteredTeams, maxPlayersPerTeam, plugin);
                if (result == DistributionResult.SUCCESS) {
                    return true;
                } else if (result == DistributionResult.FAILURE) {
                    return false;
                }
            }
        } else if (game.getGameStatus() == GameStatus.PLAYING || game.getGameStatus() == GameStatus.ENDING) {
            teams.get(-1).register(player, game.getPlugin());
            return true;
            //REGISTER AS SPECTATOR
        }
       return false;
    }

    @Override
    public int getMaxPlayersPerTeam() {
        return maxPlayersPerTeam;
    }

    @Override
    public GameTeam getTeam(int id) {
        return teams.get(id);
    }

    @Override
    public Map<Integer, GameTeam> getTeams() {
        return teams;
    }
}