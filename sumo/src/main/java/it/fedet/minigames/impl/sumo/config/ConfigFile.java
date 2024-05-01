package it.fedet.minigames.impl.sumo.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import it.fedet.minigames.api.config.MinigameConfig;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ConfigFile implements MinigameConfig {

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public String getFileName() {
        return "config.yml";
    }

    @Override
    public Class<? extends SettingsHolder> getClazz() {
        return ConfigFile.class;
    }

    public static final Property<String> SCRITTA = newProperty("scritta", "funzia");



}
