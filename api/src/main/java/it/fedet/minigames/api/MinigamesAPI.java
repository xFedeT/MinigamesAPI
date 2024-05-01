package it.fedet.minigames.api;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import it.fedet.minigames.api.config.MinigameConfig;
import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.services.Service;

import java.util.List;

public interface MinigamesAPI {

    SettingsManager getSettings(Class<? extends SettingsHolder> type);

    <T extends Service> T getService(Class<T> service);

    <T extends DatabaseProvider> boolean registerDatabaseProvider(T provider);

    void registerMinigame(Minigame minigame);
}
