//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.nms.v1_8_R3;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import it.fedet.minigames.api.swm.world.SlimeWorld;
import it.fedet.minigames.swm.nms.CraftSlimeWorld;
import net.minecraft.server.v1_8_R3.*;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomDataManager extends WorldNBTStorage {
    private static final Map<String, String> defaultValues;
    private final UUID uuid = UUID.randomUUID();
    private final SlimeWorld world;
    private final CustomChunkLoader chunkLoader;
    private WorldData worldData;

    CustomDataManager(SlimeWorld world) {
        super(new File("temp_" + world.getName()), world.getName(), false);
        File baseDir = new File("temp_" + world.getName(), world.getName());
        (new File(baseDir, "session.lock")).delete();
        (new File(baseDir, "data")).delete();
        baseDir.delete();
        baseDir.getParentFile().delete();
        this.world = world;
        this.chunkLoader = new CustomChunkLoader((CraftSlimeWorld) world);
    }

    public WorldData getWorldData() {
        if (this.worldData == null) {
            this.worldData = new CustomWorldData((CraftSlimeWorld) this.world);
        }

        return this.worldData;
    }

    public void checkSession() {
    }

    public IChunkLoader createChunkLoader(WorldProvider worldProvider) {
        return this.chunkLoader;
    }

    public void saveWorldData(WorldData worldData, NBTTagCompound nbtTagCompound) {
        CompoundTag gameRules = (CompoundTag) Converter.convertTag("gamerules", worldData.x().a()).getAsCompoundTag().get();
        CompoundTag extraData = this.world.getExtraData();
        extraData.getValue().remove("gamerules");
        if (!gameRules.getValue().isEmpty()) {
            for (Map.Entry<String, Tag<?>> entry : new ArrayList<>(gameRules.getValue().entrySet())) {
                String rule = entry.getKey();
                StringTag valueTag = (StringTag) entry.getValue();
                String defaultValue = defaultValues.get(rule);
                if (valueTag.getValue().equalsIgnoreCase(defaultValue)) {
                    gameRules.getValue().remove(rule);
                }
            }

            if (!gameRules.getValue().isEmpty()) {
                extraData.getValue().put("gamerules", gameRules);
            }
        }

    }

    public void saveWorldData(WorldData worldData) {
        this.saveWorldData(worldData, null);
    }

    public void a() {
    }

    public File getDataFile(String s) {
        return null;
    }

    public String g() {
        return null;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void save(EntityHuman entityHuman) {
    }

    public NBTTagCompound load(EntityHuman entityHuman) {
        return null;
    }

    public String[] getSeenPlayers() {
        return new String[0];
    }

    public SlimeWorld getWorld() {
        return this.world;
    }

    public CustomChunkLoader getChunkLoader() {
        return this.chunkLoader;
    }

    static {
        GameRules emptyRules = new GameRules();
        String[] rules = emptyRules.getGameRules();
        Stream var10000 = Arrays.stream(rules);
        Function var10001 = (rule) -> rule;
        Objects.requireNonNull(emptyRules);
        defaultValues = (Map) var10000.collect(Collectors.toMap(var10001, emptyRules::get));
    }
}
