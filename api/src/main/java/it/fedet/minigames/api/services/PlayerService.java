package it.fedet.minigames.api.services;

import it.fedet.minigames.api.loadit.UserData;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayerService<D extends UserData> extends Service {
    D getPlayer(Player player);

    CompletableFuture<Optional<D>> getCachedOrAsync(String player);
}
