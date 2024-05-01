package it.fedet.minigames.api.game.player;

import it.fedet.minigames.api.services.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayerService<T> extends Service {
    CompletableFuture<Optional<T>> getCachedOrAsync(String player);
}
