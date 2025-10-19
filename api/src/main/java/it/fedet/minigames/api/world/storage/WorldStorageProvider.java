package it.fedet.minigames.api.world.storage;


import it.fedet.minigames.api.world.data.WorldData;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Provider interface for storing and retrieving world data from different storage backends
 */
public interface WorldStorageProvider {

    /**
     * Retrieves world data from storage
     *
     * @param worldName the name of the world
     * @return CompletableFuture containing optional WorldData
     */
    CompletableFuture<Optional<WorldData>> getWorldData(String worldName);

    /**
     * Saves world data to storage
     *
     * @param worldData the world data to save
     * @return CompletableFuture that completes when save is done
     */
    CompletableFuture<Void> saveWorldData(WorldData worldData);

    /**
     * Checks if a world exists in storage
     *
     * @param worldName the name of the world
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> exists(String worldName);

    /**
     * Deletes world data from storage
     *
     * @param worldName the name of the world
     * @return CompletableFuture that completes when deletion is done
     */
    CompletableFuture<Void> deleteWorldData(String worldName);

    /**
     * Initializes the storage provider
     */
    void initialize();

    /**
     * Closes the storage provider and releases resources
     */
    void close();
}