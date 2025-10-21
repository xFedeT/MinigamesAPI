//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.nms.v1_8_R3;

import com.flowpowered.nbt.CompoundTag;
import it.fedet.minigames.api.swm.utils.NibbleArray;
import it.fedet.minigames.api.swm.world.SlimeChunk;
import it.fedet.minigames.api.swm.world.SlimeChunkSection;
import it.fedet.minigames.swm.nms.CraftSlimeWorld;
import net.minecraft.server.v1_8_R3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class CustomChunkLoader implements IChunkLoader {
    private static final Logger LOGGER = LogManager.getLogger("SWM Chunk Loader");
    private final CraftSlimeWorld world;

    void loadAllChunks(CustomWorldServer server) {
        for (SlimeChunk chunk : new ArrayList<>(this.world.getChunks().values())) {
            Chunk nmsChunk = this.createChunk(server, chunk);
            this.world.updateChunk(new NMSSlimeChunk(nmsChunk));
        }

    }

    private Chunk createChunk(CustomWorldServer server, SlimeChunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();
        LOGGER.debug("Loading chunk (" + x + ", " + z + ") on world " + this.world.getName());
        Chunk nmsChunk = new Chunk(server, x, z);
        nmsChunk.d(true);
        nmsChunk.e(true);
        CompoundTag heightMapsCompound = chunk.getHeightMaps();
        int[] heightMap = heightMapsCompound.getIntArrayValue("heightMap").get();
        nmsChunk.a(heightMap);
        LOGGER.debug("Loading chunk sections for chunk (" + x + ", " + z + ") on world " + this.world.getName());
        ChunkSection[] sections = new ChunkSection[16];

        for (int sectionId = 0; sectionId < chunk.getSections().length; ++sectionId) {
            SlimeChunkSection slimeSection = chunk.getSections()[sectionId];
            if (slimeSection != null) {
                ChunkSection section = new ChunkSection(sectionId << 4, true);
                NibbleArray data = slimeSection.data();
                byte[] blocks = slimeSection.blocks();
                char[] blockIds = new char[blocks.length];

                for (int id = 0; id < blocks.length; ++id) {
                    int blockId = blocks[id] & 255;
                    int blockData = data.get(id);
                    int packed = blockId << 4 | blockData;
                    if (Block.d.a(packed) == null) {
                        Block block = Block.getById(blockId);
                        if (block != null) {
                            try {
                                blockData = block.toLegacyData(block.fromLegacyData(blockData));
                            } catch (Exception var21) {
                                blockData = block.toLegacyData(block.getBlockData());
                            }

                            packed = blockId << 4 | blockData;
                        }
                    }

                    blockIds[id] = (char) packed;
                }

                LOGGER.debug("ChunkSection #" + sectionId + " - Chunk (" + x + ", " + z + ") - World " + this.world.getName() + ":");
                LOGGER.debug("Blocks:");
                LOGGER.debug(blockIds);
                LOGGER.debug("Block light array:");
                LOGGER.debug(slimeSection.blockLight() != null ? slimeSection.blockLight().getBacking() : "Not present");
                LOGGER.debug("Sky light array:");
                LOGGER.debug(slimeSection.skyLight() != null ? slimeSection.skyLight().getBacking() : "Not present");
                section.a(blockIds);
                if (slimeSection.blockLight() != null) {
                    section.a(Converter.convertArray(slimeSection.blockLight()));
                }

                if (slimeSection.skyLight() != null) {
                    section.b(Converter.convertArray(slimeSection.skyLight()));
                }

                section.recalcBlockCounts();
                sections[sectionId] = section;
            }
        }

        nmsChunk.a(sections);
        nmsChunk.a(this.toByteArray(chunk.getBiomes()));
        LOGGER.debug("Loading tile entities for chunk (" + x + ", " + z + ") on world " + this.world.getName());
        List<CompoundTag> tileEntities = chunk.getTileEntities();
        int loadedEntities = 0;
        if (tileEntities != null) {
            for (CompoundTag tag : tileEntities) {
                TileEntity entity = TileEntity.c((NBTTagCompound) Converter.convertTag(tag));
                if (entity != null) {
                    nmsChunk.a(entity);
                    ++loadedEntities;
                }
            }
        }

        LOGGER.debug("Loaded " + loadedEntities + " tile entities for chunk (" + x + ", " + z + ") on world " + this.world.getName());
        LOGGER.debug("Loading entities for chunk (" + x + ", " + z + ") on world " + this.world.getName());
        List<CompoundTag> entities = chunk.getEntities();
        loadedEntities = 0;
        if (entities != null) {
            for (CompoundTag tag : entities) {
                try {
                    Entity entity = EntityTypes.a((NBTTagCompound) Converter.convertTag(tag), server);
                    nmsChunk.g(true);
                    if (entity != null) {
                        nmsChunk.a(entity);
                        Entity entity1 = entity;

                        for (CompoundTag ridingTag = tag; ridingTag.getValue().containsKey("Riding"); ridingTag = (CompoundTag) ridingTag.getValue().get("Riding")) {
                            Entity entity2 = EntityTypes.a((NBTTagCompound) Converter.convertTag(ridingTag.getValue().get("Riding")), server);
                            if (entity2 != null) {
                                nmsChunk.a(entity2);
                                entity1.mount(entity2);
                                entity1 = entity2;
                                ++loadedEntities;
                            }
                        }

                        ++loadedEntities;
                    }
                } catch (Exception var22) {
                }
            }
        }

        LOGGER.debug("Loaded " + loadedEntities + " entities for chunk (" + x + ", " + z + ") on world " + this.world.getName());
        LOGGER.debug("Loaded chunk (" + x + ", " + z + ") on world " + this.world.getName());
        return nmsChunk;
    }

    private byte[] toByteArray(int[] ints) {
        ByteBuffer buf = ByteBuffer.allocate(ints.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        buf.asIntBuffer().put(ints);
        return buf.array();
    }

    public Chunk a(World nmsWorld, int x, int z) {
        SlimeChunk slimeChunk = this.world.getChunk(x, z);
        Chunk chunk;
        if (slimeChunk == null) {
            chunk = new Chunk(nmsWorld, x, z);
            chunk.d(true);
            chunk.e(true);
        } else {
            if (!(slimeChunk instanceof NMSSlimeChunk)) {
                throw new IllegalStateException("Chunk (" + x + ", " + z + ") has not been converted to a NMSSlimeChunk object!");
            }

            chunk = ((NMSSlimeChunk) slimeChunk).getChunk();
        }

        return chunk;
    }

    public void a(World world, Chunk chunk) {
        SlimeChunk slimeChunk = this.world.getChunk(chunk.locX, chunk.locZ);
        if (slimeChunk instanceof NMSSlimeChunk) {
            ((NMSSlimeChunk) slimeChunk).setChunk(chunk);
        } else {
            this.world.updateChunk(new NMSSlimeChunk(chunk));
        }

    }

    public void b() {
    }

    public void b(World world, Chunk chunk) {
    }

    public void a() {
    }

    public CustomChunkLoader(CraftSlimeWorld world) {
        this.world = world;
    }
}
