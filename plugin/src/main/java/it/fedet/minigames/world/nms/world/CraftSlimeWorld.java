package it.fedet.minigames.world.nms.world;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.github.luben.zstd.Zstd;
import it.fedet.minigames.api.world.SlimeLoader;
import it.fedet.minigames.api.world.data.SlimeWorld;
import it.fedet.minigames.api.world.exception.WorldAlreadyExistsException;
import it.fedet.minigames.world.loader.SlimeFormat;
import it.fedet.minigames.world.map.SlimePropertyMap;
import it.fedet.minigames.world.nms.chunk.CraftSlimeChunkSection;
import it.fedet.minigames.world.nms.chunk.SlimeChunk;
import org.bukkit.Difficulty;

import java.io.*;
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

    private final boolean readOnly;
    private final boolean ignoreLocked;
    private final boolean locked;

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

    @Override
    public String getName() {
        return name;
    }

    public SlimeChunk getChunk(int x, int z) {
        synchronized (chunks) {
            Long index = (((long) z) * Integer.MAX_VALUE + ((long) x));

            return chunks.get(index);
        }
    }

    public void updateChunk(SlimeChunk chunk) {
        if (!chunk.getWorldName().equals(getName())) {
            throw new IllegalArgumentException("Chunk (" + chunk.getX() + ", " + chunk.getZ() + ") belongs to world '"
                    + chunk.getWorldName() + "', not to '" + getName() + "'!");
        }

        synchronized (chunks) {
            chunks.put(((long) chunk.getZ()) * Integer.MAX_VALUE + ((long) chunk.getX()), chunk);
        }
    }

    @Override
    public CraftSlimeWorld clone(String worldName) {
        try {
            return clone(worldName, null);
        } catch (WorldAlreadyExistsException | IOException ignored) {
            return null; // Never going to happen
        }
    }

    @Override
    public CraftSlimeWorld clone(String worldName, SlimeLoader loader) throws WorldAlreadyExistsException, IOException {
        return clone(worldName, loader, true);
    }

    @Override
    public CraftSlimeWorld clone(String worldName, SlimeLoader loader, boolean lock) throws WorldAlreadyExistsException, IOException {if (name.equals(worldName)) {
        throw new IllegalArgumentException("The clone world cannot have the same name as the original world!");
    }

        if (worldName == null) {
            throw new IllegalArgumentException("The world name cannot be null!");
        }

        if (loader != null) {
            if (loader.worldExists(worldName)) {
                throw new WorldAlreadyExistsException(worldName);
            }
        }

        CraftSlimeWorld world;

        synchronized (chunks) {
            world = new CraftSlimeWorld(loader == null ? this.loader : loader, worldName, new HashMap<>(chunks), extraData.clone(),
                    new ArrayList<>(worldMaps), version, propertyMap, loader == null, this.ignoreLocked, lock);
        }

        if (loader != null) {
            loader.saveWorld(worldName, world.serialize(), lock);
        }

        return world;
    }

    public SlimeProperties getProperties() {
        return SlimeProperties.builder().spawnX(propertyMap.getInt(it.fedet.minigames.world.map.SlimeProperties.SPAWN_X))
                .spawnY(propertyMap.getInt(it.fedet.minigames.world.map.SlimeProperties.SPAWN_Y))
                .spawnZ(propertyMap.getInt(it.fedet.minigames.world.map.SlimeProperties.SPAWN_Z))
                .environment(propertyMap.getString(it.fedet.minigames.world.map.SlimeProperties.ENVIRONMENT))
                .pvp(propertyMap.getBoolean(it.fedet.minigames.world.map.SlimeProperties.PVP))
                .allowMonsters(propertyMap.getBoolean(it.fedet.minigames.world.map.SlimeProperties.ALLOW_MONSTERS))
                .allowAnimals(propertyMap.getBoolean(it.fedet.minigames.world.map.SlimeProperties.ALLOW_ANIMALS))
                .difficulty(Difficulty.valueOf(propertyMap.getString(it.fedet.minigames.world.map.SlimeProperties.DIFFICULTY).toUpperCase()).getValue())
                .readOnly(readOnly).build();
    }

    /**
     * All the currently-available properties of the world.
     *
     * @deprecated see {@link SlimePropertyMap}
     */
    @Deprecated
    public static class SlimeProperties {

        private double spawnX;
        private double spawnY = 255;
        private double spawnZ;

        private int difficulty;

        private boolean allowMonsters = true;
        private boolean allowAnimals = true;

        private boolean readOnly;
        private boolean ignoreLocked;
        private boolean pvp = true;

        private String environment = "NORMAL";

        // --- Costruttori ---

        public SlimeProperties() {}

        private SlimeProperties(Builder builder) {
            this.spawnX = builder.spawnX;
            this.spawnY = builder.spawnY;
            this.spawnZ = builder.spawnZ;
            this.difficulty = builder.difficulty;
            this.allowMonsters = builder.allowMonsters;
            this.allowAnimals = builder.allowAnimals;
            this.ignoreLocked = builder.ignoreLocked;
            this.readOnly = builder.readOnly;
            this.pvp = builder.pvp;
            this.environment = builder.environment;
        }

        // --- Getters e Setters ---

        public double getSpawnX() {
            return spawnX;
        }

        public void setSpawnX(double spawnX) {
            this.spawnX = spawnX;
        }

        public double getSpawnY() {
            return spawnY;
        }

        public void setSpawnY(double spawnY) {
            this.spawnY = spawnY;
        }

        public double getSpawnZ() {
            return spawnZ;
        }

        public void setSpawnZ(double spawnZ) {
            this.spawnZ = spawnZ;
        }

        public int getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(int difficulty) {
            this.difficulty = difficulty;
        }

        public boolean allowMonsters() {
            return allowMonsters;
        }

        public void setAllowMonsters(boolean allowMonsters) {
            this.allowMonsters = allowMonsters;
        }

        public boolean allowAnimals() {
            return allowAnimals;
        }

        public void setAllowAnimals(boolean allowAnimals) {
            this.allowAnimals = allowAnimals;
        }

        public boolean isIgnoreLocked() {
            return ignoreLocked;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

        public boolean isPvp() {
            return pvp;
        }

        public void setPvp(boolean pvp) {
            this.pvp = pvp;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        // --- Metodi stile Lombok ---

        /** Simula @Wither per readOnly */
        public SlimeProperties withReadOnly(boolean readOnly) {
            SlimeProperties copy = this.toBuilder().readOnly(readOnly).build();
            return copy;
        }

        /** Simula @Builder(toBuilder = true) */
        public Builder toBuilder() {
            return new Builder()
                    .spawnX(spawnX)
                    .spawnY(spawnY)
                    .spawnZ(spawnZ)
                    .difficulty(difficulty)
                    .allowMonsters(allowMonsters)
                    .allowAnimals(allowAnimals)
                    .ignoreLocked(ignoreLocked)
                    .readOnly(readOnly)
                    .pvp(pvp)
                    .environment(environment);
        }

        // --- Builder manuale ---

        public static class Builder {
            private double spawnX;
            private double spawnY = 255;
            private double spawnZ;
            private int difficulty;
            private boolean allowMonsters = true;
            private boolean allowAnimals = true;
            private boolean readOnly;
            private boolean ignoreLocked;
            private boolean pvp = true;
            private String environment = "NORMAL";

            public Builder() {}

            public Builder spawnX(double spawnX) {
                this.spawnX = spawnX;
                return this;
            }

            public Builder spawnY(double spawnY) {
                this.spawnY = spawnY;
                return this;
            }

            public Builder spawnZ(double spawnZ) {
                this.spawnZ = spawnZ;
                return this;
            }

            public Builder difficulty(int difficulty) {
                this.difficulty = difficulty;
                return this;
            }

            public Builder allowMonsters(boolean allowMonsters) {
                this.allowMonsters = allowMonsters;
                return this;
            }

            public Builder allowAnimals(boolean allowAnimals) {
                this.allowAnimals = allowAnimals;
                return this;
            }

            public Builder readOnly(boolean readOnly) {
                this.readOnly = readOnly;
                return this;
            }

            public Builder ignoreLocked(boolean ignoreLocked) {
                this.ignoreLocked = ignoreLocked;
                return this;
            }

            public Builder pvp(boolean pvp) {
                this.pvp = pvp;
                return this;
            }

            public Builder environment(String environment) {
                this.environment = environment;
                return this;
            }

            public SlimeProperties build() {
                return new SlimeProperties(this);
            }
        }

        // --- Utility: toString, equals, hashCode ---

        @Override
        public String toString() {
            return "SlimeProperties{" +
                    "spawnX=" + spawnX +
                    ", spawnY=" + spawnY +
                    ", spawnZ=" + spawnZ +
                    ", difficulty=" + difficulty +
                    ", allowMonsters=" + allowMonsters +
                    ", allowAnimals=" + allowAnimals +
                    ", isIgnoreLocked=" + ignoreLocked +
                    ", readOnly=" + readOnly +
                    ", pvp=" + pvp +
                    ", environment='" + environment + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SlimeProperties)) return false;
            SlimeProperties that = (SlimeProperties) o;
            return Double.compare(that.spawnX, spawnX) == 0 &&
                    Double.compare(that.spawnY, spawnY) == 0 &&
                    Double.compare(that.spawnZ, spawnZ) == 0 &&
                    difficulty == that.difficulty &&
                    allowMonsters == that.allowMonsters &&
                    allowAnimals == that.allowAnimals &&
                    ignoreLocked == that.ignoreLocked &&
                    readOnly == that.readOnly &&
                    pvp == that.pvp &&
                    environment.equals(that.environment);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(spawnX, spawnY, spawnZ, difficulty, allowMonsters, allowAnimals, ignoreLocked, readOnly, pvp, environment);
        }

        // --- Metodo statico builder() per comodità ---
        public static Builder builder() {
            return new Builder();
        }
    }


    // World Serialization methods

    public byte[] serialize() {
        List<SlimeChunk> sortedChunks;

        synchronized (chunks) {
            sortedChunks = new ArrayList<>(chunks.values());
        }

        sortedChunks.sort(Comparator.comparingLong(chunk -> (long) chunk.getZ() * Integer.MAX_VALUE + (long) chunk.getX()));
        sortedChunks.removeIf(chunk -> chunk == null || Arrays.stream(chunk.getSections()).allMatch(Objects::isNull)); // Remove empty chunks to save space

        // Store world properties
        extraData.getValue().put("properties", propertyMap.toCompound());

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        try {
            // File Header and Slime version
            outStream.write(SlimeFormat.SLIME_HEADER);
            outStream.write(SlimeFormat.SLIME_VERSION);

            // World version
            outStream.writeByte(version);

            // Lowest chunk coordinates
            int minX = sortedChunks.stream().mapToInt(SlimeChunk::getX).min().orElse(0);
            int minZ = sortedChunks.stream().mapToInt(SlimeChunk::getZ).min().orElse(0);
            int maxX = sortedChunks.stream().mapToInt(SlimeChunk::getX).max().orElse(0);
            int maxZ = sortedChunks.stream().mapToInt(SlimeChunk::getZ).max().orElse(0);

            outStream.writeShort(minX);
            outStream.writeShort(minZ);

            // Width and depth
            int width = maxX - minX + 1;
            int depth = maxZ - minZ + 1;

            outStream.writeShort(width);
            outStream.writeShort(depth);

            // Chunk Bitmask
            BitSet chunkBitset = new BitSet(width * depth);

            for (SlimeChunk chunk : sortedChunks) {
                int bitsetIndex = (chunk.getZ() - minZ) * width + (chunk.getX() - minX);

                chunkBitset.set(bitsetIndex, true);
            }

            int chunkMaskSize = (int) Math.ceil((width * depth) / 8.0D);
            writeBitSetAsBytes(outStream, chunkBitset, chunkMaskSize);

            // Chunks
            byte[] chunkData = serializeChunks(sortedChunks, version);
            byte[] compressedChunkData = Zstd.compress(chunkData);

            outStream.writeInt(compressedChunkData.length);
            outStream.writeInt(chunkData.length);
            outStream.write(compressedChunkData);

            // Tile Entities
            List<CompoundTag> tileEntitiesList = sortedChunks.stream().flatMap(chunk -> chunk.getTileEntities().stream()).collect(Collectors.toList());
            ListTag<CompoundTag> tileEntitiesNbtList = new ListTag<>("tiles", TagType.TAG_COMPOUND, tileEntitiesList);
            CompoundTag tileEntitiesCompound = new CompoundTag("", new CompoundMap(Collections.singletonList(tileEntitiesNbtList)));
            byte[] tileEntitiesData = serializeCompoundTag(tileEntitiesCompound);
            byte[] compressedTileEntitiesData = Zstd.compress(tileEntitiesData);

            outStream.writeInt(compressedTileEntitiesData.length);
            outStream.writeInt(tileEntitiesData.length);
            outStream.write(compressedTileEntitiesData);

            // Entities
            List<CompoundTag> entitiesList = sortedChunks.stream().flatMap(chunk -> chunk.getEntities().stream()).collect(Collectors.toList());

            outStream.writeBoolean(!entitiesList.isEmpty());

            if (!entitiesList.isEmpty()) {
                ListTag<CompoundTag> entitiesNbtList = new ListTag<>("entities", TagType.TAG_COMPOUND, entitiesList);
                CompoundTag entitiesCompound = new CompoundTag("", new CompoundMap(Collections.singletonList(entitiesNbtList)));
                byte[] entitiesData = serializeCompoundTag(entitiesCompound);
                byte[] compressedEntitiesData = Zstd.compress(entitiesData);

                outStream.writeInt(compressedEntitiesData.length);
                outStream.writeInt(entitiesData.length);
                outStream.write(compressedEntitiesData);
            }

            // Extra Tag
            byte[] extra = serializeCompoundTag(extraData);
            byte[] compressedExtra = Zstd.compress(extra);

            outStream.writeInt(compressedExtra.length);
            outStream.writeInt(extra.length);
            outStream.write(compressedExtra);

            // World Maps
            CompoundMap map = new CompoundMap();
            map.put("maps", new ListTag<>("maps", TagType.TAG_COMPOUND, worldMaps));

            CompoundTag mapsCompound = new CompoundTag("", map);

            byte[] mapArray = serializeCompoundTag(mapsCompound);
            byte[] compressedMapArray = Zstd.compress(mapArray);

            outStream.writeInt(compressedMapArray.length);
            outStream.writeInt(mapArray.length);
            outStream.write(compressedMapArray);
        } catch (IOException ex) { // Ignore
            ex.printStackTrace();
        }

        return outByteStream.toByteArray();
    }

    private static void writeBitSetAsBytes(DataOutputStream outStream, BitSet set, int fixedSize) throws IOException {
        byte[] array = set.toByteArray();
        outStream.write(array);

        int chunkMaskPadding = fixedSize - array.length;

        for (int i = 0; i < chunkMaskPadding; i++) {
            outStream.write(0);
        }
    }

    private static byte[] serializeChunks(List<SlimeChunk> chunks, byte worldVersion) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(16384);
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        for (SlimeChunk chunk : chunks) {
            // Height Maps
            if (worldVersion >= 0x04) {
                byte[] heightMaps = serializeCompoundTag(chunk.getHeightMaps());
                outStream.writeInt(heightMaps.length);
                outStream.write(heightMaps);
            } else {
                int[] heightMap = chunk.getHeightMaps().getIntArrayValue("heightMap").get();

                for (int i = 0; i < 256; i++) {
                    outStream.writeInt(heightMap[i]);
                }
            }

            // Biomes
            int[] biomes = chunk.getBiomes();
            if (worldVersion >= 0x04) {
                outStream.writeInt(biomes.length);
            }

            for (int biome : biomes) {
                outStream.writeInt(biome);
            }

            // Chunk sections
            CraftSlimeChunkSection[] sections = chunk.getSections();
            BitSet sectionBitmask = new BitSet(16);

            for (int i = 0; i < sections.length; i++) {
                sectionBitmask.set(i, sections[i] != null);
            }

            writeBitSetAsBytes(outStream, sectionBitmask, 2);

            for (CraftSlimeChunkSection section : sections) {
                if (section == null) {
                    continue;
                }

                // Block Light
                boolean hasBlockLight = section.getBlockLight() != null;
                outStream.writeBoolean(hasBlockLight);

                if (hasBlockLight) {
                    outStream.write(section.getBlockLight().getBacking());
                }

                // Block Data
                if (worldVersion >= 0x04) {
                    // Palette
                    List<CompoundTag> palette = section.getPalette().getValue();
                    outStream.writeInt(palette.size());

                    for (CompoundTag value : palette) {
                        byte[] serializedValue = serializeCompoundTag(value);

                        outStream.writeInt(serializedValue.length);
                        outStream.write(serializedValue);
                    }

                    // Block states
                    long[] blockStates = section.getBlockStates();

                    outStream.writeInt(blockStates.length);

                    for (long value : section.getBlockStates()) {
                        outStream.writeLong(value);
                    }
                } else {
                    outStream.write(section.getBlocks());
                    outStream.write(section.getData().getBacking());
                }

                // Sky Light
                boolean hasSkyLight = section.getSkyLight() != null;
                outStream.writeBoolean(hasSkyLight);

                if (hasSkyLight) {
                    outStream.write(section.getSkyLight().getBacking());
                }
            }
        }

        return outByteStream.toByteArray();
    }

    private static byte[] serializeCompoundTag(CompoundTag tag) throws IOException {
        if (tag == null || tag.getValue().isEmpty()) {
            return new byte[0];
        }

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        NBTOutputStream outStream = new NBTOutputStream(outByteStream, NBTInputStream.NO_COMPRESSION, ByteOrder.BIG_ENDIAN);
        outStream.writeTag(tag);

        return outByteStream.toByteArray();
    }

    public SlimeLoader getLoader() {
        return loader;
    }

    public void setLoader(SlimeLoader loader) {
        this.loader = loader;
    }

    public Map<Long, SlimeChunk> getChunks() {
        return chunks;
    }

    public List<CompoundTag> getWorldMaps() {
        return worldMaps;
    }

    public CompoundTag getExtraData() {
        return extraData;
    }

    public SlimePropertyMap getPropertyMap() {
        return propertyMap;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isIgnoreLocked() {
        return this.ignoreLocked;
    }

    public boolean isReadOnly() {
        return readOnly;
    }


}
