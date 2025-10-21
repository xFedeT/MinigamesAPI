//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.service.world.importer;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import it.fedet.minigames.api.swm.exceptions.InvalidWorldException;
import it.fedet.minigames.api.swm.utils.NibbleArray;
import it.fedet.minigames.api.swm.world.SlimeChunk;
import it.fedet.minigames.api.swm.world.SlimeChunkSection;
import it.fedet.minigames.api.swm.world.properties.SlimeProperties;
import it.fedet.minigames.api.swm.world.properties.SlimePropertyMap;
import it.fedet.minigames.swm.nms.CraftSlimeChunk;
import it.fedet.minigames.swm.nms.CraftSlimeChunkSection;
import it.fedet.minigames.swm.nms.CraftSlimeWorld;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class WorldImporter {
    private static final Pattern MAP_FILE_PATTERN = Pattern.compile("^(?:map_([0-9]*).dat)$");
    private static final int SECTOR_SIZE = 4096;

    public static CraftSlimeWorld readFromDirectory(File worldDir) throws InvalidWorldException, IOException {
        File levelFile = new File(worldDir, "level.dat");
        if (levelFile.exists() && levelFile.isFile()) {
            LevelData data = readLevelData(levelFile);
            byte worldVersion;
            if (data.version() == -1) {
                worldVersion = 1;
            } else if (data.version() < 818) {
                worldVersion = 2;
            } else if (data.version() < 1501) {
                worldVersion = 3;
            } else if (data.version() < 1517) {
                worldVersion = 4;
            } else {
                worldVersion = 5;
            }

            File regionDir = new File(worldDir, "region");
            if (regionDir.exists() && regionDir.isDirectory()) {
                Map<Long, SlimeChunk> chunks = new HashMap();

                for (File file : regionDir.listFiles((dir, name) -> name.endsWith(".mca"))) {
                    chunks.putAll(loadChunks(file, worldVersion).stream().collect(Collectors.toMap((chunk) -> (long) chunk.getZ() * 2147483647L + (long) chunk.getX(), (chunk) -> chunk)));
                }

                if (chunks.isEmpty()) {
                    throw new InvalidWorldException(worldDir);
                } else {
                    File dataDir = new File(worldDir, "data");
                    List<CompoundTag> maps = new ArrayList();
                    if (dataDir.exists()) {
                        if (!dataDir.isDirectory()) {
                            throw new InvalidWorldException(worldDir);
                        }

                        for (File mapFile : dataDir.listFiles((dir, name) -> MAP_FILE_PATTERN.matcher(name).matches())) {
                            maps.add(loadMap(mapFile));
                        }
                    }

                    CompoundMap extraData = new CompoundMap();
                    if (!data.gameRules().isEmpty()) {
                        CompoundMap gamerules = new CompoundMap();
                        data.gameRules().forEach((rule, value) -> gamerules.put(rule, new StringTag(rule, value)));
                        extraData.put("gamerules", new CompoundTag("gamerules", gamerules));
                    }

                    SlimePropertyMap propertyMap = new SlimePropertyMap();
                    propertyMap.setInt(SlimeProperties.SPAWN_X, data.spawnX());
                    propertyMap.setInt(SlimeProperties.SPAWN_Y, data.spawnY());
                    propertyMap.setInt(SlimeProperties.SPAWN_Z, data.spawnZ());
                    return new CraftSlimeWorld(null, worldDir.getName(), chunks, new CompoundTag("", extraData), maps, worldVersion, propertyMap, false, false, true);
                }
            } else {
                throw new InvalidWorldException(worldDir);
            }
        } else {
            throw new InvalidWorldException(worldDir);
        }
    }

    private static CompoundTag loadMap(File mapFile) throws IOException {
        String fileName = mapFile.getName();
        int mapId = Integer.parseInt(fileName.substring(4, fileName.length() - 4));
        NBTInputStream nbtStream = new NBTInputStream(new FileInputStream(mapFile), 1, ByteOrder.BIG_ENDIAN);

        CompoundTag tag;
        try {
            tag = nbtStream.readTag().getAsCompoundTag().get().getAsCompoundTag("data").get();
        } catch (Throwable var8) {
            try {
                nbtStream.close();
            } catch (Throwable var7) {
                var8.addSuppressed(var7);
            }

            throw var8;
        }

        nbtStream.close();
        tag.getValue().put("id", new IntTag("id", mapId));
        return tag;
    }

    private static LevelData readLevelData(File file) throws IOException, InvalidWorldException {
        NBTInputStream nbtStream = new NBTInputStream(new FileInputStream(file));

        Optional<CompoundTag> tag;
        try {
            tag = nbtStream.readTag().getAsCompoundTag();
        } catch (Throwable var10) {
            try {
                nbtStream.close();
            } catch (Throwable var9) {
                var10.addSuppressed(var9);
            }

            throw var10;
        }

        nbtStream.close();
        if (tag.isPresent()) {
            Optional<CompoundTag> dataTag = tag.get().getAsCompoundTag("Data");
            if (dataTag.isPresent()) {
                int dataVersion = dataTag.get().getIntValue("DataVersion").orElse(-1);
                Map<String, String> gameRules = new HashMap();
                Optional<CompoundTag> rulesList = dataTag.get().getAsCompoundTag("GameRules");
                rulesList.ifPresent((compoundTag) -> compoundTag.getValue().forEach((ruleName, ruleTag) -> gameRules.put(ruleName, ruleTag.getAsStringTag().get().getValue())));
                int spawnX = dataTag.get().getIntValue("SpawnX").orElse(0);
                int spawnY = dataTag.get().getIntValue("SpawnY").orElse(255);
                int spawnZ = dataTag.get().getIntValue("SpawnZ").orElse(0);
                return new LevelData(dataVersion, gameRules, spawnX, spawnY, spawnZ);
            }
        }

        throw new InvalidWorldException(file.getParentFile());
    }

    private static List<SlimeChunk> loadChunks(File file, byte worldVersion) throws IOException {
        byte[] regionByteArray = Files.readAllBytes(file.toPath());
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(regionByteArray));
        List<ChunkEntry> chunks = new ArrayList(1024);

        for (int i = 0; i < 1024; ++i) {
            int entry = inputStream.readInt();
            int chunkOffset = entry >>> 8;
            int chunkSize = entry & 15;
            if (entry != 0) {
                ChunkEntry chunkEntry = new ChunkEntry(chunkOffset * 4096, chunkSize * 4096);
                chunks.add(chunkEntry);
            }
        }

        List<SlimeChunk> loadedChunks = chunks.stream().map((entryx) -> {
            try {
                DataInputStream headerStream = new DataInputStream(new ByteArrayInputStream(regionByteArray, entryx.offset(), entryx.paddedSize()));
                int chunkSize = headerStream.readInt() - 1;
                int compressionScheme = headerStream.readByte();
                DataInputStream chunkStream = new DataInputStream(new ByteArrayInputStream(regionByteArray, entryx.offset() + 5, chunkSize));
                InputStream decompressorStream = compressionScheme == 1 ? new GZIPInputStream(chunkStream) : new InflaterInputStream(chunkStream);
                NBTInputStream nbtStream = new NBTInputStream(decompressorStream, 0, ByteOrder.BIG_ENDIAN);
                CompoundTag globalCompound = (CompoundTag) nbtStream.readTag();
                CompoundMap globalMap = globalCompound.getValue();
                if (!globalMap.containsKey("Level")) {
                    throw new RuntimeException("Missing Level tag?");
                } else {
                    CompoundTag levelCompound = (CompoundTag) globalMap.get("Level");
                    return readChunk(levelCompound, worldVersion);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return loadedChunks;
    }

    private static SlimeChunk readChunk(CompoundTag compound, byte worldVersion) {
        int chunkX = compound.getAsIntTag("xPos").get().getValue();
        int chunkZ = compound.getAsIntTag("zPos").get().getValue();
        Optional<String> status = compound.getStringValue("Status");
        if (status.isPresent() && !status.get().equals("postprocessed") && !status.get().startsWith("full")) {
            return null;
        } else {
            Tag biomesTag = compound.getValue().get("Biomes");
            int[] biomes;
            if (biomesTag instanceof IntArrayTag) {
                biomes = ((IntArrayTag) biomesTag).getValue();
            } else if (biomesTag instanceof ByteArrayTag) {
                byte[] byteBiomes = ((ByteArrayTag) biomesTag).getValue();
                biomes = toIntArray(byteBiomes);
            } else {
                biomes = null;
            }

            Optional<CompoundTag> optionalHeightMaps = compound.getAsCompoundTag("Heightmaps");
            CompoundTag heightMapsCompound;
            if (worldVersion >= 4) {
                heightMapsCompound = optionalHeightMaps.orElse(new CompoundTag("", new CompoundMap()));
            } else {
                int[] heightMap = compound.getIntArrayValue("HeightMap").orElse(new int[256]);
                heightMapsCompound = new CompoundTag("", new CompoundMap());
                heightMapsCompound.getValue().put("heightMap", new IntArrayTag("heightMap", heightMap));
            }

            List<CompoundTag> tileEntities = ((ListTag) compound.getAsListTag("TileEntities").orElse(new ListTag("TileEntities", TagType.TAG_COMPOUND, new ArrayList()))).getValue();
            List<CompoundTag> entities = ((ListTag) compound.getAsListTag("Entities").orElse(new ListTag("Entities", TagType.TAG_COMPOUND, new ArrayList()))).getValue();
            ListTag<CompoundTag> sectionsTag = (ListTag) compound.getAsListTag("Sections").get();
            SlimeChunkSection[] sectionArray = new SlimeChunkSection[16];

            for (CompoundTag sectionTag : sectionsTag.getValue()) {
                int index = sectionTag.getByteValue("Y").get();
                if (index >= 0) {
                    byte[] blocks = sectionTag.getByteArrayValue("Blocks").orElse(null);
                    NibbleArray dataArray;
                    ListTag<CompoundTag> paletteTag;
                    long[] blockStatesArray;
                    if (worldVersion < 4) {
                        dataArray = new NibbleArray(sectionTag.getByteArrayValue("Data").get());
                        if (isEmpty(blocks)) {
                            continue;
                        }

                        paletteTag = null;
                        blockStatesArray = null;
                    } else {
                        dataArray = null;
                        paletteTag = (ListTag) sectionTag.getAsListTag("Palette").orElse(null);
                        blockStatesArray = sectionTag.getLongArrayValue("BlockStates").orElse(null);
                        if (paletteTag == null || blockStatesArray == null || isEmpty(blockStatesArray)) {
                            continue;
                        }
                    }

                    NibbleArray blockLightArray = sectionTag.getValue().containsKey("BlockLight") ? new NibbleArray(sectionTag.getByteArrayValue("BlockLight").get()) : null;
                    NibbleArray skyLightArray = sectionTag.getValue().containsKey("SkyLight") ? new NibbleArray(sectionTag.getByteArrayValue("SkyLight").get()) : null;
                    sectionArray[index] = new CraftSlimeChunkSection(blocks, dataArray, paletteTag, blockStatesArray, blockLightArray, skyLightArray);
                }
            }

            for (SlimeChunkSection section : sectionArray) {
                if (section != null) {
                    return new CraftSlimeChunk(null, chunkX, chunkZ, sectionArray, heightMapsCompound, biomes, tileEntities, entities);
                }
            }

            return null;
        }
    }

    private static int[] toIntArray(byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN);
        int[] ret = new int[buf.length / 4];
        buffer.asIntBuffer().get(ret);
        return ret;
    }

    private static boolean isEmpty(byte[] array) {
        for (byte b : array) {
            if (b != 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean isEmpty(long[] array) {
        for (long b : array) {
            if (b != 0L) {
                return false;
            }
        }

        return true;
    }

    public record ChunkEntry(int offset, int paddedSize) {

    }
}
