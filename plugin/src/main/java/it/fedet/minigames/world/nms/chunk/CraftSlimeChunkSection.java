package it.fedet.minigames.world.nms.chunk;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import it.fedet.minigames.utils.NibbleArray;

public class CraftSlimeChunkSection {

    // Pre 1.13 block data
    private final byte[] blocks;
    private final NibbleArray data;

    // Post 1.13 block data
    private final ListTag<CompoundTag> palette;
    private final long[] blockStates;

    private final NibbleArray blockLight;
    private final NibbleArray skyLight;

    public CraftSlimeChunkSection(byte[] blocks, NibbleArray data, ListTag<CompoundTag> palette, long[] blockStates, NibbleArray blockLight, NibbleArray skyLight) {
        this.blocks = blocks;
        this.data = data;
        this.palette = palette;
        this.blockStates = blockStates;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
    }

    public byte[] getBlocks() {
        return blocks;
    }

    public ListTag<CompoundTag> getPalette() {
        return palette;
    }

    public long[] getBlockStates() {
        return blockStates;
    }

    public NibbleArray getBlockLight() {
        return blockLight;
    }

    public NibbleArray getData() {
        return data;
    }

    public NibbleArray getSkyLight() {
        return skyLight;
    }
}
