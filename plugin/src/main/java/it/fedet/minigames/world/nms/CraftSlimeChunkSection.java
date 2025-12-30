//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.nms;


import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import it.fedet.minigames.api.world.utils.NibbleArray;
import it.fedet.minigames.api.world.world.SlimeChunkSection;

public record CraftSlimeChunkSection(byte[] blocks, NibbleArray data, ListTag<CompoundTag> palette, long[] blockStates,
                                     NibbleArray blockLight, NibbleArray skyLight) implements SlimeChunkSection {

}
