package dev.trexzo.cleanroom.core.module;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ModuleRegistryTest {
    @Test
    void duplicateIdsAreRejectedAndLifecycleIsIdempotent() {
        AtomicInteger enables = new AtomicInteger();
        AtomicInteger disables = new AtomicInteger();

        Module module = new Module("render.example", "Example", Module.Category.RENDER) {
            @Override
            protected void onEnable() {
                enables.incrementAndGet();
            }

            @Override
            protected void onDisable() {
                disables.incrementAndGet();
            }
        };

        ModuleRegistry registry = new ModuleRegistry();
        registry.register(module);

        module.setEnabled(true);
        module.setEnabled(true);
        module.setEnabled(false);
        module.setEnabled(false);

        assertEquals(1, enables.get());
        assertEquals(1, disables.get());
        assertEquals(1, registry.size());
        assertTrue(registry.find("render.example").isPresent());

        assertThrows(IllegalStateException.class, () ->
                registry.register(new Module("render.example", "Other", Module.Category.RENDER) {}));
    }
}
