package it.fedet.minigames.api.world.world.properties;


import com.flowpowered.nbt.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Holds and manages the dynamic property map for a SlimeWorld.
 * Allows getting, setting, serializing, and deserializing world-level properties.
 */
public class WorldPropertyMap {

    private final Map<WorldProperty, Object> values;

    /**
     * Creates an empty property map.
     */
    public WorldPropertyMap() {
        this(new HashMap<>());
    }

    private WorldPropertyMap(Map<WorldProperty, Object> values) {
        this.values = values;
    }

    /* =========================================================
       STRING
       ========================================================= */
    public String getString(WorldProperty property) {
        ensureType(property, PropertyType.STRING);
        String value = (String) values.get(property);
        if (value == null) {
            value = (String) property.getDefaultValue();
        }
        return value;
    }

    public void setString(WorldProperty property, String value) {
        Objects.requireNonNull(value, "Property value cannot be null");
        ensureType(property, PropertyType.STRING);

        if (property.getValidator() != null && !property.getValidator().apply(value)) {
            throw new IllegalArgumentException("'" + value + "' is not a valid property value.");
        }

        values.put(property, value);
    }

    /* =========================================================
       BOOLEAN
       ========================================================= */
    public Boolean getBoolean(WorldProperty property) {
        ensureType(property, PropertyType.BOOLEAN);
        Boolean value = (Boolean) values.get(property);
        if (value == null) {
            value = (Boolean) property.getDefaultValue();
        }
        return value;
    }

    public void setBoolean(WorldProperty property, boolean value) {
        ensureType(property, PropertyType.BOOLEAN);
        values.put(property, value);
    }

    /* =========================================================
       INT
       ========================================================= */
    public int getInt(WorldProperty property) {
        ensureType(property, PropertyType.INT);
        Integer value = (Integer) values.get(property);
        if (value == null) {
            value = (Integer) property.getDefaultValue();
        }
        return value;
    }

    public void setInt(WorldProperty property, int value) {
        ensureType(property, PropertyType.INT);

        if (property.getValidator() != null && !property.getValidator().apply(value)) {
            throw new IllegalArgumentException("'" + value + "' is not a valid property value.");
        }

        values.put(property, value);
    }

    /* =========================================================
       INTERNAL HELPERS
       ========================================================= */
    private void ensureType(WorldProperty property, PropertyType requiredType) {
        if (property.getType() != requiredType) {
            throw new IllegalArgumentException(
                    "Property " + property.getNbtName() +
                            " type is " + property.getType().name() +
                            ", not " + requiredType.name()
            );
        }
    }

    /* =========================================================
       MERGE / NBT CONVERSION
       ========================================================= */
    public void merge(WorldPropertyMap other) {
        values.putAll(other.values);
    }

    public CompoundTag toCompound() {
        CompoundMap map = new CompoundMap();

        for (Map.Entry<WorldProperty, Object> entry : values.entrySet()) {
            WorldProperty property = entry.getKey();
            Object value = entry.getValue();

            switch (property.getType()) {
                case STRING:
                    map.put(property.getNbtName(), new StringTag(property.getNbtName(), (String) value));
                    break;
                case BOOLEAN:
                    map.put(property.getNbtName(), new ByteTag(property.getNbtName(), (byte) ((Boolean) value ? 1 : 0)));
                    break;
                case INT:
                    map.put(property.getNbtName(), new IntTag(property.getNbtName(), (Integer) value));
                    break;
            }
        }

        return new CompoundTag("properties", map);
    }

    public static WorldPropertyMap fromCompound(CompoundTag compound) {
        Map<WorldProperty, Object> values = new HashMap<>();

        for (WorldProperty property : WorldProperties.VALUES) {
            switch (property.getType()) {
                case STRING:
                    compound.getStringValue(property.getNbtName())
                            .ifPresent(value -> values.put(property, value));
                    break;

                case BOOLEAN:
                    compound.getByteValue(property.getNbtName())
                            .map(v -> v == 1)
                            .ifPresent(value -> values.put(property, value));
                    break;

                case INT:
                    compound.getIntValue(property.getNbtName())
                            .ifPresent(value -> values.put(property, value));
                    break;
            }
        }

        return new WorldPropertyMap(values);
    }

    /* =========================================================
       INTERNAL ACCESSORS
       ========================================================= */
    Map<WorldProperty, Object> getValues() {
        return values;
    }
}
