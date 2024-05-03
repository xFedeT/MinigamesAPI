package it.fedet.minigames.api.game.database.loader;

import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.loadit.DataLoader;
import it.fedet.minigames.api.loadit.UserData;


import java.util.Optional;
import java.util.UUID;

public class UserDataLoader<T extends UserData> implements DataLoader<T> {

    private final DatabaseProvider<T> databaseService;

    public UserDataLoader(DatabaseProvider<T> databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Optional<T> getOrCreate(UUID uuid, String name) {
        boolean result = databaseService.existsPlayer(name);

        if (!result) {
            databaseService.createPlayer(name);
        }

        Optional<T> userData = databaseService.retrievePlayer(name);

        return userData;
    }

    @Override
    public Optional<T> load(UUID uuid) {
        return databaseService.retrievePlayer(uuid);
    }

    @Override
    public Optional<T> load(String name) {
        return databaseService.retrievePlayer(name);
    }

}
