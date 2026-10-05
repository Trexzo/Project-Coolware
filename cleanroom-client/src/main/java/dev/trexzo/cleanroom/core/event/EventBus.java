package dev.trexzo.cleanroom.core.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventBus {
    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Consumer<?>>> listeners =
            new ConcurrentHashMap<>();

    public <T> Subscription subscribe(Class<T> eventType, Consumer<? super T> listener) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(listener, "listener");
        var bucket = listeners.computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList<>());
        bucket.add(listener);
        return () -> {
            bucket.remove(listener);
            if (bucket.isEmpty()) {
                listeners.remove(eventType, bucket);
            }
        };
    }

    public <T> T post(T event) {
        Objects.requireNonNull(event, "event");
        List<Consumer<?>> bucket = listeners.get(event.getClass());
        if (bucket == null) {
            return event;
        }
        for (Consumer<?> raw : bucket) {
            @SuppressWarnings("unchecked")
            Consumer<T> listener = (Consumer<T>) raw;
            listener.accept(event);
        }
        return event;
    }

    public int listenerCount(Class<?> eventType) {
        var bucket = listeners.get(eventType);
        return bucket == null ? 0 : bucket.size();
    }

    @FunctionalInterface
    public interface Subscription extends AutoCloseable {
        @Override
        void close();
    }
}
