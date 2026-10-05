package dev.trexzo.cleanroom.core.setting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SettingRegistryTest {
    @Test
    void resolvesRenameAliasToCanonicalSetting() {
        Setting<Integer> range = new Setting<>("range", 3, value -> value >= 1 && value <= 6, ignored -> {});
        SettingRegistry registry = new SettingRegistry();

        SettingRegistry.RegisteredSetting<Integer> registered =
                registry.register("combat.aura", range, ValueCodec.INTEGER, "distance");

        assertEquals("combat.aura.range", registered.canonicalId());
        assertSame(registered, registry.find("combat.aura.range").orElseThrow());
        assertSame(registered, registry.find("combat.aura.distance").orElseThrow());

        registered.applyEncoded("5");
        assertEquals(5, range.get());
    }

    @Test
    void invalidDecodedValueDoesNotMutateSetting() {
        Setting<Integer> range = new Setting<>("range", 3, value -> value >= 1 && value <= 6, ignored -> {});
        SettingRegistry.RegisteredSetting<Integer> registered =
                new SettingRegistry().register("combat.aura", range, ValueCodec.INTEGER);

        assertThrows(IllegalArgumentException.class, () -> registered.applyEncoded("99"));
        assertEquals(3, range.get());
    }

    @Test
    void rejectsAliasCollision() {
        SettingRegistry registry = new SettingRegistry();
        registry.register("render.esp", Setting.of("mode", "box"), ValueCodec.STRING, "style");

        assertThrows(
                IllegalStateException.class,
                () -> registry.register("render.esp", Setting.of("style", "outline"), ValueCodec.STRING));
    }

    @Test
    void enumCodecRoundTripsByStableEnumName() {
        ValueCodec<Mode> codec = ValueCodec.enumCodec(Mode.class);

        assertEquals("OUTLINE", codec.encode(Mode.OUTLINE));
        assertEquals(Mode.OUTLINE, codec.decode("OUTLINE"));
        assertThrows(IllegalArgumentException.class, () -> codec.decode("outline"));
    }

    private enum Mode {
        BOX,
        OUTLINE
    }
}
