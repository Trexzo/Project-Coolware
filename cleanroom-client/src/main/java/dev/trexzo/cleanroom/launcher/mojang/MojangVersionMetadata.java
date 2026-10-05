package dev.trexzo.cleanroom.launcher.mojang;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record MojangVersionMetadata(
        String id,
        String mainClass,
        String minecraftArguments,
        DownloadSpec client,
        AssetIndex assetIndex,
        List<LibrarySpec> libraries) {

    public MojangVersionMetadata {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(mainClass, "mainClass");
        minecraftArguments = minecraftArguments == null ? "" : minecraftArguments;
        Objects.requireNonNull(client, "client");
        Objects.requireNonNull(assetIndex, "assetIndex");
        libraries = List.copyOf(Objects.requireNonNull(libraries, "libraries"));
    }

    public record DownloadSpec(URI uri, String sha1, long size, String relativePath) {
        public DownloadSpec(URI uri, String sha1, long size) {
            this(uri, sha1, size, null);
        }

        public DownloadSpec {
            uri = TrustedMojangUri.requireTrustedHttps(uri);
            sha1 = MojangManifestParser.requireSha1(sha1);
            if (size < 0) {
                throw new IllegalArgumentException("Download size must not be negative: " + size);
            }
            if (relativePath != null) {
                relativePath = validateRelativePath(relativePath);
            }
        }

        private static String validateRelativePath(String path) {
            if (path.isBlank() || path.startsWith("/") || path.startsWith("\\")
                    || path.contains("\\") || path.contains("..")) {
                throw new IllegalArgumentException("Unsafe artifact relative path: " + path);
            }
            return path;
        }
    }

    public record AssetIndex(String id, DownloadSpec download, long totalSize) {
        public AssetIndex {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(download, "download");
            if (totalSize < 0) {
                throw new IllegalArgumentException("Asset totalSize must not be negative: " + totalSize);
            }
        }
    }

    public record LibrarySpec(
            String name,
            DownloadSpec artifact,
            Map<String, DownloadSpec> classifiers,
            Map<String, String> natives,
            List<Rule> rules,
            List<String> extractExcludes) {
        public LibrarySpec {
            Objects.requireNonNull(name, "name");
            classifiers = Map.copyOf(Objects.requireNonNull(classifiers, "classifiers"));
            natives = Map.copyOf(Objects.requireNonNull(natives, "natives"));
            rules = List.copyOf(Objects.requireNonNull(rules, "rules"));
            extractExcludes = List.copyOf(Objects.requireNonNull(extractExcludes, "extractExcludes"));
        }
    }

    public record Rule(Action action, OsConstraint os) {
        public Rule {
            Objects.requireNonNull(action, "action");
        }
    }

    public record OsConstraint(String name, String versionRegex, String archRegex) {}

    public enum Action {
        ALLOW,
        DISALLOW
    }
}
