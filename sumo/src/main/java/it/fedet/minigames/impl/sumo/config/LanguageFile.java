package it.fedet.minigames.impl.sumo.config;

import ch.jalu.configme.SettingsHolder;
import it.fedet.minigames.api.config.MinigameConfig;

public class LanguageFile implements MinigameConfig {
    @Override
    public String getPath() {
        return "";
    }

    @Override
    public String getFileName() {
        return "language.yml";
    }

    @Override
    public Class<? extends SettingsHolder> getClazz() {
        return LanguageFile.class;
    }
}
