package it.fedet.minigames.api.function;

@FunctionalInterface
public interface ThrowableBiConsumer<T, U, E extends Exception> {

    void accept(T t, U u) throws E;
}
