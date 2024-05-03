package it.fedet.minigames.team;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.GameStatus;
import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import it.fedet.minigames.api.game.team.criteria.DistributionResult;
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
        this.criterias = criterias;

        this.criterias.sort((Comparator.comparingInt(DistributionCriteria::getPriority)).reversed());
    }

    public void populateTeams(int teamCount, int gameID) {
        for (int id = 0; id < teamCount; id++) {
            plugin.getMinigame().registerTeamProvider().getTeamInstance(id, gameID);
        }
    }

    @Override
    public <P extends JavaPlugin & Minigame<P>> void addIntoATeam(Player player, Game<P> game) {
        if (game.getGameStatus() != GameStatus.PLAYING || game.getGameStatus() != GameStatus.ENDING) {
            for (DistributionCriteria criteria : criterias) {
                Set<GameTeam> filteredTeams = new HashSet<>(criteria.getFilter().filter(teams.values()));

                DistributionResult result = criteria.distribute(game, player, filteredTeams, maxPlayersPerTeam, plugin);
                if (result == DistributionResult.SUCCESS) {

                    break;
                }
            }
        }
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
