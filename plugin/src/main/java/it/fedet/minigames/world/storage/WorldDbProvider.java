package it.fedet.minigames.world.storage;

import it.fedet.minigames.api.world.storage.IWorldDbProvider;
import it.fedet.minigames.api.world.storage.StorageType;

public class WorldDbProvider implements IWorldDbProvider {

    private String connectionOrHostString; // e.g., "mongodb://localhost:27017" or "localhost"
    private String databaseName; // e.g., "minigames"
    private String tableOrCollectionName; // e.g., "worlds"

    private String username; // for MariaDB
    private String password; // for MariaDB
    private int port; // for MariaDB

    private StorageType type;


    @Override
    public String getConnectionOrHostString() {
        return connectionOrHostString;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getTableOrCollectionName() {
        return tableOrCollectionName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public StorageType getType() {
        return type;
    }
}
