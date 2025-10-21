//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.swm.exceptions;

public class WorldLoadedException extends SlimeException {
    public WorldLoadedException(String worldName) {
        super("World " + worldName + " is loaded! Unload it before importing it.");
    }
}
