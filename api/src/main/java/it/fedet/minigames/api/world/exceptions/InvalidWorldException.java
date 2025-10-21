//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.exceptions;

import java.io.File;

public class InvalidWorldException extends SlimeException {
    public InvalidWorldException(File worldDir) {
        super("Directory " + worldDir.getPath() + " does not contain a valid MC world!");
    }
}
