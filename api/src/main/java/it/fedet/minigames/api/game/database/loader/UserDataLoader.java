package it.fedet.minigames.api.game.database.loader;

import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.ytnoos.loadit.api.DataLoader;
import it.ytnoos.loadit.api.UserData;

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

        Optional<T> skywarsPlayer = databaseService.retrievePlayer(name);

        return skywarsPlayer;
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
