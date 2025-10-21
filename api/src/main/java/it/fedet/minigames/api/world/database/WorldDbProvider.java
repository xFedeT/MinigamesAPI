package it.fedet.minigames.api.world.database;

public interface WorldDbProvider {

    String getConnectionOrHostString();

    String getDatabaseName();

    String getTableOrCollectionName();

    String getUsername();

    String getPassword();

    int getPort();

    StorageType getStorageType();

}