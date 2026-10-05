package dev.trexzo.cleanroom.core.event;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class EventBusTest {
    record Tick(int index) {}

    @Test
    void subscriptionIsTypedAndCloseable() {
        EventBus bus = new EventBus();
        AtomicInteger total = new AtomicInteger();

        EventBus.Subscription subscription = bus.subscribe(Tick.class, tick -> total.addAndGet(tick.index()));

        bus.post(new Tick(2));
        bus.post(new Tick(3));
        assertEquals(5, total.get());
        assertEquals(1, bus.listenerCount(Tick.class));

        subscription.close();
        bus.post(new Tick(9));

        assertEquals(5, total.get());
        assertEquals(0, bus.listenerCount(Tick.class));
    }
}
