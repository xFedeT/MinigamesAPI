package it.fedet.minigames.api.game.team;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Optional;

public interface TeamManager {

    /**
     * Assegna un giocatore a un team
     */
    <P extends JavaPlugin & Minigame<P>> boolean assignPlayerToTeam(Player player, Game<P> game);

    /**
     * Rimuove un giocatore da un team
     */
    <P extends JavaPlugin & Minigame<P>> void removePlayerFromTeam(Player player, Game<P> game);

    /**
     * Ottiene il team di un giocatore
     */
    <P extends JavaPlugin & Minigame<P>> Optional<GameTeam> getPlayerTeam(Player player, Game<P> game);

    /**
     * Ottiene tutti i team di un game
     */
    Collection<GameTeam> getGameTeams(int gameId);

    /**
     * Crea i team per un game
     */
    <P extends JavaPlugin & Minigame<P>> void initializeTeams(Game<P> game);

    /**
     * Pulisce i team di un game
     */
    void cleanupTeams(int gameId);
}