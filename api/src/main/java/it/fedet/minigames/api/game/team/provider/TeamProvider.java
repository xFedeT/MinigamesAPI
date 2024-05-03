package it.fedet.minigames.api.game.team.provider;

import it.fedet.minigames.api.game.team.GameTeam;
import it.fedet.minigames.api.game.team.criteria.DistributionCriteria;
import org.bukkit.entity.Player;

import java.util.List;

public interface TeamProvider {

    void onAddInTeam(Player player, GameTeam team);

    void onRemoveInTeam(Player player, GameTeam team);

    GameTeam getTeamInstance(int id, int gameID);

    int getMaxPlayerPerTeams();

    List<DistributionCriteria> getCriterias();

    int teamQuantity();

}
