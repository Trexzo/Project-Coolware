package dev.trexzo.cleanroom.core.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class SettingRegistry {
    private final Map<String, RegisteredSetting<?>> settings = new LinkedHashMap<>();
    private final Map<String, String> aliases = new LinkedHashMap<>();

    public synchronized <T> RegisteredSetting<T> register(
            String namespace,
            Setting<T> setting,
            ValueCodec<T> codec,
            String... previousIds) {
        String safeNamespace = requireId(namespace, "namespace");
        Objects.requireNonNull(setting, "setting");
        Objects.requireNonNull(codec, "codec");

        String canonicalId = qualify(safeNamespace, setting.id());
        if (settings.containsKey(canonicalId) || aliases.containsKey(canonicalId)) {
            throw new IllegalStateException("Duplicate setting id: " + canonicalId);
        }

        List<String> qualifiedAliases = Arrays.stream(previousIds == null ? new String[0] : previousIds)
                .map(id -> qualify(safeNamespace, requireId(id, "alias")))
                .distinct()
                .toList();

        for (String alias : qualifiedAliases) {
            if (alias.equals(canonicalId)) {
                continue;
            }
            if (settings.containsKey(alias) || aliases.containsKey(alias)) {
                throw new IllegalStateException("Duplicate setting alias: " + alias);
            }
        }

        RegisteredSetting<T> registered =
                new RegisteredSetting<>(canonicalId, setting, codec, qualifiedAliases);
        settings.put(canonicalId, registered);
        for (String alias : qualifiedAliases) {
            if (!alias.equals(canonicalId)) {
                aliases.put(alias, canonicalId);
            }
        }
        return registered;
    }

    public synchronized Optional<RegisteredSetting<?>> find(String qualifiedId) {
        Objects.requireNonNull(qualifiedId, "qualifiedId");
        RegisteredSetting<?> direct = settings.get(qualifiedId);
        if (direct != null) {
            return Optional.of(direct);
        }
        String canonicalId = aliases.get(qualifiedId);
        return canonicalId == null ? Optional.empty() : Optional.ofNullable(settings.get(canonicalId));
    }

    public synchronized Collection<RegisteredSetting<?>> snapshot() {
        List<RegisteredSetting<?>> copy = new ArrayList<>(settings.values());
        copy.sort(Comparator.comparing(RegisteredSetting::canonicalId));
        return List.copyOf(copy);
    }

    public synchronized int size() {
        return settings.size();
    }

    private static String qualify(String namespace, String settingId) {
        return namespace + "." + settingId;
    }

    private static String requireId(String id, String label) {
        Objects.requireNonNull(id, label);
        if (!id.matches("[a-z0-9][a-z0-9._-]*")) {
            throw new IllegalArgumentException("Invalid " + label + ": " + id);
        }
        return id;
    }

    public static final class RegisteredSetting<T> {
        private final String canonicalId;
        private final Setting<T> setting;
        private final ValueCodec<T> codec;
        private final List<String> aliases;

        private RegisteredSetting(
                String canonicalId,
                Setting<T> setting,
                ValueCodec<T> codec,
                List<String> aliases) {
            this.canonicalId = canonicalId;
            this.setting = setting;
            this.codec = codec;
            this.aliases = List.copyOf(aliases);
        }

        public String canonicalId() {
            return canonicalId;
        }

        public Setting<T> setting() {
            return setting;
        }

        public List<String> aliases() {
            return aliases;
        }

        public String encodeCurrent() {
            return codec.encode(setting.get());
        }

        public T decode(String encoded) {
            T decoded = codec.decode(Objects.requireNonNull(encoded, "encoded"));
            setting.validate(decoded);
            return decoded;
        }

        public void applyEncoded(String encoded) {
            setting.set(decode(encoded));
        }
    }
}
