package it.fedet.minigames.world.map;


import java.util.function.Function;

/**
 * A property object.
 */
public class SlimeProperty {

    private final String nbtName;
    private final PropertyType type;
    private final Object defaultValue;
    private final Function<Object, Boolean> validator;

    SlimeProperty(String nbtName, PropertyType type, Object defaultValue) {
        this(nbtName, type, defaultValue, null);
    }

    SlimeProperty(String nbtName, PropertyType type, Object defaultValue, Function<Object, Boolean> validator) {
        this.nbtName = nbtName;
        this.type = type;

        if (defaultValue != null) {
            if (!type.getValueClazz().isInstance(defaultValue)) {
                throw new IllegalArgumentException(defaultValue + " does not match class " + type.getValueClazz().getName());
            }

            if (validator != null) {
                if (!validator.apply(defaultValue)) {
                    throw new IllegalArgumentException("Invalid default value for property " + nbtName + "! " + defaultValue);
                }
            }
        }

        this.defaultValue = defaultValue;
        this.validator = validator;
    }

    public Function<Object, Boolean> getValidator() {
        return validator;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public PropertyType getType() {
        return type;
    }

    public String getNbtName() {
        return nbtName;
    }
}
