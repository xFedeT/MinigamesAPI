package it.fedet.minigames.api.game.listener;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public interface GameListener<T extends Event> {

    Class<T> getEventClass();

    default EventPriority getPriority() {
        return EventPriority.NORMAL;
    }

    /*
    Define if the handler ignores a cancelled event. If ignoreCancelled is true and the event is cancelled,
    the method is not called. Otherwise, the method is always called.
     */

    default boolean ignoreCancelled() {
        return true;
    }

    void apply(T event);

    default void onEvent(Event event) {
        if (getEventClass() != event.getClass()) return;
        if (event instanceof Cancellable cancellable)
            if (cancellable.isCancelled() && ignoreCancelled())
                return;

        apply((T) event);
    }


}
