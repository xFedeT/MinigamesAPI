package it.fedet.minigames.player;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.loadit.UserData;
import it.fedet.minigames.api.services.IPlayerService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayerService<U extends UserData> implements IPlayerService<U> {

    private final MinigamesCore plugin;

    public PlayerService(MinigamesCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }

    @Override
    public U getPlayer(Player player) {
        return (U) plugin.getService(DatabaseProvider.class).getPlayerDataLoadit().getContainer().getCached(player);
    }

    @Override
    public CompletableFuture<Optional<U>> getCachedOrAsync(String player) {
        Player onlinePlayer = Bukkit.getPlayerExact(player);
        if (onlinePlayer == null)
            return plugin.getService(DatabaseProvider.class).getPlayerDataLoadit().getContainer().get(player);

        return CompletableFuture.completedFuture((Optional<U>) Optional.of(plugin.getService(DatabaseProvider.class).getPlayerDataLoadit().getContainer().getCached(onlinePlayer)));
    }
}
