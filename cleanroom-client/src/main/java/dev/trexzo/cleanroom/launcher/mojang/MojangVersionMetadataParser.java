package dev.trexzo.cleanroom.launcher.mojang;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.Action;
import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.AssetIndex;
import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.DownloadSpec;
import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.LibrarySpec;
import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.OsConstraint;
import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.Rule;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MojangVersionMetadataParser {
    public MojangVersionMetadata parse(String json, String expectedVersionId) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(expectedVersionId, "expectedVersionId");

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            String id = requiredString(root, "id");
            if (!expectedVersionId.equals(id)) {
                throw new IllegalArgumentException(
                        "Version metadata id mismatch: expected " + expectedVersionId + " but got " + id);
            }

            String mainClass = requiredString(root, "mainClass");
            String minecraftArguments = optionalString(root, "minecraftArguments", "");

            JsonObject downloads = requiredObject(root, "downloads");
            DownloadSpec client = parseDownload(requiredObject(downloads, "client"));

            JsonObject asset = requiredObject(root, "assetIndex");
            AssetIndex assetIndex = new AssetIndex(
                    requiredString(asset, "id"),
                    parseDownload(asset),
                    requiredLong(asset, "totalSize"));

            JsonArray librariesJson = requiredArray(root, "libraries");
            List<LibrarySpec> libraries = new ArrayList<>(librariesJson.size());
            for (JsonElement element : librariesJson) {
                if (!element.isJsonObject()) {
                    throw new IllegalArgumentException("Library entry must be an object");
                }
                libraries.add(parseLibrary(element.getAsJsonObject()));
            }

            return new MojangVersionMetadata(
                    id,
                    mainClass,
                    minecraftArguments,
                    client,
                    assetIndex,
                    libraries);
        } catch (JsonParseException | IllegalStateException failure) {
            throw new IllegalArgumentException("Invalid Mojang version metadata", failure);
        }
    }

    private static LibrarySpec parseLibrary(JsonObject library) {
        String name = requiredString(library, "name");
        DownloadSpec artifact = null;
        Map<String, DownloadSpec> classifiers = new LinkedHashMap<>();

        JsonObject downloads = optionalObject(library, "downloads");
        if (downloads != null) {
            JsonObject artifactJson = optionalObject(downloads, "artifact");
            if (artifactJson != null) {
                artifact = parseDownload(artifactJson);
            }

            JsonObject classifierJson = optionalObject(downloads, "classifiers");
            if (classifierJson != null) {
                for (Map.Entry<String, JsonElement> entry : classifierJson.entrySet()) {
                    if (!entry.getValue().isJsonObject()) {
                        throw new IllegalArgumentException(
                                "Classifier " + entry.getKey() + " for " + name + " must be an object");
                    }
                    classifiers.put(entry.getKey(), parseDownload(entry.getValue().getAsJsonObject()));
                }
            }
        }

        Map<String, String> natives = new LinkedHashMap<>();
        JsonObject nativesJson = optionalObject(library, "natives");
        if (nativesJson != null) {
            for (Map.Entry<String, JsonElement> entry : nativesJson.entrySet()) {
                if (!entry.getValue().isJsonPrimitive()
                        || !entry.getValue().getAsJsonPrimitive().isString()) {
                    throw new IllegalArgumentException(
                            "Native classifier mapping for " + name + " must be a string");
                }
                natives.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        List<Rule> rules = new ArrayList<>();
        JsonArray rulesJson = optionalArray(library, "rules");
        if (rulesJson != null) {
            for (JsonElement element : rulesJson) {
                if (!element.isJsonObject()) {
                    throw new IllegalArgumentException("Library rule for " + name + " must be an object");
                }
                JsonObject rule = element.getAsJsonObject();
                Action action = switch (requiredString(rule, "action")) {
                    case "allow" -> Action.ALLOW;
                    case "disallow" -> Action.DISALLOW;
                    default -> throw new IllegalArgumentException(
                            "Unknown library rule action for " + name + ": " + requiredString(rule, "action"));
                };

                JsonObject os = optionalObject(rule, "os");
                OsConstraint constraint = os == null
                        ? null
                        : new OsConstraint(
                                optionalString(os, "name", null),
                                optionalString(os, "version", null),
                                optionalString(os, "arch", null));
                rules.add(new Rule(action, constraint));
            }
        }

        List<String> excludes = new ArrayList<>();
        JsonObject extract = optionalObject(library, "extract");
        if (extract != null) {
            JsonArray excludeArray = optionalArray(extract, "exclude");
            if (excludeArray != null) {
                for (JsonElement element : excludeArray) {
                    if (!element.isJsonPrimitive()
                            || !element.getAsJsonPrimitive().isString()) {
                        throw new IllegalArgumentException("Library extract exclusion must be a string");
                    }
                    excludes.add(element.getAsString());
                }
            }
        }

        return new LibrarySpec(name, artifact, classifiers, natives, rules, excludes);
    }

    private static DownloadSpec parseDownload(JsonObject object) {
        return new DownloadSpec(
                TrustedMojangUri.requireTrustedHttps(requiredString(object, "url")),
                requiredString(object, "sha1"),
                requiredLong(object, "size"),
                optionalString(object, "path", null));
    }

    private static JsonObject requiredObject(JsonObject parent, String key) {
        JsonObject result = optionalObject(parent, key);
        if (result == null) {
            throw new IllegalArgumentException("Missing object field: " + key);
        }
        return result;
    }

    private static JsonObject optionalObject(JsonObject parent, String key) {
        JsonElement value = parent.get(key);
        if (value == null || value.isJsonNull()) {
            return null;
        }
        if (!value.isJsonObject()) {
            throw new IllegalArgumentException("Field is not an object: " + key);
        }
        return value.getAsJsonObject();
    }

    private static JsonArray requiredArray(JsonObject parent, String key) {
        JsonArray result = optionalArray(parent, key);
        if (result == null) {
            throw new IllegalArgumentException("Missing array field: " + key);
        }
        return result;
    }

    private static JsonArray optionalArray(JsonObject parent, String key) {
        JsonElement value = parent.get(key);
        if (value == null || value.isJsonNull()) {
            return null;
        }
        if (!value.isJsonArray()) {
            throw new IllegalArgumentException("Field is not an array: " + key);
        }
        return value.getAsJsonArray();
    }

    private static String requiredString(JsonObject object, String key) {
        String result = optionalString(object, key, null);
        if (result == null || result.isBlank()) {
            throw new IllegalArgumentException("Missing string field: " + key);
        }
        return result;
    }

    private static String optionalString(JsonObject object, String key, String fallback) {
        JsonElement value = object.get(key);
        if (value == null || value.isJsonNull()) {
            return fallback;
        }
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Field is not a string: " + key);
        }
        return value.getAsString();
    }

    private static long requiredLong(JsonObject object, String key) {
        JsonElement value = object.get(key);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException("Missing numeric field: " + key);
        }
        long result = value.getAsLong();
        if (result < 0) {
            throw new IllegalArgumentException("Negative numeric field " + key + ": " + result);
        }
        return result;
    }
}
