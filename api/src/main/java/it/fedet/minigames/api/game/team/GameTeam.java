// api/src/main/java/it/fedet/minigames/api/game/team/GameTeam.java
package it.fedet.minigames.api.game.team;

import it.fedet.minigames.api.game.player.PlayerStatus;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameTeam {

    private final int id;
    private final int gameId;
    protected final Map<UUID, PlayerStatus> members = new ConcurrentHashMap<>();

    protected GameTeam(int id, int gameId) {
        this.id = id;
        this.gameId = gameId;
    }

    /**
     * Aggiunge un giocatore al team
     */
    public void addPlayer(Player player, PlayerStatus status) {
        members.put(player.getUniqueId(), status);
        onPlayerAdded(player, status);
    }

    /**
     * Rimuove un giocatore dal team
     */
    public void removePlayer(Player player) {
        PlayerStatus oldStatus = members.remove(player.getUniqueId());
        if (oldStatus != null) {
            onPlayerRemoved(player, oldStatus);
        }
    }

    /**
     * Aggiorna lo status di un giocatore
     */
    public void updatePlayerStatus(Player player, PlayerStatus newStatus) {
        PlayerStatus oldStatus = members.put(player.getUniqueId(), newStatus);
        if (oldStatus != newStatus) {
            onPlayerStatusChanged(player, oldStatus, newStatus);
        }
    }

    /**
     * Ottiene lo status di un giocatore
     */
    public PlayerStatus getPlayerStatus(UUID playerId) {
        return members.getOrDefault(playerId, null);
    }

    /**
     * Verifica se un giocatore è nel team
     */
    public boolean hasPlayer(UUID playerId) {
        return members.containsKey(playerId);
    }

    /**
     * Ottiene tutti i giocatori con un certo status
     */
    public Collection<UUID> getPlayers(PlayerStatus status) {
        if (status == PlayerStatus.INDIFFERENT) {
            return members.keySet();
        }
        return members.entrySet().stream()
                .filter(entry -> entry.getValue() == status)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Conta i membri del team
     */
    public int size() {
        return members.size();
    }

    /**
     * Verifica se il team è vuoto
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    public int getId() {
        return id;
    }

    public int getGameId() {
        return gameId;
    }

    public boolean isSpectatorTeam() {
        return id == -1;
    }

    // Hook methods per le sottoclassi
    protected void onPlayerAdded(Player player, PlayerStatus status) {}
    protected void onPlayerRemoved(Player player, PlayerStatus oldStatus) {}
    protected void onPlayerStatusChanged(Player player, PlayerStatus oldStatus, PlayerStatus newStatus) {}
}