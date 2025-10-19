package it.fedet.minigames.world.uploader;

import it.fedet.minigames.api.world.data.WorldData;
import it.fedet.minigames.api.world.storage.WorldStorageProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class to upload existing worlds to storage
 */
public class WorldUploader {

    private final WorldStorageProvider storageProvider;
    private final Logger logger;

    public WorldUploader(WorldStorageProvider storageProvider, Logger logger) {
        this.storageProvider = storageProvider;
        this.logger = logger;
    }

    /**
     * Uploads a world from the server to storage
     */
    public CompletableFuture<Void> uploadWorld(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                World world = Bukkit.getWorld(worldName);
                
                if (world == null) {
                    // Try to load from file system
                    File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
                    if (!worldFolder.exists() || !worldFolder.isDirectory()) {
                        throw new IllegalArgumentException("World " + worldName + " not found");
                    }
                    
                    return uploadFromFolder(worldFolder, worldName, World.Environment.NORMAL, 0L, false);
                } else {
                    return uploadFromLoadedWorld(world);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to upload world " + worldName, e);
                throw new RuntimeException(e);
            }
        }).thenCompose(storageProvider::saveWorldData);
    }

    /**
     * Uploads a world from a loaded Bukkit World
     */
    private WorldData uploadFromLoadedWorld(World world) throws IOException {
        File worldFolder = world.getWorldFolder();
        
        byte[] compressedData = compressWorldData(worldFolder);

        return WorldData.builder()
                .worldName(world.getName())
                .worldData(compressedData)
                .environment(world.getEnvironment())
                .seed(world.getSeed())
                .generateStructures(world.canGenerateStructures())
                .build();
    }

    /**
     * Uploads a world from a folder
     */
    private WorldData uploadFromFolder(File worldFolder, String worldName, 
                                      World.Environment environment, long seed, 
                                      boolean generateStructures) throws IOException {
        byte[] compressedData = compressWorldData(worldFolder);

        return WorldData.builder()
                .worldName(worldName)
                .worldData(compressedData)
                .environment(environment)
                .seed(seed)
                .generateStructures(generateStructures)
                .build();
    }

    /**
     * Compresses world data into a byte array
     */
    private byte[] compressWorldData(File worldFolder) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos);
             DataOutputStream dos = new DataOutputStream(gzos)) {

            // Collect all files
            File[] files = collectWorldFiles(worldFolder);
            
            logger.info("Compressing " + files.length + " files from " + worldFolder.getName());
            
            // Write number of files
            dos.writeInt(files.length);

            int processedFiles = 0;
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
                
                processedFiles++;
                if (processedFiles % 100 == 0) {
                    logger.info("Processed " + processedFiles + "/" + files.length + " files");
                }
            }
            
            logger.info("Compression complete. Total size: " + (baos.size() / 1024 / 1024) + " MB");
        }

        return baos.toByteArray();
    }

    /**
     * Collects all files from a world folder
     */
    private File[] collectWorldFiles(File directory) throws IOException {
        return Files.walk(directory.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> {
                    String name = path.getFileName().toString();
                    // Skip session.lock, uid.dat, and other temporary files
                    return !name.equals("session.lock") 
                        && !name.equals("uid.dat")
                        && !name.endsWith(".tmp")
                        && !name.endsWith(".lock");
                })
                .map(Path::toFile)
                .toArray(File[]::new);
    }

    /**
     * Batch upload multiple worlds
     */
    public CompletableFuture<Void> uploadMultipleWorlds(String... worldNames) {
        CompletableFuture<?>[] futures = new CompletableFuture[worldNames.length];
        
        for (int i = 0; i < worldNames.length; i++) {
            final String worldName = worldNames[i];
            futures[i] = uploadWorld(worldName)
                    .thenRun(() -> logger.info("Successfully uploaded world: " + worldName))
                    .exceptionally(e -> {
                        logger.log(Level.SEVERE, "Failed to upload world: " + worldName, e);
                        return null;
                    });
        }
        
        return CompletableFuture.allOf(futures);
    }
}