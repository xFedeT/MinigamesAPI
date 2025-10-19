package it.fedet.minigames.world.loader;

import it.fedet.minigames.api.world.WorldLoader;
import it.fedet.minigames.api.world.data.WorldData;
import it.fedet.minigames.api.world.storage.WorldStorageProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class AsyncWorldLoader implements WorldLoader {

    private final JavaPlugin plugin;
    private final WorldStorageProvider storageProvider;
    private final Map<String, World> loadedWorlds = new ConcurrentHashMap<>();

    public AsyncWorldLoader(JavaPlugin plugin, WorldStorageProvider storageProvider) {
        this.plugin = plugin;
        this.storageProvider = storageProvider;
        this.storageProvider.initialize();
    }

    @Override
    public CompletableFuture<World> loadWorld(String worldName) {

        // Check if already loaded
        if (loadedWorlds.containsKey(worldName)) {
            return CompletableFuture.completedFuture(loadedWorlds.get(worldName));
        }

        return storageProvider.getWorldData(worldName)
                .thenCompose(optWorldData -> {
                    if (optWorldData.isEmpty()) {
                        return CompletableFuture.failedFuture(
                                new IllegalArgumentException("World " + worldName + " not found in storage")
                        );
                    }

                    WorldData worldData = optWorldData.get();
                    
                    // Extract world to temporary directory asynchronously
                    return extractWorldData(worldName, worldData)
                            .thenCompose(success -> {
                                if (!success) {
                                    return CompletableFuture.failedFuture(
                                            new IOException("Failed to extract world data")
                                    );
                                }

                                // Load world on main thread
                                return loadWorldSync(worldName, worldData);
                            });
                });
    }

    private CompletableFuture<Boolean> extractWorldData(String worldName, WorldData worldData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
                
                // Delete existing world folder if present
                if (worldFolder.exists()) {
                    deleteDirectory(worldFolder.toPath());
                }

                worldFolder.mkdirs();

                // Decompress and extract world data
                decompressWorldData(worldData.getWorldData(), worldFolder);

                return true;
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to extract world data for " + worldName, e);
                return false;
            }
        });
    }

    private void decompressWorldData(byte[] compressedData, File destination) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzis = new GZIPInputStream(bais);
             DataInputStream dis = new DataInputStream(gzis)) {

            // Read number of files
            int fileCount = dis.readInt();

            for (int i = 0; i < fileCount; i++) {
                // Read file path
                String relativePath = dis.readUTF();
                
                // Read file size
                int fileSize = dis.readInt();
                
                // Read file data
                byte[] fileData = new byte[fileSize];
                dis.readFully(fileData);

                // Write file
                File file = new File(destination, relativePath);
                file.getParentFile().mkdirs();
                
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(fileData);
                }
            }
        }
    }

    private CompletableFuture<World> loadWorldSync(String worldName, WorldData worldData) {
        CompletableFuture<World> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                WorldCreator creator = worldData.toWorldCreator(worldName);
                World world = creator.createWorld();

                if (world == null) {
                    future.completeExceptionally(new IllegalStateException("Failed to create world"));
                    return;
                }

                world.setAutoSave(false);
                loadedWorlds.put(worldName, world);
                
                plugin.getLogger().info("Successfully loaded world: " + worldName);
                future.complete(world);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load world " + worldName, e);
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<Void> unloadWorld(World world) {
        return unloadWorld(world.getName());
    }

    @Override
    public CompletableFuture<Void> unloadWorld(String worldName) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                World world = loadedWorlds.remove(worldName);
                
                if (world != null) {
                    // Kick all players from world
                    world.getPlayers().forEach(p -> 
                        p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation())
                    );

                    // Unload world
                    boolean unloaded = Bukkit.unloadWorld(world, false);
                    
                    if (!unloaded) {
                        plugin.getLogger().warning("Failed to unload world: " + worldName);
                    }
                }

                // Delete world folder
                File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
                if (worldFolder.exists()) {
                    deleteDirectory(worldFolder.toPath());
                }

                plugin.getLogger().info("Successfully unloaded world: " + worldName);
                future.complete(null);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to unload world " + worldName, e);
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<Boolean> worldExists(String worldName) {
        return storageProvider.exists(worldName);
    }

    @Override
    public CompletableFuture<Void> saveWorld(World world) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File worldFolder = world.getWorldFolder();
                
                // Compress world data
                byte[] compressedData = compressWorldData(worldFolder);

                WorldData worldData = WorldData.builder()
                        .worldName(world.getName())
                        .worldData(compressedData)
                        .environment(world.getEnvironment())
                        .seed(world.getSeed())
                        .generateStructures(world.canGenerateStructures())
                        .build();

                return worldData;
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to compress world data", e);
                throw new RuntimeException(e);
            }
        }).thenCompose(storageProvider::saveWorldData);
    }

    private byte[] compressWorldData(File worldFolder) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos);
             DataOutputStream dos = new DataOutputStream(gzos)) {

            // Collect all files
            File[] files = collectWorldFiles(worldFolder);
            
            // Write number of files
            dos.writeInt(files.length);

            for (File file : files) {
                String relativePath = worldFolder.toPath().relativize(file.toPath()).toString();
                
                // Write file path
                dos.writeUTF(relativePath);
                
                // Read file data
                byte[] fileData = Files.readAllBytes(file.toPath());
                
                // Write file size
                dos.writeInt(fileData.length);
                
                // Write file data
                dos.write(fileData);
            }
        }

        return baos.toByteArray();
    }

    private File[] collectWorldFiles(File directory) throws IOException {
        return Files.walk(directory.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> {
                    String name = path.getFileName().toString();
                    // Skip session.lock and uid.dat
                    return !name.equals("session.lock") && !name.equals("uid.dat");
                })
                .map(Path::toFile)
                .toArray(File[]::new);
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            plugin.getLogger().log(Level.WARNING, "Failed to delete " + p, e);
                        }
                    });
        }
    }

    public void shutdown() {
        // Unload all worlds
        loadedWorlds.keySet().forEach(this::unloadWorld);
        
        // Close storage provider
        storageProvider.close();
    }
}