package it.fedet.minigames.world.nms.bridge;

import java.util.function.BooleanSupplier;

/**
 * This class serves as a bridge between the SWM and the Minecraft server.
 *
 * As plugins are loaded using a different ClassLoader, their code cannot
 * be accessed from a NMS method. Because of this, it's impossible to make
 * any calls to any method when rewriting the bytecode of a NMS class.
 *
 * As a workaround, this bridge simply calls a method of the {@link CraftCLSMBridge} interface,
 * which is implemented by the SWM plugin when loaded.
 */
public class ClassModifier {

    // Required for Paper 1.13 as javassist can't compile this class
    public static final BooleanSupplier BOOLEAN_SUPPLIER = () -> true;

    private static CraftCLSMBridge customLoader;

    public static boolean isCustomWorld(Object world) {
        return customLoader != null && customLoader.isCustomWorld(world);
    }

    public static boolean skipWorldAdd(Object world) {
        return customLoader != null && customLoader.skipWorldAdd(world);
    }

    public static void setLoader(CraftCLSMBridge loader) {
        customLoader = loader;
    }

    public static Object[] getDefaultWorlds() {
        return customLoader != null ? customLoader.getDefaultWorlds() : null;
    }
}
