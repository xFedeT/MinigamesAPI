//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.nms;

import com.flowpowered.nbt.CompoundTag;
import it.fedet.minigames.api.world.world.SlimeChunk;
import it.fedet.minigames.api.world.world.SlimeChunkSection;

import java.util.List;

public class CraftSlimeChunk implements SlimeChunk {
    private final String worldName;
    private final int x;
    private final int z;
    private final SlimeChunkSection[] sections;
    private final CompoundTag heightMaps;
    private final int[] biomes;
    private final List<CompoundTag> tileEntities;
    private final List<CompoundTag> entities;
    private CompoundTag upgradeData;

    CraftSlimeChunk(String worldName, SlimeChunk base) {
        this.worldName = worldName;
        this.x = base.getX();
        this.z = base.getZ();
        this.sections = base.getSections();
        this.heightMaps = base.getHeightMaps();
        this.biomes = base.getBiomes();
        this.tileEntities = base.getTileEntities();
        this.entities = base.getEntities();
        if (base instanceof CraftSlimeChunk) {
            this.upgradeData = ((CraftSlimeChunk) base).getUpgradeData();
        } else {
            this.upgradeData = null;
        }

    }

    public String getWorldName() {
        return this.worldName;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public SlimeChunkSection[] getSections() {
        return this.sections;
    }

    public CompoundTag getHeightMaps() {
        return this.heightMaps;
    }

    public int[] getBiomes() {
        return this.biomes;
    }

    public List<CompoundTag> getTileEntities() {
        return this.tileEntities;
    }

    public List<CompoundTag> getEntities() {
        return this.entities;
    }

    public CompoundTag getUpgradeData() {
        return this.upgradeData;
    }

    public CraftSlimeChunk(String worldName, int x, int z, SlimeChunkSection[] sections, CompoundTag heightMaps, int[] biomes, List<CompoundTag> tileEntities, List<CompoundTag> entities) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.sections = sections;
        this.heightMaps = heightMaps;
        this.biomes = biomes;
        this.tileEntities = tileEntities;
        this.entities = entities;
    }

    public CraftSlimeChunk(String worldName, int x, int z, SlimeChunkSection[] sections, CompoundTag heightMaps, int[] biomes, List<CompoundTag> tileEntities, List<CompoundTag> entities, CompoundTag upgradeData) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.sections = sections;
        this.heightMaps = heightMaps;
        this.biomes = biomes;
        this.tileEntities = tileEntities;
        this.entities = entities;
        this.upgradeData = upgradeData;
    }
}
