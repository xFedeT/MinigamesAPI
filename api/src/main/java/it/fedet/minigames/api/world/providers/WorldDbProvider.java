package it.fedet.minigames.api.world.providers;


import it.fedet.minigames.api.world.storage.IStorageType;

public interface WorldDbProvider {

    String getConnectionOrHostString();

    String getDatabaseName();

    String getTableOrCollectionName();

    String getUsername();

    String getPassword();

    int getPort();

    IStorageType getType();
}