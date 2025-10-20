package it.fedet.minigames.api.world.storage;

public interface IStorageType {
    String getName();

    Class<? extends UpdatableLoader> getLoaderClass();
}
