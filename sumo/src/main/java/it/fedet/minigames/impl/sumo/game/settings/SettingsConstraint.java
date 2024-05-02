package it.fedet.minigames.impl.sumo.game.settings;

import it.fedet.minigames.api.game.settings.GameSetting;

public interface SettingsConstraint {

    GameSetting<String> NOME = new GameSetting<>(0, "Ciao", "sono", "fede");

}
