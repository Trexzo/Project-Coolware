package dev.trexzo.cleanroom.launcher.mojang;

import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.DownloadSpec;
import java.io.IOException;
import java.net.URI;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;

public final class ArtifactCache {
    private final Downloader downloader;

    public ArtifactCache(Downloader downloader) {
        this.downloader = Objects.requireNonNull(downloader, "downloader");
    }

    public Path ensure(DownloadSpec spec, Path target) throws IOException, InterruptedException {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(target, "target");

        Path absolute = target.toAbsolutePath();
        if (matches(absolute, spec)) {
            return absolute;
        }

        Path parent = absolute.getParent();
        if (parent == null) {
            throw new IOException("Artifact path has no parent: " + target);
        }
        Files.createDirectories(parent);

        Path temp = Files.createTempFile(parent, absolute.getFileName().toString() + ".", ".download");
        boolean promoted = false;
        try {
            downloader.download(spec.uri(), temp);
            verify(temp, spec);
            try {
                Files.move(
                        temp,
                        absolute,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING);
            }
            promoted = true;
            return absolute;
        } finally {
            if (!promoted) {
                Files.deleteIfExists(temp);
            }
        }
    }

    public static boolean matches(Path path, DownloadSpec spec) throws IOException {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(spec, "spec");
        if (!Files.isRegularFile(path) || Files.size(path) != spec.size()) {
            return false;
        }
        return Hashing.sha1(path).equals(spec.sha1().toLowerCase(Locale.ROOT));
    }

    private static void verify(Path path, DownloadSpec spec) throws IOException {
        long size = Files.size(path);
        if (size != spec.size()) {
            throw new IOException(
                    "Artifact size mismatch for " + spec.uri()
                            + ": expected " + spec.size() + " but got " + size);
        }

        String sha1 = Hashing.sha1(path);
        if (!sha1.equals(spec.sha1().toLowerCase(Locale.ROOT))) {
            throw new IOException(
                    "Artifact SHA-1 mismatch for " + spec.uri()
                            + ": expected " + spec.sha1() + " but got " + sha1);
        }
    }

    @FunctionalInterface
    public interface Downloader {
        void download(URI uri, Path destination) throws IOException, InterruptedException;
    }
}
