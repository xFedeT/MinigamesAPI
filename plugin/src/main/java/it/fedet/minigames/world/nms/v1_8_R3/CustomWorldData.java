//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.nms.v1_8_R3;

import com.flowpowered.nbt.CompoundTag;
import it.fedet.minigames.api.world.world.properties.WorldProperties;
import it.fedet.minigames.world.nms.CraftSlimeWorld;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.WorldData;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import net.minecraft.server.v1_8_R3.WorldType;

import java.util.Optional;

public class CustomWorldData extends WorldData {
    private final CraftSlimeWorld world;
    private final WorldType type;

    CustomWorldData(CraftSlimeWorld world) {
        this.world = world;
        this.type = WorldType.getType(world.getPropertyMap().getString(WorldProperties.WORLD_TYPE).toUpperCase());
        this.setGameType(EnumGamemode.SURVIVAL);
        CompoundTag extraData = world.getExtraData();
        Optional<CompoundTag> gameRules = extraData.getAsCompoundTag("gamerules");
        gameRules.ifPresent((compoundTag) -> this.x().a((NBTTagCompound) Converter.convertTag(compoundTag)));
    }

    public String getName() {
        return this.world.getName();
    }

    public CraftSlimeWorld getWorld() {
        return this.world;
    }

    public WorldType getType() {
        return this.type;
    }
}
