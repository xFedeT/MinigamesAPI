package it.fedet.minigames.api.game.database.loader;

import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.loadit.DataLoader;
import it.fedet.minigames.api.loadit.UserData;

import java.util.Optional;
import java.util.UUID;

public class UserDataLoader<U extends UserData> implements DataLoader<U> {

    private final DatabaseProvider<U> databaseService;

    public UserDataLoader(DatabaseProvider<U> databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Optional<U> getOrCreate(UUID uuid, String name) {
        boolean result = databaseService.existsPlayer(name);

        if (!result) {
            databaseService.createPlayer(name);
        }

        return databaseService.retrievePlayer(name);
    }

    @Override
    public Optional<U> load(UUID uuid) {
        return databaseService.retrievePlayer(uuid);
    }

    @Override
    public Optional<U> load(String name) {
        return databaseService.retrievePlayer(name);
    }

}
