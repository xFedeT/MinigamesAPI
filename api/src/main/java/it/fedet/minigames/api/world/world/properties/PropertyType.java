//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.world.properties;

public enum PropertyType {
    STRING(String.class),
    BOOLEAN(Boolean.class),
    INT(Integer.class);

    private final Class<?> valueClazz;

    public Class<?> getValueClazz() {
        return this.valueClazz;
    }

    private PropertyType(Class<?> valueClazz) {
        this.valueClazz = valueClazz;
    }
}
