package it.fedet.minigames.utils;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.DoubleTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.TagType;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.github.luben.zstd.Zstd;
import com.mongodb.MongoException;
import it.fedet.minigames.api.world.SlimeLoader;
import it.fedet.minigames.api.world.exception.CorruptedWorldException;
import it.fedet.minigames.api.world.exception.NewerFormatException;
import it.fedet.minigames.world.loader.SlimeFormat;
import it.fedet.minigames.world.map.SlimePropertyMap;
import it.fedet.minigames.world.nms.chunk.CraftSlimeChunk;
import it.fedet.minigames.world.nms.chunk.CraftSlimeChunkSection;
import it.fedet.minigames.world.nms.chunk.SlimeChunk;
import it.fedet.minigames.world.nms.world.CraftSlimeWorld;
import it.fedet.minigames.world.storage.StorageType;
import it.fedet.minigames.api.world.providers.WorldDbProvider;
import it.fedet.minigames.api.world.storage.UpdatableLoader;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.util.*;

public class LoaderUtils {

    public static final long MAX_LOCK_TIME = 300000L; // Max time difference between current time millis and world lock
    public static final long LOCK_INTERVAL = 60000L;

    private static final Map<String, SlimeLoader> loaderMap = new HashMap<>();

    public static void registerLoaders(StorageType storageType, WorldDbProvider provider) {
        try {
            Class<?> loaderClass = storageType.getLoaderClass();

            // Cerchiamo un costruttore compatibile con la classe effettiva del provider
            Constructor<?> constructor = getConstructor(provider, loaderClass);

            SlimeLoader loader = (SlimeLoader) constructor.newInstance(provider);
            registerLoader(storageType, loader);

        } catch (InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to register loader for " + storageType.getName(), e);
        }
    }

    private static @NotNull Constructor<?> getConstructor(WorldDbProvider provider, Class<?> loaderClass) throws NoSuchMethodException {
        Constructor<?> constructor = null;
        for (Constructor<?> c : loaderClass.getConstructors()) {
            Class<?>[] params = c.getParameterTypes();
            if (params.length == 1 && params[0].isAssignableFrom(provider.getClass())) {
                constructor = c;
                break;
            }
        }

        // Se non ne troviamo uno compatibile, proviamo con WorldDbProvider
        if (constructor == null) {
            constructor = loaderClass.getConstructor(WorldDbProvider.class);
        }
        return constructor;
    }


    public static List<String> getAvailableLoadersNames() {
        return new LinkedList<>(loaderMap.keySet());
    }


    public static SlimeLoader getLoader(StorageType storageType) {
        return loaderMap.get(storageType.getName());
    }

    public static void registerLoader(StorageType storageType, SlimeLoader loader) {
        if (loaderMap.containsKey(storageType.getName())) {
            throw new IllegalArgumentException("Data source " + storageType.getName() + " already has a declared loader!");
        }

        if (loader instanceof UpdatableLoader) {
            try {
                ((UpdatableLoader) loader).update();
            } catch (UpdatableLoader.NewerDatabaseException e) {
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }

        loaderMap.put(storageType.getName(), loader);
    }

    public static CraftSlimeWorld deserializeWorld(SlimeLoader loader, String worldName, byte[] serializedWorld, SlimePropertyMap propertyMap, boolean ignoreLocked, boolean readOnly)
            throws IOException, CorruptedWorldException, NewerFormatException {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(serializedWorld));

        try {
            byte[] fileHeader = new byte[SlimeFormat.SLIME_HEADER.length];
            dataStream.read(fileHeader);

            if (!Arrays.equals(SlimeFormat.SLIME_HEADER, fileHeader)) {
                throw new CorruptedWorldException(worldName);
            }

            // File version
            byte version = dataStream.readByte();

            if (version > SlimeFormat.SLIME_VERSION) {
                throw new NewerFormatException(version);
            }

            // World version
            byte worldVersion;

            if (version >= 6) {
                worldVersion = dataStream.readByte();
            } else if (version >= 4) { // In v4 there's just a boolean indicating whether the world is pre-1.13 or post-1.13
                worldVersion = (byte) (dataStream.readBoolean() ? 0x04 : 0x01);
            } else {
                worldVersion = 0; // We'll try to automatically detect it later
            }

            // Chunk
            short minX = dataStream.readShort();
            short minZ = dataStream.readShort();
            int width = dataStream.readShort();
            int depth = dataStream.readShort();

            if (width <= 0 || depth <= 0) {
                throw new CorruptedWorldException(worldName);
            }

            int bitmaskSize = (int) Math.ceil((width * depth) / 8.0D);
            byte[] chunkBitmask = new byte[bitmaskSize];
            dataStream.read(chunkBitmask);
            BitSet chunkBitset = BitSet.valueOf(chunkBitmask);

            int compressedChunkDataLength = dataStream.readInt();
            int chunkDataLength = dataStream.readInt();
            byte[] compressedChunkData = new byte[compressedChunkDataLength];
            byte[] chunkData = new byte[chunkDataLength];

            dataStream.read(compressedChunkData);

            // Tile Entities
            int compressedTileEntitiesLength = dataStream.readInt();
            int tileEntitiesLength = dataStream.readInt();
            byte[] compressedTileEntities = new byte[compressedTileEntitiesLength];
            byte[] tileEntities = new byte[tileEntitiesLength];

            dataStream.read(compressedTileEntities);

            // Entities
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

            // Extra NBT tag
            byte[] compressedExtraTag = new byte[0];
            byte[] extraTag = new byte[0];

            if (version >= 2) {
                int compressedExtraTagLength = dataStream.readInt();
                int extraTagLength = dataStream.readInt();
                compressedExtraTag = new byte[compressedExtraTagLength];
                extraTag = new byte[extraTagLength];

                dataStream.read(compressedExtraTag);
            }

            // World Map NBT tag
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
            }

            // Data decompression
            Zstd.decompress(chunkData, compressedChunkData);
            Zstd.decompress(tileEntities, compressedTileEntities);
            Zstd.decompress(entities, compressedEntities);
            Zstd.decompress(extraTag, compressedExtraTag);
            Zstd.decompress(mapsTag, compressedMapsTag);

            // Chunk deserialization
            Map<Long, SlimeChunk> chunks = readChunks(worldVersion, version, worldName, minX, minZ, width, depth, chunkBitset, chunkData);

            // Entity deserialization
            CompoundTag entitiesCompound = readCompoundTag(entities);

            if (entitiesCompound != null) {
                ListTag<CompoundTag> entitiesList = (ListTag<CompoundTag>) entitiesCompound.getValue().get("entities");

                for (CompoundTag entityCompound : entitiesList.getValue()) {
                    ListTag<DoubleTag> listTag = (ListTag<DoubleTag>) entityCompound.getAsListTag("Pos").get();

                    int chunkX = floor(listTag.getValue().get(0).getValue()) >> 4;
                    int chunkZ = floor(listTag.getValue().get(2).getValue()) >> 4;
                    long chunkKey = ((long) chunkZ) * Integer.MAX_VALUE + ((long) chunkX);
                    SlimeChunk chunk = chunks.get(chunkKey);

                    if (chunk == null) {
                        throw new CorruptedWorldException(worldName);
                    }

                    chunk.getEntities().add(entityCompound);
                }
            }

            // Tile Entity deserialization
            CompoundTag tileEntitiesCompound = readCompoundTag(tileEntities);

            if (tileEntitiesCompound != null) {
                ListTag<CompoundTag> tileEntitiesList = (ListTag<CompoundTag>) tileEntitiesCompound.getValue().get("tiles");

                for (CompoundTag tileEntityCompound : tileEntitiesList.getValue()) {
                    int chunkX = ((IntTag) tileEntityCompound.getValue().get("x")).getValue() >> 4;
                    int chunkZ = ((IntTag) tileEntityCompound.getValue().get("z")).getValue() >> 4;
                    long chunkKey = ((long) chunkZ) * Integer.MAX_VALUE + ((long) chunkX);
                    SlimeChunk chunk = chunks.get(chunkKey);

                    if (chunk == null) {
                        throw new CorruptedWorldException(worldName);
                    }

                    chunk.getTileEntities().add(tileEntityCompound);
                }
            }

            // Extra Data
            CompoundTag extraCompound = readCompoundTag(extraTag);

            if (extraCompound == null) {
                extraCompound = new CompoundTag("", new CompoundMap());
            }

            // World Maps
            CompoundTag mapsCompound = readCompoundTag(mapsTag);
            List<CompoundTag> mapList;

            if (mapsCompound != null) {
                mapList = (List<CompoundTag>) mapsCompound.getAsListTag("maps").map(ListTag::getValue).orElse(new ArrayList<>());
            } else {
                mapList = new ArrayList<>();
            }

            // v1_13 world format detection for old versions
            if (worldVersion == 0) {
                mainLoop:
                for (SlimeChunk chunk : chunks.values()) {
                    for (CraftSlimeChunkSection section : chunk.getSections()) {
                        if (section != null) {
                            worldVersion = (byte) (section.getBlocks() == null ? 0x04 : 0x01);

                            break mainLoop;
                        }
                    }
                }
            }

            // World properties
            SlimePropertyMap worldPropertyMap = propertyMap;
            Optional<CompoundTag> propertiesTag = extraCompound.getAsCompoundTag("properties");

            if (propertiesTag.isPresent()) {
                worldPropertyMap = SlimePropertyMap.fromCompound(propertiesTag.get());
                worldPropertyMap.merge(propertyMap); // Override world properties
            } else if (propertyMap == null) { // Make sure the property map is never null
                worldPropertyMap = new SlimePropertyMap();
            }

            return new CraftSlimeWorld(loader, worldName, chunks, extraCompound, mapList, worldVersion, worldPropertyMap, readOnly, ignoreLocked, !readOnly);
        } catch (EOFException ex) {
            throw new CorruptedWorldException(worldName, ex);
        }
    }

    private static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    private static Map<Long, SlimeChunk> readChunks(byte worldVersion, int version, String worldName, int minX, int minZ, int width, int depth, BitSet chunkBitset, byte[] chunkData) throws IOException {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(chunkData));
        Map<Long, SlimeChunk> chunkMap = new HashMap<>();

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                int bitsetIndex = z * width + x;

                if (chunkBitset.get(bitsetIndex)) {
                    // Height Maps
                    CompoundTag heightMaps;

                    if (worldVersion >= 0x04) {
                        int heightMapsLength = dataStream.readInt();
                        byte[] heightMapsArray = new byte[heightMapsLength];
                        dataStream.read(heightMapsArray);
                        heightMaps = readCompoundTag(heightMapsArray);

                        // Height Maps might be null if empty
                        if (heightMaps == null) {
                            heightMaps = new CompoundTag("", new CompoundMap());
                        }
                    } else {
                        int[] heightMap = new int[256];

                        for (int i = 0; i < 256; i++) {
                            heightMap[i] = dataStream.readInt();
                        }

                        CompoundMap map = new CompoundMap();
                        map.put("heightMap", new IntArrayTag("heightMap", heightMap));

                        heightMaps = new CompoundTag("", map);
                    }

                    // Biome array
                    int[] biomes;

                    if (version == 8 && worldVersion < 0x04) {
                        // Patch the v8 bug: biome array size is wrong for old worlds
                        dataStream.readInt();
                    }

                    if (worldVersion >= 0x04) {
                        int biomesArrayLength = version >= 8 ? dataStream.readInt() : 256;
                        biomes = new int[biomesArrayLength];

                        for (int i = 0; i < biomes.length; i++) {
                            biomes[i] = dataStream.readInt();
                        }
                    } else {
                        byte[] byteBiomes = new byte[256];
                        dataStream.read(byteBiomes);
                        biomes = toIntArray(byteBiomes);
                    }

                    // Chunk Sections
                    CraftSlimeChunkSection[] sections = readChunkSections(dataStream, worldVersion, version);

                    chunkMap.put(((long) minZ + z) * Integer.MAX_VALUE + ((long) minX + x), new CraftSlimeChunk(worldName,minX + x, minZ + z,
                            sections, heightMaps, biomes, new ArrayList<>(), new ArrayList<>()));
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

    private static CraftSlimeChunkSection[] readChunkSections(DataInputStream dataStream, byte worldVersion, int version) throws IOException {
        CraftSlimeChunkSection[] chunkSectionArray = new CraftSlimeChunkSection[16];
        byte[] sectionBitmask = new byte[2];
        dataStream.read(sectionBitmask);
        BitSet sectionBitset = BitSet.valueOf(sectionBitmask);

        for (int i = 0; i < 16; i++) {
            if (sectionBitset.get(i)) {
                // Block Light Nibble Array
                NibbleArray blockLightArray;

                if (version < 5 || dataStream.readBoolean()) {
                    byte[] blockLightByteArray = new byte[2048];
                    dataStream.read(blockLightByteArray);
                    blockLightArray = new NibbleArray((blockLightByteArray));
                } else {
                    blockLightArray = null;
                }

                // Block data
                byte[] blockArray;
                NibbleArray dataArray;

                ListTag<CompoundTag> paletteTag;
                long[] blockStatesArray;

                // Post 1.13 block format
                if (worldVersion >= 0x04) {
                    // Palette
                    int paletteLength = dataStream.readInt();
                    List<CompoundTag> paletteList = new ArrayList<>(paletteLength);

                    for (int index = 0; index < paletteLength; index++) {
                        int tagLength = dataStream.readInt();
                        byte[] serializedTag = new byte[tagLength];
                        dataStream.read(serializedTag);

                        paletteList.add(readCompoundTag(serializedTag));
                    }

                    paletteTag = new ListTag<>("", TagType.TAG_COMPOUND, paletteList);

                    // Block states
                    int blockStatesArrayLength = dataStream.readInt();
                    blockStatesArray = new long[blockStatesArrayLength];

                    for (int index = 0; index < blockStatesArrayLength; index++) {
                        blockStatesArray[index] = dataStream.readLong();
                    }

                    blockArray = null;
                    dataArray = null;
                } else {
                    blockArray = new byte[4096];
                    dataStream.read(blockArray);

                    // Block Data Nibble Array
                    byte[] dataByteArray = new byte[2048];
                    dataStream.read(dataByteArray);
                    dataArray = new NibbleArray((dataByteArray));

                    paletteTag = null;
                    blockStatesArray = null;
                }

                // Sky Light Nibble Array
                NibbleArray skyLightArray;

                if (version < 5 || dataStream.readBoolean()) {
                    byte[] skyLightByteArray = new byte[2048];
                    dataStream.read(skyLightByteArray);
                    skyLightArray = new NibbleArray((skyLightByteArray));
                } else {
                    skyLightArray = null;
                }

                // HypixelBlocks 3
                if (version < 4) {
                    short hypixelBlocksLength = dataStream.readShort();
                    dataStream.skip(hypixelBlocksLength);
                }

                chunkSectionArray[i] = new CraftSlimeChunkSection(blockArray, dataArray, paletteTag, blockStatesArray, blockLightArray, skyLightArray);
            }
        }

        return chunkSectionArray;
    }

    private static CompoundTag readCompoundTag(byte[] serializedCompound) throws IOException {
        if (serializedCompound.length == 0) {
            return null;
        }

        NBTInputStream stream = new NBTInputStream(new ByteArrayInputStream(serializedCompound), NBTInputStream.NO_COMPRESSION, ByteOrder.BIG_ENDIAN);

        return (CompoundTag) stream.readTag();
    }
}
