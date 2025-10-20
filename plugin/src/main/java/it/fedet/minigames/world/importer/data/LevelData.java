package it.fedet.minigames.world.importer.data;

import java.util.Map;

public class LevelData {

    private final int version;
    private final Map<String, String> gameRules;

    private final int spawnX;
    private final int spawnY;
    private final int spawnZ;

    public LevelData(int version, Map<String, String> gameRules, int spawnX, int spawnY, int spawnZ) {
        this.version = version;
        this.gameRules = gameRules;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public int getVersion() {
        return version;
    }

    public Map<String, String> getGameRules() {
        return gameRules;
    }
}
