package dev.trexzo.cleanroom.core.profile;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public record ProfileSnapshot(
        int schemaVersion,
        Map<String, Boolean> moduleStates,
        Map<String, String> settingValues) {

    public static final int CURRENT_SCHEMA_VERSION = 1;

    public ProfileSnapshot {
        if (schemaVersion <= 0) {
            throw new IllegalArgumentException("schemaVersion must be positive");
        }
        moduleStates = immutableSortedCopy(moduleStates, "moduleStates");
        settingValues = immutableSortedCopy(settingValues, "settingValues");
    }

    public static ProfileSnapshot current(
            Map<String, Boolean> moduleStates,
            Map<String, String> settingValues) {
        return new ProfileSnapshot(CURRENT_SCHEMA_VERSION, moduleStates, settingValues);
    }

    private static <V> Map<String, V> immutableSortedCopy(Map<String, V> source, String label) {
        Objects.requireNonNull(source, label);
        TreeMap<String, V> sorted = new TreeMap<>();
        source.forEach((key, value) -> {
            requireId(key, label + " key");
            sorted.put(key, Objects.requireNonNull(value, label + " value for " + key));
        });
        return Collections.unmodifiableMap(sorted);
    }

    private static void requireId(String id, String label) {
        Objects.requireNonNull(id, label);
        if (!id.matches("[a-z0-9][a-z0-9._-]*")) {
            throw new IllegalArgumentException("Invalid " + label + ": " + id);
        }
    }
}
