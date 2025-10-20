package it.fedet.minigames.world.nms.chunk;

import com.flowpowered.nbt.CompoundTag;

import java.util.List;

public class CraftSlimeChunk implements SlimeChunk {

    private final String worldName;
    private final int x;
    private final int z;

    private final CraftSlimeChunkSection[] sections;
    private final CompoundTag heightMaps;
    private final int[] biomes;
    private final List<CompoundTag> tileEntities;
    private final List<CompoundTag> entities;

    // Optional data for 1.13 world upgrading
    private CompoundTag upgradeData;

    public CraftSlimeChunk(String worldName, int x, int z, CraftSlimeChunkSection[] sections, CompoundTag heightMaps, int[] biomes, List<CompoundTag> tileEntities, List<CompoundTag> entities) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.sections = sections;
        this.heightMaps = heightMaps;
        this.biomes = biomes;
        this.tileEntities = tileEntities;
        this.entities = entities;
    }

    public CompoundTag getHeightMaps() {
        return heightMaps;
    }

    public CompoundTag getUpgradeData() {
        return upgradeData;
    }

    public List<CompoundTag> getTileEntities() {
        return tileEntities;
    }

    public CraftSlimeChunkSection[] getSections() {
        return sections;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int[] getBiomes() {
        return biomes;
    }

    public List<CompoundTag> getEntities() {
        return entities;
    }

    public String getWorldName() {
        return worldName;
    }
}
