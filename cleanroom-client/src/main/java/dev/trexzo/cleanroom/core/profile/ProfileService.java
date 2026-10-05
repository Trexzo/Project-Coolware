package dev.trexzo.cleanroom.core.profile;

import dev.trexzo.cleanroom.core.module.Module;
import dev.trexzo.cleanroom.core.module.ModuleRegistry;
import dev.trexzo.cleanroom.core.setting.SettingRegistry;
import dev.trexzo.cleanroom.core.setting.SettingRegistry.RegisteredSetting;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ProfileService {
    private final ModuleRegistry modules;
    private final SettingRegistry settings;

    public ProfileService(ModuleRegistry modules, SettingRegistry settings) {
        this.modules = Objects.requireNonNull(modules, "modules");
        this.settings = Objects.requireNonNull(settings, "settings");
    }

    public ProfileSnapshot capture() {
        Map<String, Boolean> moduleStates = new LinkedHashMap<>();
        for (Module module : modules.snapshot()) {
            moduleStates.put(module.id(), module.enabled());
        }

        Map<String, String> settingValues = new LinkedHashMap<>();
        for (RegisteredSetting<?> setting : settings.snapshot()) {
            settingValues.put(setting.canonicalId(), setting.encodeCurrent());
        }

        return ProfileSnapshot.current(moduleStates, settingValues);
    }

    public ApplyResult apply(ProfileSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        if (snapshot.schemaVersion() != ProfileSnapshot.CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException(
                    "Unsupported profile schema version: " + snapshot.schemaVersion());
        }

        Map<RegisteredSetting<?>, String> requestedSettings = new LinkedHashMap<>();
        int unknownSettings = 0;
        for (Map.Entry<String, String> entry : snapshot.settingValues().entrySet()) {
            RegisteredSetting<?> registered = settings.find(entry.getKey()).orElse(null);
            if (registered == null) {
                unknownSettings++;
                continue;
            }
            if (requestedSettings.putIfAbsent(registered, entry.getValue()) != null) {
                throw new IllegalArgumentException(
                        "Profile contains multiple keys for setting " + registered.canonicalId());
            }
            registered.decode(entry.getValue());
        }

        Map<Module, Boolean> requestedModules = new LinkedHashMap<>();
        int unknownModules = 0;
        for (Map.Entry<String, Boolean> entry : snapshot.moduleStates().entrySet()) {
            Module module = modules.find(entry.getKey()).orElse(null);
            if (module == null) {
                unknownModules++;
                continue;
            }
            requestedModules.put(module, entry.getValue());
        }

        Map<RegisteredSetting<?>, String> oldSettings = new LinkedHashMap<>();
        requestedSettings.keySet().forEach(setting -> oldSettings.put(setting, setting.encodeCurrent()));

        Map<Module, Boolean> oldModules = new LinkedHashMap<>();
        requestedModules.keySet().forEach(module -> oldModules.put(module, module.enabled()));

        try {
            requestedSettings.forEach(RegisteredSetting::applyEncoded);
            requestedModules.forEach(Module::setEnabled);
            return new ApplyResult(
                    requestedSettings.size(),
                    requestedModules.size(),
                    unknownSettings,
                    unknownModules);
        } catch (RuntimeException | Error failure) {
            rollback(oldModules, oldSettings, failure);
            throw failure;
        }
    }

    private static void rollback(
            Map<Module, Boolean> oldModules,
            Map<RegisteredSetting<?>, String> oldSettings,
            Throwable originalFailure) {
        for (Map.Entry<Module, Boolean> entry : oldModules.entrySet()) {
            try {
                entry.getKey().setEnabled(entry.getValue());
            } catch (RuntimeException | Error rollbackFailure) {
                if (rollbackFailure != originalFailure) {
                    originalFailure.addSuppressed(rollbackFailure);
                }
            }
        }
        for (Map.Entry<RegisteredSetting<?>, String> entry : oldSettings.entrySet()) {
            try {
                entry.getKey().applyEncoded(entry.getValue());
            } catch (RuntimeException | Error rollbackFailure) {
                if (rollbackFailure != originalFailure) {
                    originalFailure.addSuppressed(rollbackFailure);
                }
            }
        }
    }

    public record ApplyResult(
            int settingsApplied,
            int moduleStatesApplied,
            int unknownSettings,
            int unknownModules) {}
}
