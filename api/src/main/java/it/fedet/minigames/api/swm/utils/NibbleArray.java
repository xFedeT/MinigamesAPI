//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.swm.utils;

public class NibbleArray {
    private final int size;
    private final byte[] backing;

    public NibbleArray(int size) {
        this(new byte[size / 2]);
    }

    public NibbleArray(byte[] backing) {
        this.backing = backing;
        this.size = backing.length * 2;
    }

    public int get(int index) {
        int value = this.backing[index / 2];
        return index % 2 == 0 ? value & 15 : (value & 240) >> 4;
    }

    public void set(int index, int value) {
        int nibble = value & 15;
        int halfIndex = index / 2;
        int previous = this.backing[halfIndex];
        if (index % 2 == 0) {
            this.backing[halfIndex] = (byte)(previous & 240 | nibble);
        } else {
            this.backing[halfIndex] = (byte)(previous & 15 | nibble << 4);
        }

    }

    public byte[] getBacking() {
        return this.backing;
    }
}
