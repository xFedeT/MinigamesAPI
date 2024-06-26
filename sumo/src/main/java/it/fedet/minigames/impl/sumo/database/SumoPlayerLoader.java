package it.fedet.minigames.impl.sumo.database;

import it.fedet.minigames.api.game.database.DatabaseProvider;
import it.fedet.minigames.api.game.database.loader.UserDataLoader;
import it.fedet.minigames.impl.sumo.player.SumoPlayer;

import java.util.Optional;
import java.util.UUID;

public class SumoPlayerLoader extends UserDataLoader<SumoPlayer> {
    private final Database databaseService;

    public SumoPlayerLoader(DatabaseProvider<SumoPlayer> databaseService) {
        super(databaseService);
        this.databaseService = (Database) databaseService;
    }

    @Override
    public Optional<SumoPlayer> getOrCreate(UUID uuid, String name) {
        Optional<SumoPlayer> player = super.getOrCreate(uuid, name);

        //DO SOMETHING

        return player;
    }

}
