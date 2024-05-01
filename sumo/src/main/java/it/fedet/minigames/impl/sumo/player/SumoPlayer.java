package it.fedet.minigames.impl.sumo.player;

import it.ytnoos.loadit.api.UserData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SumoPlayer extends UserData {


    protected SumoPlayer(@NotNull UUID uuid, @NotNull String name) {
        super(uuid, name);
    }
}
