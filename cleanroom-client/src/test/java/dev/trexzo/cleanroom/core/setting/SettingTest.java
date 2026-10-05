package dev.trexzo.cleanroom.core.setting;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SettingTest {
    @Test
    void validatesChangesAndNotifiesOnlyOnRealChange() {
        AtomicInteger changes = new AtomicInteger();
        Setting<Integer> setting = new Setting<>(
                "render.radius",
                8,
                value -> value >= 1 && value <= 64,
                ignored -> changes.incrementAndGet()
        );

        setting.set(16);
        setting.set(16);

        assertEquals(16, setting.get());
        assertEquals(1, changes.get());
        assertThrows(IllegalArgumentException.class, () -> setting.set(0));

        setting.reset();
        assertEquals(8, setting.get());
        assertEquals(2, changes.get());
    }
}
