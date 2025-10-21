//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.nms;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.TagType;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.github.luben.zstd.Zstd;
import it.fedet.minigames.api.swm.exceptions.WorldAlreadyExistsException;
import it.fedet.minigames.api.swm.loaders.SlimeLoader;
import it.fedet.minigames.api.swm.utils.SlimeFormat;
import it.fedet.minigames.api.swm.world.SlimeChunk;
import it.fedet.minigames.api.swm.world.SlimeChunkSection;
import it.fedet.minigames.api.swm.world.SlimeWorld;
import it.fedet.minigames.api.swm.world.properties.SlimePropertyMap;
import org.bukkit.Difficulty;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;

public class CraftSlimeWorld implements SlimeWorld {
    private SlimeLoader loader;
    private final String name;
    private final Map<Long, SlimeChunk> chunks;
    private final CompoundTag extraData;
    private final List<CompoundTag> worldMaps;
    private byte version;
    private final SlimePropertyMap propertyMap;
    private boolean readOnly;
    private final boolean ignoreLocked;
    private final boolean locked;

    public SlimeChunk getChunk(int x, int z) {
        synchronized (this.chunks) {
            Long index = (long) z * 2147483647L + (long) x;
            return this.chunks.get(index);
        }
    }

    public void updateChunk(SlimeChunk chunk) {
        if (!chunk.getWorldName().equals(this.getName())) {
            throw new IllegalArgumentException("Chunk (" + chunk.getX() + ", " + chunk.getZ() + ") belongs to world '" + chunk.getWorldName() + "', not to '" + this.getName() + "'!");
        } else {
            synchronized (this.chunks) {
                this.chunks.put((long) chunk.getZ() * 2147483647L + (long) chunk.getX(), chunk);
            }
        }
    }

    public SlimeWorld clone(String worldName) {
        try {
            return this.clone(worldName, null);
        } catch (IOException | WorldAlreadyExistsException var3) {
            return null;
        }
    }

    public SlimeWorld clone(String worldName, SlimeLoader loader) throws WorldAlreadyExistsException, IOException {
        return this.clone(worldName, loader, true);
    }

    public SlimeWorld clone(String worldName, SlimeLoader loader, boolean lock) throws WorldAlreadyExistsException, IOException {
        if (this.name.equals(worldName)) {
            throw new IllegalArgumentException("The clone world cannot have the same name as the original world!");
        } else if (worldName == null) {
            throw new IllegalArgumentException("The world name cannot be null!");
        } else if (loader != null && loader.worldExists(worldName)) {
            throw new WorldAlreadyExistsException(worldName);
        } else {
            CraftSlimeWorld world;
            synchronized (this.chunks) {
                Map<Long, SlimeChunk> chunks = new HashMap(this.chunks.size());

                for (Map.Entry<Long, SlimeChunk> entry : this.chunks.entrySet()) {
                    chunks.put(entry.getKey(), new CraftSlimeChunk(worldName, entry.getValue()));
                }

                world = new CraftSlimeWorld(loader == null ? this.loader : loader, worldName, chunks, this.extraData.clone(), new ArrayList(this.worldMaps), this.version, this.propertyMap, loader == null, this.ignoreLocked, lock);
            }

            if (loader != null) {
                loader.saveWorld(worldName, world.serialize(), lock);
            }

            return world;
        }
    }

    public static byte[] serializationOf(SlimeWorld slimeWorld) {
        CraftSlimeWorld world = (CraftSlimeWorld) slimeWorld;
        return world.serialize();
    }

    public SlimeWorld.SlimeProperties getProperties() {
        return SlimeProperties.builder()
                .spawnX(this.propertyMap.getInt(it.fedet.minigames.api.swm.world.properties.SlimeProperties.SPAWN_X))
                .spawnY(this.propertyMap.getInt(it.fedet.minigames.api.swm.world.properties.SlimeProperties.SPAWN_Y))
                .spawnZ(this.propertyMap.getInt(it.fedet.minigames.api.swm.world.properties.SlimeProperties.SPAWN_Z))
                .environment(this.propertyMap.getString(it.fedet.minigames.api.swm.world.properties.SlimeProperties.ENVIRONMENT))
                .pvp(this.propertyMap.getBoolean(it.fedet.minigames.api.swm.world.properties.SlimeProperties.PVP))
                .allowMonsters(this.propertyMap.getBoolean(it.fedet.minigames.api.swm.world.properties.SlimeProperties.ALLOW_MONSTERS))
                .allowAnimals(this.propertyMap.getBoolean(it.fedet.minigames.api.swm.world.properties.SlimeProperties.ALLOW_ANIMALS))
                .difficulty(Difficulty.valueOf(this.propertyMap.getString(it.fedet.minigames.api.swm.world.properties.SlimeProperties.DIFFICULTY).toUpperCase()).getValue())
                .readOnly(this.readOnly)
                .ignoreLocked(this.ignoreLocked)
                .build();
    }

    public byte[] serialize() {
        List<SlimeChunk> sortedChunks;
        synchronized (this.chunks) {
            sortedChunks = new ArrayList(this.chunks.values());
        }

        sortedChunks.sort(Comparator.comparingLong((chunkx) -> (long) chunkx.getZ() * 2147483647L + (long) chunkx.getX()));
        sortedChunks.removeIf((chunkx) -> chunkx == null || Arrays.stream(chunkx.getSections()).allMatch(Objects::isNull));
        this.extraData.getValue().put("properties", this.propertyMap.toCompound());
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        try {
            outStream.write(SlimeFormat.SLIME_HEADER);
            outStream.write(9);
            outStream.writeByte(this.version);
            int minX = sortedChunks.stream().mapToInt(SlimeChunk::getX).min().orElse(0);
            int minZ = sortedChunks.stream().mapToInt(SlimeChunk::getZ).min().orElse(0);
            int maxX = sortedChunks.stream().mapToInt(SlimeChunk::getX).max().orElse(0);
            int maxZ = sortedChunks.stream().mapToInt(SlimeChunk::getZ).max().orElse(0);
            outStream.writeShort(minX);
            outStream.writeShort(minZ);
            int width = maxX - minX + 1;
            int depth = maxZ - minZ + 1;
            outStream.writeShort(width);
            outStream.writeShort(depth);
            BitSet chunkBitset = new BitSet(width * depth);

            for (SlimeChunk chunk : sortedChunks) {
                int bitsetIndex = (chunk.getZ() - minZ) * width + (chunk.getX() - minX);
                chunkBitset.set(bitsetIndex, true);
            }

            int chunkMaskSize = (int) Math.ceil((double) (width * depth) / (double) 8.0F);
            writeBitSetAsBytes(outStream, chunkBitset, chunkMaskSize);
            byte[] chunkData = serializeChunks(sortedChunks, this.version);
            byte[] compressedChunkData = Zstd.compress(chunkData);
            outStream.writeInt(compressedChunkData.length);
            outStream.writeInt(chunkData.length);
            outStream.write(compressedChunkData);
            List<CompoundTag> tileEntitiesList = sortedChunks.stream().flatMap((chunkx) -> chunkx.getTileEntities().stream()).collect(Collectors.toList());
            ListTag<CompoundTag> tileEntitiesNbtList = new ListTag("tiles", TagType.TAG_COMPOUND, tileEntitiesList);
            CompoundTag tileEntitiesCompound = new CompoundTag("", new CompoundMap(Collections.singletonList(tileEntitiesNbtList)));
            byte[] tileEntitiesData = serializeCompoundTag(tileEntitiesCompound);
            byte[] compressedTileEntitiesData = Zstd.compress(tileEntitiesData);
            outStream.writeInt(compressedTileEntitiesData.length);
            outStream.writeInt(tileEntitiesData.length);
            outStream.write(compressedTileEntitiesData);
            List<CompoundTag> entitiesList = sortedChunks.stream().flatMap((chunkx) -> chunkx.getEntities().stream()).collect(Collectors.toList());
            outStream.writeBoolean(!entitiesList.isEmpty());
            if (!entitiesList.isEmpty()) {
                ListTag<CompoundTag> entitiesNbtList = new ListTag("entities", TagType.TAG_COMPOUND, entitiesList);
                CompoundTag entitiesCompound = new CompoundTag("", new CompoundMap(Collections.singletonList(entitiesNbtList)));
                byte[] entitiesData = serializeCompoundTag(entitiesCompound);
                byte[] compressedEntitiesData = Zstd.compress(entitiesData);
                outStream.writeInt(compressedEntitiesData.length);
                outStream.writeInt(entitiesData.length);
                outStream.write(compressedEntitiesData);
            }

            byte[] extra = serializeCompoundTag(this.extraData);
            byte[] compressedExtra = Zstd.compress(extra);
            outStream.writeInt(compressedExtra.length);
            outStream.writeInt(extra.length);
            outStream.write(compressedExtra);
            CompoundMap map = new CompoundMap();
            map.put("maps", new ListTag("maps", TagType.TAG_COMPOUND, this.worldMaps));
            CompoundTag mapsCompound = new CompoundTag("", map);
            byte[] mapArray = serializeCompoundTag(mapsCompound);
            byte[] compressedMapArray = Zstd.compress(mapArray);
            outStream.writeInt(compressedMapArray.length);
            outStream.writeInt(mapArray.length);
            outStream.write(compressedMapArray);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return outByteStream.toByteArray();
    }

    private static void writeBitSetAsBytes(DataOutputStream outStream, BitSet set, int fixedSize) throws IOException {
        byte[] array = set.toByteArray();
        outStream.write(array);
        int chunkMaskPadding = fixedSize - array.length;

        for (int i = 0; i < chunkMaskPadding; ++i) {
            outStream.write(0);
        }

    }

    private static byte[] serializeChunks(List<SlimeChunk> chunks, byte worldVersion) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(16384);
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        for (SlimeChunk chunk : chunks) {
            if (worldVersion >= 4) {
                byte[] heightMaps = serializeCompoundTag(chunk.getHeightMaps());
                outStream.writeInt(heightMaps.length);
                outStream.write(heightMaps);
            } else {
                int[] heightMap = chunk.getHeightMaps().getIntArrayValue("heightMap").get();

                for (int i = 0; i < 256; ++i) {
                    outStream.writeInt(heightMap[i]);
                }
            }

            int[] biomes = chunk.getBiomes();
            if (worldVersion >= 4) {
                outStream.writeInt(biomes.length);
            }

            for (int biome : biomes) {
                outStream.writeInt(biome);
            }

            SlimeChunkSection[] sections = chunk.getSections();
            BitSet sectionBitmask = new BitSet(16);

            for (int i = 0; i < sections.length; ++i) {
                sectionBitmask.set(i, sections[i] != null);
            }

            writeBitSetAsBytes(outStream, sectionBitmask, 2);

            for (SlimeChunkSection section : sections) {
                if (section != null) {
                    boolean hasBlockLight = section.blockLight() != null;
                    outStream.writeBoolean(hasBlockLight);
                    if (hasBlockLight) {
                        outStream.write(section.blockLight().getBacking());
                    }

                    if (worldVersion >= 4) {
                        List<CompoundTag> palette = section.palette().getValue();
                        outStream.writeInt(palette.size());

                        for (CompoundTag value : palette) {
                            byte[] serializedValue = serializeCompoundTag(value);
                            outStream.writeInt(serializedValue.length);
                            outStream.write(serializedValue);
                        }

                        long[] blockStates = section.blockStates();
                        outStream.writeInt(blockStates.length);

                        for (long value : section.blockStates()) {
                            outStream.writeLong(value);
                        }
                    } else {
                        outStream.write(section.blocks());
                        outStream.write(section.data().getBacking());
                    }

                    boolean hasSkyLight = section.skyLight() != null;
                    outStream.writeBoolean(hasSkyLight);
                    if (hasSkyLight) {
                        outStream.write(section.skyLight().getBacking());
                    }
                }
            }
        }

        return outByteStream.toByteArray();
    }

    private static byte[] serializeCompoundTag(CompoundTag tag) throws IOException {
        if (tag != null && !tag.getValue().isEmpty()) {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            NBTOutputStream outStream = new NBTOutputStream(outByteStream, 0, ByteOrder.BIG_ENDIAN);
            outStream.writeTag(tag);
            return outByteStream.toByteArray();
        } else {
            return new byte[0];
        }
    }

    public SlimeLoader getLoader() {
        return this.loader;
    }

    public String getName() {
        return this.name;
    }

    public Map<Long, SlimeChunk> getChunks() {
        return this.chunks;
    }

    public CompoundTag getExtraData() {
        return this.extraData;
    }

    public List<CompoundTag> getWorldMaps() {
        return this.worldMaps;
    }

    public byte getVersion() {
        return this.version;
    }

    public SlimePropertyMap getPropertyMap() {
        return this.propertyMap;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public boolean isIgnoreLocked() {
        return this.ignoreLocked;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLoader(SlimeLoader loader) {
        this.loader = loader;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public CraftSlimeWorld(SlimeLoader loader, String name, Map<Long, SlimeChunk> chunks, CompoundTag extraData, List<CompoundTag> worldMaps, byte version, SlimePropertyMap propertyMap, boolean readOnly, boolean ignoreLocked, boolean locked) {
        this.loader = loader;
        this.name = name;
        this.chunks = chunks;
        this.extraData = extraData;
        this.worldMaps = worldMaps;
        this.version = version;
        this.propertyMap = propertyMap;
        this.readOnly = readOnly;
        this.ignoreLocked = ignoreLocked;
        this.locked = locked;
    }
}
