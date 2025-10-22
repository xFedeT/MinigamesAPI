package it.fedet.minigames.impl.sumo.player;

import it.fedet.minigames.api.loadit.UserData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SumoPlayer extends UserData {

    public SumoPlayer(@NotNull UUID uuid, @NotNull String name) {
        super(uuid, name);
    }
}
