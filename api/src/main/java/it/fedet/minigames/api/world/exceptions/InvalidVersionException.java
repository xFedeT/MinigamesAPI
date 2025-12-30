//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.exceptions;

public class InvalidVersionException extends SlimeException {
    public InvalidVersionException(String version) {
        super("SlimeWorldManager does not support Spigot " + version + "!");
    }
}
