package dev.trexzo.cleanroom.core.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.trexzo.cleanroom.core.module.Module;
import dev.trexzo.cleanroom.core.module.ModuleRegistry;
import dev.trexzo.cleanroom.core.setting.Setting;
import dev.trexzo.cleanroom.core.setting.SettingRegistry;
import dev.trexzo.cleanroom.core.setting.ValueCodec;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProfileServiceTest {
    @Test
    void appliesRenamedSettingAliasAndIgnoresUnknownIds() {
        ModuleRegistry modules = new ModuleRegistry();
        TestModule esp = new TestModule("render.esp", false);
        modules.register(esp);

        Setting<Integer> range = new Setting<>("range", 3, value -> value >= 1 && value <= 8, ignored -> {});
        SettingRegistry settings = new SettingRegistry();
        settings.register("render.esp", range, ValueCodec.INTEGER, "distance");

        ProfileService service = new ProfileService(modules, settings);
        ProfileService.ApplyResult result = service.apply(ProfileSnapshot.current(
                Map.of("render.esp", true, "missing.module", true),
                Map.of("render.esp.distance", "6", "missing.setting", "ignored")));

        assertEquals(6, range.get());
        assertEquals(true, esp.enabled());
        assertEquals(1, result.settingsApplied());
        assertEquals(1, result.moduleStatesApplied());
        assertEquals(1, result.unknownSettings());
        assertEquals(1, result.unknownModules());
    }

    @Test
    void validatesEverySettingBeforeMutatingAnySetting() {
        ModuleRegistry modules = new ModuleRegistry();
        SettingRegistry settings = new SettingRegistry();

        Setting<Integer> alpha = new Setting<>("alpha", 10, value -> value >= 0 && value <= 100, ignored -> {});
        Setting<Integer> width = new Setting<>("width", 2, value -> value >= 1 && value <= 10, ignored -> {});
        settings.register("render.esp", alpha, ValueCodec.INTEGER);
        settings.register("render.esp", width, ValueCodec.INTEGER);

        ProfileService service = new ProfileService(modules, settings);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.apply(ProfileSnapshot.current(
                        Map.of(),
                        Map.of("render.esp.alpha", "80", "render.esp.width", "999"))));

        assertEquals(10, alpha.get());
        assertEquals(2, width.get());
    }

    @Test
    void rollsBackSettingsAndEarlierModulesWhenLaterModuleFails() {
        ModuleRegistry modules = new ModuleRegistry();
        TestModule good = new TestModule("a.good", false);
        TestModule bad = new TestModule("b.bad", true);
        modules.register(good);
        modules.register(bad);

        Setting<Integer> range = Setting.of("range", 3);
        SettingRegistry settings = new SettingRegistry();
        settings.register("combat.aura", range, ValueCodec.INTEGER);

        ProfileService service = new ProfileService(modules, settings);

        assertThrows(
                IllegalStateException.class,
                () -> service.apply(ProfileSnapshot.current(
                        Map.of("a.good", true, "b.bad", true),
                        Map.of("combat.aura.range", "5"))));

        assertFalse(good.enabled());
        assertFalse(bad.enabled());
        assertEquals(3, range.get());
    }

    private static final class TestModule extends Module {
        private final boolean failOnEnable;

        private TestModule(String id, boolean failOnEnable) {
            super(id, id, Category.MISC);
            this.failOnEnable = failOnEnable;
        }

        @Override
        protected void onEnable() {
            if (failOnEnable) {
                throw new IllegalStateException("expected test failure");
            }
        }
    }
}
