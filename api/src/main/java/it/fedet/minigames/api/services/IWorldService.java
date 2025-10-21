package it.fedet.minigames.api.services;


import it.fedet.minigames.api.swm.database.WorldDbProvider;

public interface IWorldService extends Service {
    void setProvider(WorldDbProvider provider);
}
