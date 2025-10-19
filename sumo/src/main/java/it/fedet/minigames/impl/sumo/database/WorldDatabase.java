package it.fedet.minigames.impl.sumo.database;

import it.fedet.minigames.api.world.storage.IWorldDbProvider;
import it.fedet.minigames.api.world.storage.StorageType;

public class WorldDatabase implements IWorldDbProvider {
    @Override
    public String getConnectionOrHostString() {
        return "";
    }

    @Override
    public String getDatabaseName() {
        return "";
    }

    @Override
    public String getTableOrCollectionName() {
        return "";
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
    public StorageType getType() {
        return StorageType.MONGODB;
    }
}
