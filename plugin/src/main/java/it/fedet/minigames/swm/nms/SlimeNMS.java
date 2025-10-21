//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.nms;

import com.flowpowered.nbt.CompoundTag;
import it.fedet.minigames.api.swm.world.SlimeWorld;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public interface SlimeNMS {
    void setDefaultWorlds(SlimeWorld var1, SlimeWorld var2, SlimeWorld var3);

    CompletableFuture<World> generateWorld(SlimeWorld var1);

    default Object createNMSWorld(SlimeWorld world) {
        throw new UnsupportedOperationException("This spigot version does not support async world loading");
    }

    default CompletableFuture<World> addWorldToServerList(Object worldObject) {
        throw new UnsupportedOperationException("This spigot version does not support async world loading");
    }

    SlimeWorld getSlimeWorld(World var1);

    byte getWorldVersion();

    default CompoundTag convertChunk(CompoundTag chunkTag) {
        return chunkTag;
    }
}
