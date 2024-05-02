package it.fedet.minigames.api.settings;

import java.util.Objects;
import java.util.stream.Stream;

public class GameSettingScroller<T> implements Runnable {

    protected final GameSetting<T> gameSetting;
    protected final T[] values;

    @SafeVarargs
    public GameSettingScroller(GameSetting<T> gameSetting, T... values) {
        this.gameSetting = gameSetting;
        if (values.length == 0) {
            this.values = (T[]) Stream.of(values).toArray();
        } else this.values = values;
    }


    public T scroll() {
        Object value = gameSetting.getValue();
        if (value == null) {
            return gameSetting.updateValue(values[0]);
        }

        int scroll = -1;
        for (int i = 0; i < values.length; i++) {
            if (Objects.equals(values[i], value)) {
                scroll = i;
            }
        }

        if (scroll == -1 || scroll + 1 >= values.length) {

            return gameSetting.updateValue(values[0]);
        }

        return gameSetting.updateValue(values[scroll + 1]);

    }

    @Override
    public void run() {
        scroll();
    }
}
