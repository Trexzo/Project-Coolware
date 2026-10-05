package dev.trexzo.cleanroom.core.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProfileStoreTest {
    @TempDir
    Path tempDir;

    @Test
    void savesLoadsAndReplacesProfileWithoutLeavingTempFiles() throws Exception {
        ProfileStore store = new ProfileStore(new ProfileCodec());
        Path profile = tempDir.resolve("profiles").resolve("default.profile");

        ProfileSnapshot first = ProfileSnapshot.current(
                Map.of("render.esp", true),
                Map.of("render.esp.range", "4"));
        store.save(profile, first);

        assertTrue(Files.isRegularFile(profile));
        assertEquals(first, store.load(profile).orElseThrow());

        ProfileSnapshot second = ProfileSnapshot.current(
                Map.of("render.esp", false),
                Map.of("render.esp.range", "6"));
        store.save(profile, second);

        assertEquals(second, store.load(profile).orElseThrow());

        try (var files = Files.list(profile.getParent())) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }

    @Test
    void missingProfileLoadsAsEmpty() throws Exception {
        ProfileStore store = new ProfileStore(new ProfileCodec());

        assertTrue(store.load(tempDir.resolve("missing.profile")).isEmpty());
    }
}
