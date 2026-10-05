package dev.trexzo.cleanroom.launcher.mojang;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import java.net.URI;
import java.util.Objects;

public final class MojangManifestParser {
    public VersionRef resolve(String manifestJson, String versionId) {
        Objects.requireNonNull(manifestJson, "manifestJson");
        Objects.requireNonNull(versionId, "versionId");

        try {
            JsonObject root = JsonParser.parseString(manifestJson).getAsJsonObject();
            JsonArray versions = root.getAsJsonArray("versions");
            if (versions == null) {
                throw new IllegalArgumentException("Manifest has no versions array");
            }

            for (JsonElement element : versions) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject version = element.getAsJsonObject();
                if (!versionId.equals(requiredString(version, "id"))) {
                    continue;
                }

                String sha1 = requireSha1(requiredString(version, "sha1"));
                URI metadataUri = TrustedMojangUri.requireTrustedHttps(requiredString(version, "url"));
                String type = requiredString(version, "type");
                return new VersionRef(versionId, type, metadataUri, sha1);
            }
        } catch (JsonParseException | IllegalStateException failure) {
            throw new IllegalArgumentException("Invalid Mojang version manifest", failure);
        }

        throw new IllegalArgumentException("Minecraft version not found in manifest: " + versionId);
    }

    private static String requiredString(JsonObject object, String key) {
        JsonElement value = object.get(key);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Missing string field: " + key);
        }
        String result = value.getAsString();
        if (result.isBlank()) {
            throw new IllegalArgumentException("Blank string field: " + key);
        }
        return result;
    }

    static String requireSha1(String sha1) {
        if (!sha1.matches("[0-9a-fA-F]{40}")) {
            throw new IllegalArgumentException("Invalid SHA-1: " + sha1);
        }
        return sha1.toLowerCase(java.util.Locale.ROOT);
    }

    public record VersionRef(String id, String type, URI metadataUri, String metadataSha1) {
        public VersionRef {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(type, "type");
            metadataUri = TrustedMojangUri.requireTrustedHttps(metadataUri);
            metadataSha1 = requireSha1(metadataSha1);
        }
    }
}
