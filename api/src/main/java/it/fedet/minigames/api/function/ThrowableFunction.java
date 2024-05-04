package it.fedet.minigames.api.function;

@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {
    R apply(T t) throws E;
}
