package it.fedet.minigames.world.nms.world;

import com.flowpowered.nbt.CompoundTag;
import it.fedet.minigames.world.map.SlimeProperties;
import it.fedet.minigames.world.nms.converter.Converter;
import net.minecraft.server.v1_8_R3.*;

import java.util.Optional;

public class CustomWorldData extends WorldData {

    private final CraftSlimeWorld world;
    private final WorldType type;

    public CustomWorldData(CraftSlimeWorld world) {
        this.world = world;
        this.type = WorldType.getType(world.getPropertyMap().getString(SlimeProperties.WORLD_TYPE).toUpperCase());
        this.setGameType(WorldSettings.EnumGamemode.NOT_SET);

        CompoundTag extraData = world.getExtraData();
        Optional<CompoundTag> gameRules = extraData.getAsCompoundTag("gamerules");
        gameRules.ifPresent(compoundTag -> this.x().a((NBTTagCompound) Converter.convertTag(compoundTag)));
    }

    @Override
    public String getName() {
        return world.getName();
    }

    public CraftSlimeWorld getWorld() {
        return world;
    }

    @Override
    public WorldType getType() {
        return type;
    }
}
