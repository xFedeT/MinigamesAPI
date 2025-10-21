//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.world.properties;

import java.util.function.Function;

public class WorldProperty {
    private final String nbtName;
    private final PropertyType type;
    private final Object defaultValue;
    private final Function<Object, Boolean> validator;

    WorldProperty(String nbtName, PropertyType type, Object defaultValue) {
        this(nbtName, type, defaultValue, (Function) null);
    }

    WorldProperty(String nbtName, PropertyType type, Object defaultValue, Function<Object, Boolean> validator) {
        this.nbtName = nbtName;
        this.type = type;
        if (defaultValue != null) {
            if (!type.getValueClazz().isInstance(defaultValue)) {
                throw new IllegalArgumentException(defaultValue + " does not match class " + type.getValueClazz().getName());
            }

            if (validator != null && !(Boolean) validator.apply(defaultValue)) {
                throw new IllegalArgumentException("Invalid default value for property " + nbtName + "! " + defaultValue);
            }
        }

        this.defaultValue = defaultValue;
        this.validator = validator;
    }

    public String getNbtName() {
        return this.nbtName;
    }

    public PropertyType getType() {
        return this.type;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public Function<Object, Boolean> getValidator() {
        return this.validator;
    }
}
