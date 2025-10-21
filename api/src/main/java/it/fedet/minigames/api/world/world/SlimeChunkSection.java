//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.world;


import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import it.fedet.minigames.api.world.utils.NibbleArray;

public interface SlimeChunkSection {
    byte[] blocks();

    NibbleArray data();

    ListTag<CompoundTag> palette();

    long[] blockStates();

    NibbleArray blockLight();

    NibbleArray skyLight();
}
