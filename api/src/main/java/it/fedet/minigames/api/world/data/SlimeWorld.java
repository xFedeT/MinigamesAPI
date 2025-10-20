package it.fedet.minigames.api.world.data;

import it.fedet.minigames.api.world.SlimeLoader;
import it.fedet.minigames.api.world.exception.WorldAlreadyExistsException;

import java.io.IOException;

public interface SlimeWorld {
    String getName();

    SlimeWorld clone(String worldName);

    SlimeWorld clone(String worldName, SlimeLoader loader) throws WorldAlreadyExistsException, IOException;

    SlimeWorld clone(String worldName, SlimeLoader loader, boolean lock) throws WorldAlreadyExistsException, IOException;
}
