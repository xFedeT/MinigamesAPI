//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.nms.v1_8_R3;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntArrayTag;
import it.fedet.minigames.api.world.utils.NibbleArray;
import it.fedet.minigames.api.world.world.SlimeChunk;
import it.fedet.minigames.api.world.world.SlimeChunkSection;
import it.fedet.minigames.world.nms.CraftSlimeChunkSection;
import net.minecraft.server.v1_8_R3.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class NMSSlimeChunk implements SlimeChunk {
    private Chunk chunk;

    public String getWorldName() {
        return this.chunk.getWorld().getWorldData().getName();
    }

    public int getX() {
        return this.chunk.locX;
    }

    public int getZ() {
        return this.chunk.locZ;
    }

    public SlimeChunkSection[] getSections() {
        SlimeChunkSection[] sections = new SlimeChunkSection[16];

        for (int sectionId = 0; sectionId < this.chunk.getSections().length; ++sectionId) {
            ChunkSection section = this.chunk.getSections()[sectionId];
            if (section != null) {
                section.recalcBlockCounts();
                if (!section.a()) {
                    NibbleArray blockLightArray = Converter.convertArray(section.getEmittedLightArray());
                    NibbleArray skyLightArray = new NibbleArray(4096);
                    if (section.getSkyLightArray() != null) {
                        skyLightArray = Converter.convertArray(section.getSkyLightArray());
                    }

                    byte[] blocks = new byte[4096];
                    NibbleArray blockDataArray = new NibbleArray(4096);

                    for (int i = 0; i < section.getIdArray().length; ++i) {
                        char packed = section.getIdArray()[i];
                        blocks[i] = (byte) (packed >> 4 & 255);
                        blockDataArray.set(i, packed & 15);
                    }

                    sections[sectionId] = new CraftSlimeChunkSection(blocks, blockDataArray, null, null, blockLightArray, skyLightArray);
                }
            }
        }

        return sections;
    }

    public CompoundTag getHeightMaps() {
        CompoundTag heightMapsCompound = new CompoundTag("", new CompoundMap());
        heightMapsCompound.getValue().put("heightMap", new IntArrayTag("heightMap", this.chunk.heightMap));
        return heightMapsCompound;
    }

    public int[] getBiomes() {
        return toIntArray(this.chunk.getBiomeIndex());
    }

    public List<CompoundTag> getTileEntities() {
        List<CompoundTag> tileEntities = new ArrayList();

        for (TileEntity entity : this.chunk.getTileEntities().values()) {
            NBTTagCompound entityNbt = new NBTTagCompound();
            entity.b(entityNbt);
            tileEntities.add((CompoundTag) Converter.convertTag("", entityNbt));
        }

        return tileEntities;
    }

    public List<CompoundTag> getEntities() {
        List<CompoundTag> entities = new ArrayList();

        for (int i = 0; i < this.chunk.getEntitySlices().length; ++i) {
            for (Entity entity : this.chunk.getEntitySlices()[i]) {
                NBTTagCompound entityNbt = new NBTTagCompound();
                if (entity.d(entityNbt)) {
                    this.chunk.g(true);
                    entities.add((CompoundTag) Converter.convertTag("", entityNbt));
                }
            }
        }

        return entities;
    }

    private static int[] toIntArray(byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN);
        int[] ret = new int[buf.length / 4];
        buffer.asIntBuffer().get(ret);
        return ret;
    }

    public Chunk getChunk() {
        return this.chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NMSSlimeChunk other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$chunk = this.getChunk();
                Object other$chunk = other.getChunk();
                if (this$chunk == null) {
                    return other$chunk == null;
                } else return this$chunk.equals(other$chunk);
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NMSSlimeChunk;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $chunk = this.getChunk();
        result = result * 59 + ($chunk == null ? 43 : $chunk.hashCode());
        return result;
    }

    public String toString() {
        return "NMSSlimeChunk(chunk=" + this.getChunk() + ")";
    }

    public NMSSlimeChunk(Chunk chunk) {
        this.chunk = chunk;
    }
}
