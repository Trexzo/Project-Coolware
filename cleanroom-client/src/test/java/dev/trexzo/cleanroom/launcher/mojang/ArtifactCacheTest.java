package dev.trexzo.cleanroom.launcher.mojang;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.DownloadSpec;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ArtifactCacheTest {
    @TempDir
    Path tempDir;

    @Test
    void downloadsVerifiesPromotesAndThenReusesCache() throws Exception {
        byte[] payload = "verified-minecraft-artifact".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        DownloadSpec spec = new DownloadSpec(
                URI.create("https://piston-data.mojang.com/test/client.jar"),
                Hashing.sha1(payload),
                payload.length);

        AtomicInteger downloads = new AtomicInteger();
        ArtifactCache cache = new ArtifactCache((uri, destination) -> {
            downloads.incrementAndGet();
            Files.write(destination, payload);
        });

        Path target = tempDir.resolve("client.jar");
        Path first = cache.ensure(spec, target);
        Path second = cache.ensure(spec, target);

        assertEquals(target.toAbsolutePath(), first);
        assertEquals(first, second);
        assertEquals(1, downloads.get());
        assertArrayEquals(payload, Files.readAllBytes(target));
        assertTrue(ArtifactCache.matches(target, spec));
    }

    @Test
    void refusesBadPayloadAndLeavesNoPartialTarget() throws Exception {
        byte[] expected = "expected".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        DownloadSpec spec = new DownloadSpec(
                URI.create("https://piston-data.mojang.com/test/client.jar"),
                Hashing.sha1(expected),
                expected.length);

        ArtifactCache cache = new ArtifactCache((uri, destination) ->
                Files.writeString(destination, "wrong-payload"));

        Path target = tempDir.resolve("client.jar");
        assertThrows(IOException.class, () -> cache.ensure(spec, target));

        assertFalse(Files.exists(target));
        try (var files = Files.list(tempDir)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().endsWith(".download")));
        }
    }

    @Test
    void rejectsUntrustedOrNonHttpsMetadataUris() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TrustedMojangUri.requireTrustedHttps("http://piston-meta.mojang.com/test"));
        assertThrows(
                IllegalArgumentException.class,
                () -> TrustedMojangUri.requireTrustedHttps("https://example.invalid/test"));
    }
}
