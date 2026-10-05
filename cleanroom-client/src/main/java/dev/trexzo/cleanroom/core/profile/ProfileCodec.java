package dev.trexzo.cleanroom.core.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ProfileCodec {
    public static final int MAX_PROFILE_BYTES = 1024 * 1024;
    public static final int MAX_ENTRIES = 10_000;
    private static final String HEADER = "cleanroom-profile";

    public byte[] encode(ProfileSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        StringBuilder out = new StringBuilder(256);
        out.append(HEADER).append('\t').append(snapshot.schemaVersion()).append('\n');

        snapshot.moduleStates().forEach((id, enabled) ->
                out.append("m\t").append(id).append('\t').append(enabled ? '1' : '0').append('\n'));

        Base64.Encoder base64 = Base64.getUrlEncoder().withoutPadding();
        snapshot.settingValues().forEach((id, value) -> {
            String encoded = base64.encodeToString(value.getBytes(StandardCharsets.UTF_8));
            out.append("s\t").append(id).append("\t~").append(encoded).append('\n');
        });

        byte[] bytes = out.toString().getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_PROFILE_BYTES) {
            throw new IllegalArgumentException("Encoded profile exceeds " + MAX_PROFILE_BYTES + " bytes");
        }
        return bytes;
    }

    public ProfileSnapshot decode(byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");
        if (bytes.length > MAX_PROFILE_BYTES) {
            throw new IllegalArgumentException("Profile exceeds " + MAX_PROFILE_BYTES + " bytes");
        }

        String text = new String(bytes, StandardCharsets.UTF_8);
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            String header = reader.readLine();
            if (header == null) {
                throw new IllegalArgumentException("Missing profile header");
            }

            String[] headerParts = header.split("\\t", -1);
            if (headerParts.length != 2 || !HEADER.equals(headerParts[0])) {
                throw new IllegalArgumentException("Invalid profile header");
            }

            int schemaVersion;
            try {
                schemaVersion = Integer.parseInt(headerParts[1]);
            } catch (NumberFormatException failure) {
                throw new IllegalArgumentException("Invalid profile schema version: " + headerParts[1], failure);
            }
            if (schemaVersion != ProfileSnapshot.CURRENT_SCHEMA_VERSION) {
                throw new IllegalArgumentException("Unsupported profile schema version: " + schemaVersion);
            }

            Map<String, Boolean> modules = new LinkedHashMap<>();
            Map<String, String> settings = new LinkedHashMap<>();
            Base64.Decoder base64 = Base64.getUrlDecoder();

            String line;
            int entries = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                if (++entries > MAX_ENTRIES) {
                    throw new IllegalArgumentException("Profile has more than " + MAX_ENTRIES + " entries");
                }

                String[] parts = line.split("\\t", -1);
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Malformed profile line: " + line);
                }

                switch (parts[0]) {
                    case "m" -> {
                        Boolean enabled = switch (parts[2]) {
                            case "1" -> true;
                            case "0" -> false;
                            default -> throw new IllegalArgumentException(
                                    "Invalid module state for " + parts[1] + ": " + parts[2]);
                        };
                        if (modules.putIfAbsent(parts[1], enabled) != null) {
                            throw new IllegalArgumentException("Duplicate module state: " + parts[1]);
                        }
                    }
                    case "s" -> {
                        if (!parts[2].startsWith("~")) {
                            throw new IllegalArgumentException("Invalid setting encoding for " + parts[1]);
                        }
                        String decoded;
                        try {
                            byte[] raw = base64.decode(parts[2].substring(1));
                            decoded = new String(raw, StandardCharsets.UTF_8);
                        } catch (IllegalArgumentException failure) {
                            throw new IllegalArgumentException(
                                    "Invalid Base64 setting value for " + parts[1],
                                    failure);
                        }
                        if (settings.putIfAbsent(parts[1], decoded) != null) {
                            throw new IllegalArgumentException("Duplicate setting value: " + parts[1]);
                        }
                    }
                    default -> throw new IllegalArgumentException("Unknown profile record: " + parts[0]);
                }
            }

            return new ProfileSnapshot(schemaVersion, modules, settings);
        } catch (IOException impossible) {
            throw new IllegalStateException("Unexpected in-memory profile read failure", impossible);
        }
    }
}
