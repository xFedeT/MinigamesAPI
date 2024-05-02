package it.fedet.minigames.impl.sumo.game.settings;

import it.fedet.minigames.api.settings.GameSetting;

public interface SettingsFile {

    GameSetting<String> NOME = new GameSetting<>(0, "Ciao", "sono", "fede");

}
