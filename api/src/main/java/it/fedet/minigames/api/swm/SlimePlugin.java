//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.swm;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import it.fedet.minigames.api.swm.database.StorageType;
import it.fedet.minigames.api.swm.exceptions.*;
import it.fedet.minigames.api.swm.loaders.SlimeLoader;
import it.fedet.minigames.api.swm.world.SlimeWorld;
import it.fedet.minigames.api.swm.world.properties.SlimePropertyMap;
import org.bukkit.World;

public interface SlimePlugin {
    /** @deprecated */
    @Deprecated
    SlimeWorld loadWorld(SlimeLoader var1, String var2, SlimeWorld.SlimeProperties var3) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException;

    SlimeWorld loadWorld(SlimeLoader var1, String var2, boolean var3, SlimePropertyMap var4) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException;

    SlimeWorld loadWorld(SlimeLoader var1, String var2, boolean var3, SlimePropertyMap var4, boolean var5) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException;

    /** @deprecated */
    @Deprecated
    SlimeWorld createEmptyWorld(SlimeLoader var1, String var2, SlimeWorld.SlimeProperties var3) throws WorldAlreadyExistsException, IOException;

    SlimeWorld createEmptyWorld(SlimeLoader var1, String var2, boolean var3, SlimePropertyMap var4) throws WorldAlreadyExistsException, IOException;

    CompletableFuture<World> generateWorld(SlimeWorld var1);

    void migrateWorld(String var1, SlimeLoader var2, SlimeLoader var3) throws IOException, WorldInUseException, WorldAlreadyExistsException, UnknownWorldException;

    SlimeLoader getLoader(StorageType var1);

    void registerLoader(StorageType var1, SlimeLoader var2);

    void importWorld(File var1, String var2, SlimeLoader var3) throws WorldAlreadyExistsException, InvalidWorldException, WorldLoadedException, WorldTooBigException, IOException;
}
