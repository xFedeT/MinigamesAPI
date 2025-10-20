package it.fedet.minigames.world.nms.chunk;

import com.flowpowered.nbt.*;
import it.fedet.minigames.world.nms.converter.Converter;
import net.minecraft.server.v1_8_R3.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class NMSSlimeChunk implements SlimeChunk {

    private Chunk chunk;

    public NMSSlimeChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public String getWorldName() {
        return chunk.getWorld().getWorldData().getName();
    }

    @Override
    public int getX() {
        return chunk.locX;
    }

    @Override
    public int getZ() {
        return chunk.locZ;
    }

    @Override
    public CraftSlimeChunkSection[] getSections() {
        CraftSlimeChunkSection[] sections = new CraftSlimeChunkSection[16];

        for (int sectionId = 0; sectionId < chunk.getSections().length; sectionId++) {
            ChunkSection section = chunk.getSections()[sectionId];

            if (section != null) {
                section.recalcBlockCounts();

                if (!section.a()) { // If the section is empty, just ignore it to save space
                    // Block Light Nibble Array
                    it.fedet.minigames.utils.NibbleArray blockLightArray = Converter.convertArray(section.getEmittedLightArray());

                    // Sky light Nibble Array
                    it.fedet.minigames.utils.NibbleArray skyLightArray = Converter.convertArray(section.getSkyLightArray());

                    // Block Data
                    byte[] blocks = new byte[4096];
                    it.fedet.minigames.utils.NibbleArray blockDataArray = new it.fedet.minigames.utils.NibbleArray(4096);

                    for (int i = 0; i < section.getIdArray().length; i++) {
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

    @Override
    public CompoundTag getHeightMaps() {
        CompoundTag heightMapsCompound = new CompoundTag("", new CompoundMap());
        heightMapsCompound.getValue().put("heightMap", new IntArrayTag("heightMap", chunk.heightMap));

        return heightMapsCompound;
    }

    @Override
    public int[] getBiomes() {
        return toIntArray(chunk.getBiomeIndex());
    }

    @Override
    public List<CompoundTag> getTileEntities() {
        List<CompoundTag> tileEntities = new ArrayList<>();

        for (TileEntity entity : chunk.getTileEntities().values()) {
            NBTTagCompound entityNbt = new NBTTagCompound();
            entity.b(entityNbt);
            tileEntities.add((CompoundTag) Converter.convertTag("", entityNbt));
        }

        return tileEntities;
    }

    @Override
    public List<CompoundTag> getEntities() {
        List<CompoundTag> entities = new ArrayList<>();

        for (int i = 0; i < chunk.getEntitySlices().length; i++) {
            for (Entity entity : chunk.getEntitySlices()[i]) {
                NBTTagCompound entityNbt = new NBTTagCompound();

                if (entity.d(entityNbt)) {
                    chunk.g(true);
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

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
