//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.exceptions;

public class CorruptedWorldException extends SlimeException {
    public CorruptedWorldException(String world) {
        this(world, (Exception) null);
    }

    public CorruptedWorldException(String world, Exception ex) {
        super("World " + world + " seems to be corrupted", ex);
    }
}
