package it.fedet.minigames.world.nms.bridge;

import it.fedet.minigames.world.nms.v1_8_R3SlimeNMS;
import it.fedet.minigames.world.nms.world.CustomWorldServer;
import net.minecraft.server.v1_8_R3.WorldServer;

public class CraftCLSMBridge {

    private final v1_8_R3SlimeNMS nmsInstance;

    public CraftCLSMBridge(v1_8_R3SlimeNMS nmsInstance) {
        this.nmsInstance = nmsInstance;
    }

    public Object[] getDefaultWorlds() {
        WorldServer defaultWorld = nmsInstance.getDefaultWorld();
        WorldServer netherWorld = nmsInstance.getDefaultNetherWorld();
        WorldServer endWorld = nmsInstance.getDefaultEndWorld();

        if (defaultWorld != null || netherWorld != null || endWorld != null) {
            return new WorldServer[] { defaultWorld, netherWorld, endWorld };
        }

        // Returning null will just run the original load world method
        return null;
    }

    public boolean isCustomWorld(Object world) {
        return world instanceof CustomWorldServer;
    }

    public boolean skipWorldAdd(Object world) {
        if (!isCustomWorld(world) || nmsInstance.isLoadingDefaultWorlds()) {
            return false;
        }

        CustomWorldServer worldServer = (CustomWorldServer) world;
        return !worldServer.isReady();
    }

    public static void initialize(v1_8_R3SlimeNMS instance) {
        ClassModifier.setLoader(new CraftCLSMBridge(instance));
    }

    public v1_8_R3SlimeNMS getNmsInstance() {
        return nmsInstance;
    }
}
