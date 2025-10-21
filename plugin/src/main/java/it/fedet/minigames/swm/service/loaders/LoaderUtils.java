//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.service.loaders;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.github.luben.zstd.Zstd;
import com.mongodb.MongoException;
import it.fedet.minigames.api.swm.database.StorageType;
import it.fedet.minigames.api.swm.database.WorldDbProvider;
import it.fedet.minigames.api.swm.exceptions.CorruptedWorldException;
import it.fedet.minigames.api.swm.exceptions.NewerFormatException;
import it.fedet.minigames.api.swm.loaders.SlimeLoader;
import it.fedet.minigames.api.swm.utils.NibbleArray;
import it.fedet.minigames.api.swm.utils.SlimeFormat;
import it.fedet.minigames.api.swm.world.SlimeChunk;
import it.fedet.minigames.api.swm.world.SlimeChunkSection;
import it.fedet.minigames.api.swm.world.properties.SlimePropertyMap;
import it.fedet.minigames.swm.service.loaders.mongodb.MongoLoader;
import it.fedet.minigames.swm.service.loaders.mysql.MysqlLoader;
import it.fedet.minigames.swm.service.log.Logging;
import it.fedet.minigames.swm.nms.CraftSlimeChunk;
import it.fedet.minigames.swm.nms.CraftSlimeChunkSection;
import it.fedet.minigames.swm.nms.CraftSlimeWorld;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.util.*;

public class LoaderUtils {
    public static final long MAX_LOCK_TIME = 300000L;
    public static final long LOCK_INTERVAL = 60000L;
    private static final Map<StorageType, SlimeLoader> loaderMap = new HashMap();

    public static void registerLoaders(WorldDbProvider provider) {
        switch(provider.getStorageType()) {
            case MYSQL -> {
                try {
                    registerLoader(provider.getStorageType(), new MysqlLoader(provider));
                } catch (SQLException ex) {
                    Logging.error("Failed to establish connection to the MySQL server:");
                    ex.printStackTrace();
                }
            }

            case MONGODB -> {
                try {
                    registerLoader(provider.getStorageType(), new MongoLoader(provider));
                } catch (MongoException ex) {
                    Logging.error("Failed to establish connection to the MySQL server:");
                    ex.printStackTrace();
                }
            }
        }
    }

    public static List<String> getAvailableLoadersNames() {
        return new LinkedList(loaderMap.keySet());
    }

    public static SlimeLoader getLoader(StorageType storageType) {
        return loaderMap.get(storageType);
    }

    public static void registerLoader(StorageType storageType, SlimeLoader loader) {
        if (loaderMap.containsKey(storageType)) {
            throw new IllegalArgumentException("Data source " + storageType.name() + " already has a declared loader!");
        } else {
            if (loader instanceof UpdatableLoader) {
                try {
                    ((UpdatableLoader) loader).update();
                } catch (UpdatableLoader.NewerDatabaseException e) {
                    Logging.error("Data source " + storageType.name() + " version is " + e.getDatabaseVersion() + ", while this SWM version only supports up to version " + e.getCurrentVersion() + ".");
                    return;
                } catch (IOException ex) {
                    Logging.error("Failed to check if data source " + storageType.name() + " is updated:");
                    ex.printStackTrace();
                    return;
                }
            }

            loaderMap.put(storageType, loader);
        }
    }

    public static CraftSlimeWorld deserializeWorld(SlimeLoader loader, String worldName, byte[] serializedWorld, SlimePropertyMap propertyMap, boolean readOnly, boolean ignoreLocked) throws IOException, CorruptedWorldException, NewerFormatException {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(serializedWorld));

        try {
            byte[] fileHeader = new byte[SlimeFormat.SLIME_HEADER.length];
            dataStream.read(fileHeader);
            if (!Arrays.equals(SlimeFormat.SLIME_HEADER, fileHeader)) {
                throw new CorruptedWorldException(worldName);
            } else {
                byte version = dataStream.readByte();
                if (version > 9) {
                    throw new NewerFormatException(version);
                } else {
                    byte worldVersion;
                    if (version >= 6) {
                        worldVersion = dataStream.readByte();
                    } else if (version >= 4) {
                        worldVersion = (byte) (dataStream.readBoolean() ? 4 : 1);
                    } else {
                        worldVersion = 0;
                    }

                    short minX = dataStream.readShort();
                    short minZ = dataStream.readShort();
                    int width = dataStream.readShort();
                    int depth = dataStream.readShort();
                    if (width > 0 && depth > 0) {
                        int bitmaskSize = (int) Math.ceil((double) (width * depth) / (double) 8.0F);
                        byte[] chunkBitmask = new byte[bitmaskSize];
                        dataStream.read(chunkBitmask);
                        BitSet chunkBitset = BitSet.valueOf(chunkBitmask);
                        int compressedChunkDataLength = dataStream.readInt();
                        int chunkDataLength = dataStream.readInt();
                        byte[] compressedChunkData = new byte[compressedChunkDataLength];
                        byte[] chunkData = new byte[chunkDataLength];
                        dataStream.read(compressedChunkData);
                        int compressedTileEntitiesLength = dataStream.readInt();
                        int tileEntitiesLength = dataStream.readInt();
                        byte[] compressedTileEntities = new byte[compressedTileEntitiesLength];
                        byte[] tileEntities = new byte[tileEntitiesLength];
                        dataStream.read(compressedTileEntities);
                        byte[] compressedEntities = new byte[0];
                        byte[] entities = new byte[0];
                        if (version >= 3) {
                            boolean hasEntities = dataStream.readBoolean();
                            if (hasEntities) {
                                int compressedEntitiesLength = dataStream.readInt();
                                int entitiesLength = dataStream.readInt();
                                compressedEntities = new byte[compressedEntitiesLength];
                                entities = new byte[entitiesLength];
                                dataStream.read(compressedEntities);
                            }
                        }

                        byte[] compressedExtraTag = new byte[0];
                        byte[] extraTag = new byte[0];
                        if (version >= 2) {
                            int compressedExtraTagLength = dataStream.readInt();
                            int extraTagLength = dataStream.readInt();
                            compressedExtraTag = new byte[compressedExtraTagLength];
                            extraTag = new byte[extraTagLength];
                            dataStream.read(compressedExtraTag);
                        }

                        byte[] compressedMapsTag = new byte[0];
                        byte[] mapsTag = new byte[0];
                        if (version >= 7) {
                            int compressedMapsTagLength = dataStream.readInt();
                            int mapsTagLength = dataStream.readInt();
                            compressedMapsTag = new byte[compressedMapsTagLength];
                            mapsTag = new byte[mapsTagLength];
                            dataStream.read(compressedMapsTag);
                        }

                        if (dataStream.read() != -1) {
                            throw new CorruptedWorldException(worldName);
                        } else {
                            Zstd.decompress(chunkData, compressedChunkData);
                            Zstd.decompress(tileEntities, compressedTileEntities);
                            Zstd.decompress(entities, compressedEntities);
                            Zstd.decompress(extraTag, compressedExtraTag);
                            Zstd.decompress(mapsTag, compressedMapsTag);
                            Map<Long, SlimeChunk> chunks = readChunks(worldVersion, version, worldName, minX, minZ, width, depth, chunkBitset, chunkData);
                            CompoundTag entitiesCompound = readCompoundTag(entities);
                            if (entitiesCompound != null) {
                                ListTag<CompoundTag> entitiesList = (ListTag) entitiesCompound.getValue().get("entities");

                                for (CompoundTag entityCompound : entitiesList.getValue()) {
                                    ListTag<DoubleTag> listTag = (ListTag) entityCompound.getAsListTag("Pos").get();
                                    int chunkX = floor(((DoubleTag) listTag.getValue().get(0)).getValue()) >> 4;
                                    int chunkZ = floor(((DoubleTag) listTag.getValue().get(2)).getValue()) >> 4;
                                    long chunkKey = (long) chunkZ * 2147483647L + (long) chunkX;
                                    SlimeChunk chunk = chunks.get(chunkKey);
                                    if (chunk == null) {
                                        throw new CorruptedWorldException(worldName);
                                    }

                                    chunk.getEntities().add(entityCompound);
                                }
                            }

                            CompoundTag tileEntitiesCompound = readCompoundTag(tileEntities);
                            if (tileEntitiesCompound != null) {
                                ListTag<CompoundTag> tileEntitiesList = (ListTag) tileEntitiesCompound.getValue().get("tiles");

                                for (CompoundTag tileEntityCompound : tileEntitiesList.getValue()) {
                                    int chunkX = ((IntTag) tileEntityCompound.getValue().get("x")).getValue() >> 4;
                                    int chunkZ = ((IntTag) tileEntityCompound.getValue().get("z")).getValue() >> 4;
                                    long chunkKey = (long) chunkZ * 2147483647L + (long) chunkX;
                                    SlimeChunk chunk = chunks.get(chunkKey);
                                    if (chunk == null) {
                                        throw new CorruptedWorldException(worldName);
                                    }

                                    chunk.getTileEntities().add(tileEntityCompound);
                                }
                            }

                            CompoundTag extraCompound = readCompoundTag(extraTag);
                            if (extraCompound == null) {
                                extraCompound = new CompoundTag("", new CompoundMap());
                            }

                            CompoundTag mapsCompound = readCompoundTag(mapsTag);
                            List<CompoundTag> mapList;
                            if (mapsCompound != null) {
                                mapList = (List) mapsCompound.getAsListTag("maps").map(ListTag::getValue).orElse(new ArrayList());
                            } else {
                                mapList = new ArrayList();
                            }

                            if (worldVersion == 0) {
                                label100:
                                for (SlimeChunk chunk : chunks.values()) {
                                    for (SlimeChunkSection section : chunk.getSections()) {
                                        if (section != null) {
                                            worldVersion = (byte) (section.blocks() == null ? 4 : 1);
                                            break label100;
                                        }
                                    }
                                }
                            }

                            SlimePropertyMap worldPropertyMap = propertyMap;
                            Optional<CompoundTag> propertiesTag = extraCompound.getAsCompoundTag("properties");
                            if (propertiesTag.isPresent()) {
                                worldPropertyMap = SlimePropertyMap.fromCompound((CompoundTag) propertiesTag.get());
                                worldPropertyMap.merge(propertyMap);
                            } else if (propertyMap == null) {
                                worldPropertyMap = new SlimePropertyMap();
                            }

                            return new CraftSlimeWorld(loader, worldName, chunks, extraCompound, mapList, worldVersion, worldPropertyMap, readOnly, ignoreLocked, !readOnly);
                        }
                    } else {
                        throw new CorruptedWorldException(worldName);
                    }
                }
            }
        } catch (EOFException ex) {
            throw new CorruptedWorldException(worldName, ex);
        }
    }

    private static int floor(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    private static Map<Long, SlimeChunk> readChunks(byte worldVersion, int version, String worldName, int minX, int minZ, int width, int depth, BitSet chunkBitset, byte[] chunkData) throws IOException {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(chunkData));
        Map<Long, SlimeChunk> chunkMap = new HashMap();

        for (int z = 0; z < depth; ++z) {
            for (int x = 0; x < width; ++x) {
                int bitsetIndex = z * width + x;
                if (chunkBitset.get(bitsetIndex)) {
                    CompoundTag heightMaps;
                    if (worldVersion >= 4) {
                        int heightMapsLength = dataStream.readInt();
                        byte[] heightMapsArray = new byte[heightMapsLength];
                        dataStream.read(heightMapsArray);
                        heightMaps = readCompoundTag(heightMapsArray);
                        if (heightMaps == null) {
                            heightMaps = new CompoundTag("", new CompoundMap());
                        }
                    } else {
                        int[] heightMap = new int[256];

                        for (int i = 0; i < 256; ++i) {
                            heightMap[i] = dataStream.readInt();
                        }

                        CompoundMap map = new CompoundMap();
                        map.put("heightMap", new IntArrayTag("heightMap", heightMap));
                        heightMaps = new CompoundTag("", map);
                    }

                    if (version == 8 && worldVersion < 4) {
                        dataStream.readInt();
                    }

                    int[] biomes;
                    if (worldVersion >= 4) {
                        int biomesArrayLength = version >= 8 ? dataStream.readInt() : 256;
                        biomes = new int[biomesArrayLength];

                        for (int i = 0; i < biomes.length; ++i) {
                            biomes[i] = dataStream.readInt();
                        }
                    } else {
                        byte[] byteBiomes = new byte[256];
                        dataStream.read(byteBiomes);
                        biomes = toIntArray(byteBiomes);
                    }

                    SlimeChunkSection[] sections = readChunkSections(dataStream, worldVersion, version);
                    chunkMap.put(((long) minZ + (long) z) * 2147483647L + (long) minX + (long) x, new CraftSlimeChunk(worldName, minX + x, minZ + z, sections, heightMaps, biomes, new ArrayList(), new ArrayList()));
                }
            }
        }

        return chunkMap;
    }

    private static int[] toIntArray(byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN);
        int[] ret = new int[buf.length / 4];
        buffer.asIntBuffer().get(ret);
        return ret;
    }

    private static SlimeChunkSection[] readChunkSections(DataInputStream dataStream, byte worldVersion, int version) throws IOException {
        SlimeChunkSection[] chunkSectionArray = new SlimeChunkSection[16];
        byte[] sectionBitmask = new byte[2];
        dataStream.read(sectionBitmask);
        BitSet sectionBitset = BitSet.valueOf(sectionBitmask);

        for (int i = 0; i < 16; ++i) {
            if (sectionBitset.get(i)) {
                NibbleArray blockLightArray;
                if (version >= 5 && !dataStream.readBoolean()) {
                    blockLightArray = null;
                } else {
                    byte[] blockLightByteArray = new byte[2048];
                    dataStream.read(blockLightByteArray);
                    blockLightArray = new NibbleArray(blockLightByteArray);
                }

                NibbleArray dataArray;
                ListTag<CompoundTag> paletteTag;
                long[] blockStatesArray;
                byte[] blockArray;
                if (worldVersion >= 4) {
                    int paletteLength = dataStream.readInt();
                    List<CompoundTag> paletteList = new ArrayList(paletteLength);

                    for (int index = 0; index < paletteLength; ++index) {
                        int tagLength = dataStream.readInt();
                        byte[] serializedTag = new byte[tagLength];
                        dataStream.read(serializedTag);
                        paletteList.add(readCompoundTag(serializedTag));
                    }

                    paletteTag = new ListTag("", TagType.TAG_COMPOUND, paletteList);
                    int blockStatesArrayLength = dataStream.readInt();
                    blockStatesArray = new long[blockStatesArrayLength];

                    for (int index = 0; index < blockStatesArrayLength; ++index) {
                        blockStatesArray[index] = dataStream.readLong();
                    }

                    blockArray = null;
                    dataArray = null;
                } else {
                    blockArray = new byte[4096];
                    dataStream.read(blockArray);
                    byte[] dataByteArray = new byte[2048];
                    dataStream.read(dataByteArray);
                    dataArray = new NibbleArray(dataByteArray);
                    paletteTag = null;
                    blockStatesArray = null;
                }

                NibbleArray skyLightArray;
                if (version >= 5 && !dataStream.readBoolean()) {
                    skyLightArray = null;
                } else {
                    byte[] skyLightByteArray = new byte[2048];
                    dataStream.read(skyLightByteArray);
                    skyLightArray = new NibbleArray(skyLightByteArray);
                }

                if (version < 4) {
                    short hypixelBlocksLength = dataStream.readShort();
                    dataStream.skip((long) hypixelBlocksLength);
                }

                chunkSectionArray[i] = new CraftSlimeChunkSection(blockArray, dataArray, paletteTag, blockStatesArray, blockLightArray, skyLightArray);
            }
        }

        return chunkSectionArray;
    }

    private static CompoundTag readCompoundTag(byte[] serializedCompound) throws IOException {
        if (serializedCompound.length == 0) {
            return null;
        } else {
            NBTInputStream stream = new NBTInputStream(new ByteArrayInputStream(serializedCompound), 0, ByteOrder.BIG_ENDIAN);
            return (CompoundTag) stream.readTag();
        }
    }
}
