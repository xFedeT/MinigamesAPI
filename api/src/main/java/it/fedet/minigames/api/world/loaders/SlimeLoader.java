//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.loaders;

import it.fedet.minigames.api.world.exceptions.UnknownWorldException;
import it.fedet.minigames.api.world.exceptions.WorldInUseException;

import java.io.IOException;
import java.util.List;

public interface SlimeLoader {
    byte[] loadWorld(String var1, boolean var2) throws UnknownWorldException, WorldInUseException, IOException;

    byte[] loadWorld(String var1, boolean var2, boolean var3) throws UnknownWorldException, WorldInUseException, IOException;

    boolean worldExists(String var1) throws IOException;

    List<String> listWorlds() throws IOException;

    void saveWorld(String var1, byte[] var2, boolean var3) throws IOException;

    void unlockWorld(String var1) throws UnknownWorldException, IOException;

    boolean isWorldLocked(String var1) throws UnknownWorldException, IOException;

    void deleteWorld(String var1) throws UnknownWorldException, IOException;
}
