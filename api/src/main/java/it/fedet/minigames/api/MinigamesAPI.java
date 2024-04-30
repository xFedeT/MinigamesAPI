package it.fedet.minigames.api;

import it.fedet.minigames.api.services.Service;

public interface MinigamesAPI {


    <T extends Service> T getService(Class<T> service);
}
