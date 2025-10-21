//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.swm.world;

import com.flowpowered.nbt.CompoundTag;

import java.util.List;

public interface SlimeChunk {
    String getWorldName();

    int getX();

    int getZ();

    SlimeChunkSection[] getSections();

    CompoundTag getHeightMaps();

    int[] getBiomes();

    List<CompoundTag> getTileEntities();

    List<CompoundTag> getEntities();
}
