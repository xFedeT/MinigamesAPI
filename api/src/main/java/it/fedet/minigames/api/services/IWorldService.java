package it.fedet.minigames.api.services;

import it.fedet.minigames.api.world.WorldLoader;
import it.fedet.minigames.api.world.storage.IWorldDbProvider;
import it.fedet.minigames.api.world.storage.WorldStorageProvider;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public interface IWorldService {

    void setDbProvider(IWorldDbProvider dbProvider);

    CompletableFuture<World> loadWorld(String worldName);

    CompletableFuture<Void> unloadWorld(World world);

    CompletableFuture<Void> unloadWorld(String worldName);

    CompletableFuture<Boolean> worldExists(String worldName);

    CompletableFuture<Void> saveWorld(World world);

    WorldLoader getWorldLoader();

    WorldStorageProvider getStorageProvider();
}
