package it.fedet.minigames.api.services;


import it.fedet.minigames.api.world.providers.WorldDbProvider;

public interface IWorldService {
    void setProvider(WorldDbProvider provider);
}
