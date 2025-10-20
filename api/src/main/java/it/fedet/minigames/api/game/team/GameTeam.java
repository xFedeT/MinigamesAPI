package it.fedet.minigames.api.game.team;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.game.player.PlayerStatus;
import it.fedet.minigames.api.loadit.UserData;
import it.fedet.minigames.api.services.IPlayerService;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class GameTeam {

    private final int id;
    private final int gameID;
    protected final Map<Player, PlayerStatus> members = new ConcurrentHashMap<>();

    protected GameTeam(int id, int gameID) {
        this.id = id;
        this.gameID = gameID;
    }

    public <P extends JavaPlugin & Minigame<P>> void register(Player player, P plugin) {
        player.setMetadata("team_id", new FixedMetadataValue(plugin, id));
        if (id == -1) {
            members.put(player, PlayerStatus.SPECTATOR);
        } else {
            members.put(player, PlayerStatus.ALIVE);
        }
    }

    public <P extends JavaPlugin & Minigame<P>> void unregister(Player player, P plugin) {
        if (player.hasMetadata("team_id") && player.getMetadata("team_id").get(0).asInt() == id) {
            player.removeMetadata("team_id", plugin);
            members.remove(player);
        }
    }

    public int getId() {
        return id;
    }

    public boolean isInTeam(UserData player) {
        return members.containsKey(player);
    }


    public Collection<Player> getPlayers(PlayerStatus status) {
        return members.keySet().stream()
                .filter(userData -> status == PlayerStatus.INDIFFERENT || members.get(userData) == status)
                .filter(player -> player.hasMetadata("game-id") && player.getMetadata("game-id").get(0).asInt() == gameID)
                .collect(Collectors.toSet());
    }

    public void forEachOnlineTeamPlayer(Consumer<Player> consumer) {
        getPlayers(PlayerStatus.ALIVE).forEach(consumer);
    }

    public boolean isInTeam(Player player) {
        for (Player member : members.keySet()) {
            if (member.getName().equalsIgnoreCase(player.getName()))
                return true;
        }

        return false;
    }

    public int countMembers() {
        return members.size();
    }


}
