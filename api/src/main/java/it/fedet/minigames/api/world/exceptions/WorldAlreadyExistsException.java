//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.exceptions;

public class WorldAlreadyExistsException extends SlimeException {
    public WorldAlreadyExistsException(String world) {
        super("World " + world + " already exists!");
    }
}
