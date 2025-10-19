package it.fedet.minigames.api.world.storage;

public interface IWorldDbProvider {
    String getConnectionOrHostString();

    String getDatabaseName();

    String getTableOrCollectionName();

    String getUsername();

    String getPassword();

    int getPort();

    StorageType getType();
}
