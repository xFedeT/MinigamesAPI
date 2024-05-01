package it.fedet.minigames.api.function;

@FunctionalInterface
public interface ThrowableConsumer<T, E extends Exception> {

    void accept(T t) throws E;
}
