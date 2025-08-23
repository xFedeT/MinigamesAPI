package it.fedet.minigames.api.provider;

import it.fedet.minigames.api.MinigamesAPI;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MinigamesProvider {

    private static MinigamesAPI INSTANCE = null;

    @ApiStatus.Internal
    private MinigamesProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static @NotNull MinigamesAPI get() {
        return MinigamesProvider.INSTANCE;
    }

    @ApiStatus.Internal
    public static void register(MinigamesAPI instance) {
        MinigamesProvider.INSTANCE = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        INSTANCE = null;
    }

}
