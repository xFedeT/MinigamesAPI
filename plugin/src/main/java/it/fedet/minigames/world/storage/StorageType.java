package it.fedet.minigames.world.storage;

import it.fedet.minigames.api.world.storage.IStorageType;
import it.fedet.minigames.api.world.storage.UpdatableLoader;

public enum StorageType implements IStorageType {

    MYSQL("mysql", MysqlLoader.class),
    MONGODB("mongodb", MongoLoader.class);

    private final String name;
    private final Class<? extends UpdatableLoader>  loaderClass;

    StorageType(String name, Class<? extends UpdatableLoader> loaderClass) {
        this.name = name;
        this.loaderClass = loaderClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<? extends UpdatableLoader> getLoaderClass() {
        return loaderClass;
    }
}
