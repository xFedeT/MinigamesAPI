package it.fedet.minigames.api.services;


import it.fedet.minigames.api.world.database.StorageType;
import it.fedet.minigames.api.world.database.WorldDbProvider;
import it.fedet.minigames.api.world.exceptions.*;
import it.fedet.minigames.api.world.loaders.SlimeLoader;
import it.fedet.minigames.api.world.world.SlimeWorld;
import it.fedet.minigames.api.world.world.properties.WorldPropertyMap;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface IWorldService extends Service {

    /**
     * @deprecated
     */
    @Deprecated
    SlimeWorld loadWorld(SlimeLoader var1, String var2, SlimeWorld.SlimeProperties var3) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException;

    SlimeWorld loadWorld(SlimeLoader var1, String var2, boolean var3, WorldPropertyMap var4) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException;

    SlimeWorld loadWorld(SlimeLoader var1, String var2, boolean var3, WorldPropertyMap var4, boolean var5) throws UnknownWorldException, IOException, CorruptedWorldException, NewerFormatException, WorldInUseException;

    /**
     * @deprecated
     */
    @Deprecated
    SlimeWorld createEmptyWorld(SlimeLoader var1, String var2, SlimeWorld.SlimeProperties var3) throws WorldAlreadyExistsException, IOException;

    SlimeWorld createEmptyWorld(SlimeLoader var1, String var2, boolean var3, WorldPropertyMap var4) throws WorldAlreadyExistsException, IOException;

    CompletableFuture<World> generateWorld(SlimeWorld var1);

    void migrateWorld(String var1, SlimeLoader var2, SlimeLoader var3) throws IOException, WorldInUseException, WorldAlreadyExistsException, UnknownWorldException;

    SlimeLoader getLoader(StorageType var1);

    void registerLoader(StorageType var1, SlimeLoader var2);

    void importWorld(File var1, String var2, SlimeLoader var3) throws WorldAlreadyExistsException, InvalidWorldException, WorldLoadedException, WorldTooBigException, IOException;

    void setProvider(WorldDbProvider provider);
}
