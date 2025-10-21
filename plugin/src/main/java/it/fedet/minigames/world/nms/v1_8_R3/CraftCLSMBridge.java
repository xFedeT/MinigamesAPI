//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.nms.v1_8_R3;

import it.fedet.minigames.classmodifier.CLSMBridge;
import it.fedet.minigames.classmodifier.ClassModifier;
import net.minecraft.server.v1_8_R3.WorldServer;

public class CraftCLSMBridge implements CLSMBridge {
    private final v1_8_R3SlimeNMS nmsInstance;

    public Object[] getDefaultWorlds() {
        WorldServer defaultWorld = this.nmsInstance.getDefaultWorld();
        WorldServer netherWorld = this.nmsInstance.getDefaultNetherWorld();
        WorldServer endWorld = this.nmsInstance.getDefaultEndWorld();
        return defaultWorld == null && netherWorld == null && endWorld == null ? null : new WorldServer[]{defaultWorld, netherWorld, endWorld};
    }

    public boolean isCustomWorld(Object world) {
        return world instanceof CustomWorldServer;
    }

    public boolean skipWorldAdd(Object world) {
        if (this.isCustomWorld(world) && !this.nmsInstance.isLoadingDefaultWorlds()) {
            CustomWorldServer worldServer = (CustomWorldServer) world;
            return !worldServer.isReady();
        } else {
            return false;
        }
    }

    static void initialize(v1_8_R3SlimeNMS instance) {
        ClassModifier.setLoader(new CraftCLSMBridge(instance));
    }

    public CraftCLSMBridge(v1_8_R3SlimeNMS nmsInstance) {
        this.nmsInstance = nmsInstance;
    }
}
