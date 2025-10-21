package it.fedet.minigames.impl.sumo.database;


import it.fedet.minigames.api.world.database.StorageType;
import it.fedet.minigames.api.world.database.WorldDbProvider;


public class WorldDatabase implements WorldDbProvider {
    @Override
    public String getConnectionOrHostString() {
        return "mongodb://localhost:27017";
    }

    @Override
    public String getDatabaseName() {
        return "minigames";
    }

    @Override
    public String getTableOrCollectionName() {
        return "worlds";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MONGODB;
    }
}
