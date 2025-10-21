package it.fedet.minigames.api.swm.world.properties;


import com.flowpowered.nbt.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Holds and manages the dynamic property map for a SlimeWorld.
 * Allows getting, setting, serializing, and deserializing world-level properties.
 */
public class SlimePropertyMap {

    private final Map<SlimeProperty, Object> values;

    /** Creates an empty property map. */
    public SlimePropertyMap() {
        this(new HashMap<>());
    }

    private SlimePropertyMap(Map<SlimeProperty, Object> values) {
        this.values = values;
    }

    /* =========================================================
       STRING
       ========================================================= */
    public String getString(SlimeProperty property) {
        ensureType(property, PropertyType.STRING);
        String value = (String) values.get(property);
        if (value == null) {
            value = (String) property.getDefaultValue();
        }
        return value;
    }

    public void setString(SlimeProperty property, String value) {
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
    public Boolean getBoolean(SlimeProperty property) {
        ensureType(property, PropertyType.BOOLEAN);
        Boolean value = (Boolean) values.get(property);
        if (value == null) {
            value = (Boolean) property.getDefaultValue();
        }
        return value;
    }

    public void setBoolean(SlimeProperty property, boolean value) {
        ensureType(property, PropertyType.BOOLEAN);
        values.put(property, value);
    }

    /* =========================================================
       INT
       ========================================================= */
    public int getInt(SlimeProperty property) {
        ensureType(property, PropertyType.INT);
        Integer value = (Integer) values.get(property);
        if (value == null) {
            value = (Integer) property.getDefaultValue();
        }
        return value;
    }

    public void setInt(SlimeProperty property, int value) {
        ensureType(property, PropertyType.INT);

        if (property.getValidator() != null && !property.getValidator().apply(value)) {
            throw new IllegalArgumentException("'" + value + "' is not a valid property value.");
        }

        values.put(property, value);
    }

    /* =========================================================
       INTERNAL HELPERS
       ========================================================= */
    private void ensureType(SlimeProperty property, PropertyType requiredType) {
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
    public void merge(SlimePropertyMap other) {
        values.putAll(other.values);
    }

    public CompoundTag toCompound() {
        CompoundMap map = new CompoundMap();

        for (Map.Entry<SlimeProperty, Object> entry : values.entrySet()) {
            SlimeProperty property = entry.getKey();
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

    public static SlimePropertyMap fromCompound(CompoundTag compound) {
        Map<SlimeProperty, Object> values = new HashMap<>();

        for (SlimeProperty property : SlimeProperties.VALUES) {
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

        return new SlimePropertyMap(values);
    }

    /* =========================================================
       INTERNAL ACCESSORS
       ========================================================= */
    Map<SlimeProperty, Object> getValues() {
        return values;
    }
}
