package it.fedet.minigames.api.provider;

import it.fedet.minigames.api.MinigamesAPI;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MinigamesProvider {

    private static MinigamesAPI instance = null;

    @ApiStatus.Internal
    private MinigamesProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static @NotNull MinigamesAPI get() {
        return MinigamesProvider.instance;
    }

    @ApiStatus.Internal
    public static void register(MinigamesAPI instance) {
        MinigamesProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        instance = null;
    }

}
