package it.fedet.minigames.world.map;


/**
 * Enum containing all the types of properties.
 */
public enum PropertyType {
    STRING(String.class), BOOLEAN(Boolean.class), INT(Integer.class);

    private final Class<?> valueClazz;

    PropertyType(Class<?> valueClazz) {
        this.valueClazz = valueClazz;
    }

    public Class<?> getValueClazz() {
        return valueClazz;
    }
}
