package it.fedet.minigames.api.world.data;

import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Container for world data and metadata
 */
public class WorldData {
    
    private final String worldName;
    private final byte[] worldData;
    private final World.Environment environment;
    private final long seed;
    private final boolean generateStructures;
    
    public WorldData(String worldName, byte[] worldData, World.Environment environment, long seed, boolean generateStructures) {
        this.worldName = worldName;
        this.worldData = worldData;
        this.environment = environment;
        this.seed = seed;
        this.generateStructures = generateStructures;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public byte[] getWorldData() {
        return worldData;
    }
    
    public World.Environment getEnvironment() {
        return environment;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public boolean isGenerateStructures() {
        return generateStructures;
    }
    
    public WorldCreator toWorldCreator(String customName) {
        return new WorldCreator(customName)
                .environment(environment)
                .seed(seed)
                .generateStructures(generateStructures);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String worldName;
        private byte[] worldData;
        private World.Environment environment = World.Environment.NORMAL;
        private long seed = 0;
        private boolean generateStructures = false;
        
        public Builder worldName(String worldName) {
            this.worldName = worldName;
            return this;
        }
        
        public Builder worldData(byte[] worldData) {
            this.worldData = worldData;
            return this;
        }
        
        public Builder environment(World.Environment environment) {
            this.environment = environment;
            return this;
        }
        
        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }
        
        public Builder generateStructures(boolean generateStructures) {
            this.generateStructures = generateStructures;
            return this;
        }
        
        public WorldData build() {
            return new WorldData(worldName, worldData, environment, seed, generateStructures);
        }
    }
}