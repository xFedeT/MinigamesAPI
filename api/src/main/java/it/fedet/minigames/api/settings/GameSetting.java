package it.fedet.minigames.api.settings;

public class GameSetting<T> implements Cloneable {

    private final int id;
    private final T defaultValue;
    private final GameSettingScroller<T> scroller;

    private T value;

    @SafeVarargs
    public GameSetting(int id, T defaultValue, T... scrollValues) {
        this.id = id;
        this.value = this.defaultValue = defaultValue;
        this.scroller = new GameSettingScroller<>(this, scrollValues);
    }

    public int getId() {
        return id;
    }

    public GameSettingScroller<T> getScroller() {
        return scroller;
    }

    public T getValue() {
        return value;
    }


    public T updateValue(T value) {
        return this.value = value;
    }

    public void reset() {
        this.value = defaultValue;
    }

    public GameSetting<T> clone() {
        return new GameSetting<>(id, defaultValue, scroller.values);
    }
}
