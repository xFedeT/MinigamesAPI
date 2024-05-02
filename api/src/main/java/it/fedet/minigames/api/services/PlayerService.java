package it.fedet.minigames.api.services;

import it.fedet.minigames.api.loadit.UserData;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayerService<T extends UserData> extends Service {
    T getPlayer(Player player);

    CompletableFuture<Optional<T>> getCachedOrAsync(String player);
}
