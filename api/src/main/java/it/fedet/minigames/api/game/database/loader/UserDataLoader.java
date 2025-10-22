package it.fedet.minigames.api.game.database.loader;

import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.loadit.DataLoader;
import it.fedet.minigames.api.loadit.UserData;
import it.fedet.minigames.api.logging.Logging;
import it.fedet.minigames.api.services.DatabaseService;

import java.util.Optional;
import java.util.UUID;

public class UserDataLoader<U extends UserData> implements DataLoader<U> {

    private final DatabaseProvider<U> databaseService;

    public UserDataLoader(DatabaseProvider<U> databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Optional<U> getOrCreate(UUID uuid, String name) {
        boolean result = databaseService.existsPlayer(uuid, name);
        Logging.info(DatabaseService.class, "Checking if player " + name + " exists: " + result);

        if (!result) {
            Logging.info(DatabaseService.class, "Player " + name + " does not exist.");
            databaseService.createPlayer(uuid, name);
        }

        Logging.info(DatabaseService.class, "Player " + name + " trying to create.");
        Optional<U> player = databaseService.retrievePlayer(uuid);
        return player.isEmpty() ? databaseService.retrievePlayer(name) : player;
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
