package it.fedet.minigames.api.world.world;


import com.flowpowered.nbt.CompoundTag;
import it.fedet.minigames.api.world.exceptions.WorldAlreadyExistsException;
import it.fedet.minigames.api.world.loaders.SlimeLoader;
import it.fedet.minigames.api.world.world.properties.WorldPropertyMap;

import java.io.IOException;
import java.util.Collection;

public interface SlimeWorld {

    String getName();

    SlimeLoader getLoader();

    SlimeChunk getChunk(int x, int z);

    CompoundTag getExtraData();

    Collection<CompoundTag> getWorldMaps();

    /**
     * @deprecated
     */
    @Deprecated
    SlimeProperties getProperties();

    WorldPropertyMap getPropertyMap();

    boolean isReadOnly();

    void setReadOnly(boolean readOnly);

    SlimeWorld clone(String name);

    SlimeWorld clone(String name, SlimeLoader loader) throws WorldAlreadyExistsException, IOException;

    SlimeWorld clone(String name, SlimeLoader loader, boolean lock)
            throws WorldAlreadyExistsException, IOException;

    boolean isLocked();

    boolean isIgnoreLocked();

    // ===============================
    // Nested Deprecated SlimeProperties
    // ===============================

    /**
     * @deprecated
     */
    @Deprecated
    class SlimeProperties {

        private final double spawnX;
        private final double spawnY;
        private final double spawnZ;
        private final int difficulty;
        private final boolean allowMonsters;
        private final boolean allowAnimals;
        private final boolean readOnly;
        private final boolean ignoreLocked;
        private final boolean pvp;
        private final String environment;

        // Default values (match original SWM behavior)
        private static final double DEFAULT_SPAWN_Y = 255.0;
        private static final boolean DEFAULT_ALLOW_MONSTERS = true;
        private static final boolean DEFAULT_ALLOW_ANIMALS = true;
        private static final boolean DEFAULT_PVP = true;
        private static final String DEFAULT_ENVIRONMENT = "NORMAL";

        private SlimeProperties(double spawnX, double spawnY, double spawnZ, int difficulty,
                                boolean allowMonsters, boolean allowAnimals,
                                boolean readOnly, boolean ignoreLocked,
                                boolean pvp, String environment) {
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.spawnZ = spawnZ;
            this.difficulty = difficulty;
            this.allowMonsters = allowMonsters;
            this.allowAnimals = allowAnimals;
            this.readOnly = readOnly;
            this.ignoreLocked = ignoreLocked;
            this.pvp = pvp;
            this.environment = environment;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder toBuilder() {
            return new Builder()
                    .spawnX(spawnX)
                    .spawnY(spawnY)
                    .spawnZ(spawnZ)
                    .difficulty(difficulty)
                    .allowMonsters(allowMonsters)
                    .allowAnimals(allowAnimals)
                    .readOnly(readOnly)
                    .ignoreLocked(ignoreLocked)
                    .pvp(pvp)
                    .environment(environment);
        }

        // ===================
        // Getters
        // ===================
        public double getSpawnX() {
            return spawnX;
        }

        public double getSpawnY() {
            return spawnY;
        }

        public double getSpawnZ() {
            return spawnZ;
        }

        public int getDifficulty() {
            return difficulty;
        }

        public boolean allowMonsters() {
            return allowMonsters;
        }

        public boolean allowAnimals() {
            return allowAnimals;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

        public boolean isIgnoreLocked() {
            return ignoreLocked;
        }

        public boolean isPvp() {
            return pvp;
        }

        public String getEnvironment() {
            return environment;
        }

        // ===================
        // Mutations (deprecated style)
        // ===================
        public SlimeProperties withReadOnly(boolean readOnly) {
            return this.readOnly == readOnly ? this :
                    new SlimeProperties(spawnX, spawnY, spawnZ, difficulty,
                            allowMonsters, allowAnimals, readOnly,
                            ignoreLocked, pvp, environment);
        }

        public SlimeProperties withIgnoreLocked(boolean ignoreLocked) {
            return this.ignoreLocked == ignoreLocked ? this :
                    new SlimeProperties(spawnX, spawnY, spawnZ, difficulty,
                            allowMonsters, allowAnimals, readOnly,
                            ignoreLocked, pvp, environment);
        }

        // ===================
        // Builder
        // ===================
        public static class Builder {
            private double spawnX;
            private Double spawnY;
            private double spawnZ;
            private int difficulty;
            private Boolean allowMonsters;
            private Boolean allowAnimals;
            private boolean readOnly;
            private boolean ignoreLocked;
            private Boolean pvp;
            private String environment;

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
                return new SlimeProperties(
                        spawnX,
                        spawnY != null ? spawnY : DEFAULT_SPAWN_Y,
                        spawnZ,
                        difficulty,
                        allowMonsters != null ? allowMonsters : DEFAULT_ALLOW_MONSTERS,
                        allowAnimals != null ? allowAnimals : DEFAULT_ALLOW_ANIMALS,
                        readOnly,
                        ignoreLocked,
                        pvp != null ? pvp : DEFAULT_PVP,
                        environment != null ? environment : DEFAULT_ENVIRONMENT
                );
            }
        }
    }
}
