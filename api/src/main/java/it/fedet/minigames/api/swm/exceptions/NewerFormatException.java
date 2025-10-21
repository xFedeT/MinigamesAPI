//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.swm.exceptions;

public class NewerFormatException extends SlimeException {
    public NewerFormatException(byte version) {
        super("v" + version);
    }
}
