//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.swm.exceptions;

public class WorldTooBigException extends SlimeException {
    public WorldTooBigException(String worldName) {
        super("World " + worldName + " is too big to be converted into the SRF!");
    }
}
