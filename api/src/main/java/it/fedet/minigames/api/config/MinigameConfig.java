package it.fedet.minigames.api.config;

import ch.jalu.configme.SettingsHolder;

public interface MinigameConfig extends SettingsHolder {

    String getPath();
    String getFileName();
    Class<? extends SettingsHolder> getClazz();
}
