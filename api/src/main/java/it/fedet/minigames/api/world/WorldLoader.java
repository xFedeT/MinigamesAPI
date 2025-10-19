package it.fedet.minigames.api.world;

import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for loading and managing worlds from external storage
 */
public interface WorldLoader {

    /**
     * Loads a world asynchronously from storage
     *
     * @param worldName the name of the world to load
     * @return CompletableFuture containing the loaded world
     */
    CompletableFuture<World> loadWorld(String worldName);

    /**
     * Unloads a world and removes it from disk
     *
     * @param world the world to unload
     * @return CompletableFuture that completes when unload is done
     */
    CompletableFuture<Void> unloadWorld(World world);

    /**
     * Unloads a world by name
     *
     * @param worldName the name of the world to unload
     * @return CompletableFuture that completes when unload is done
     */
    CompletableFuture<Void> unloadWorld(String worldName);

    /**
     * Checks if a world exists in storage
     *
     * @param worldName the name of the world
     * @return CompletableFuture containing true if world exists
     */
    CompletableFuture<Boolean> worldExists(String worldName);

    /**
     * Saves current world state to storage (optional, for backup)
     *
     * @param world the world to save
     * @return CompletableFuture that completes when save is done
     */
    CompletableFuture<Void> saveWorld(World world);
}