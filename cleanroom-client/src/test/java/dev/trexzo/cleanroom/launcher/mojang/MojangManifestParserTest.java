package dev.trexzo.cleanroom.launcher.mojang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import org.junit.jupiter.api.Test;

class MojangManifestParserTest {
    private final MojangManifestParser parser = new MojangManifestParser();

    @Test
    void resolvesMinecraft189FromManifestFixture() {
        String json = """
                {
                  "latest": {"release": "26.3", "snapshot": "26.3"},
                  "versions": [
                    {
                      "id": "1.8.9",
                      "type": "release",
                      "url": "https://piston-meta.mojang.com/v1/packages/d546f1707a3f2b7d034eece5ea2e311eda875787/1.8.9.json",
                      "sha1": "d546f1707a3f2b7d034eece5ea2e311eda875787"
                    }
                  ]
                }
                """;

        MojangManifestParser.VersionRef ref = parser.resolve(json, "1.8.9");

        assertEquals("1.8.9", ref.id());
        assertEquals("release", ref.type());
        assertEquals(
                URI.create("https://piston-meta.mojang.com/v1/packages/d546f1707a3f2b7d034eece5ea2e311eda875787/1.8.9.json"),
                ref.metadataUri());
        assertEquals("d546f1707a3f2b7d034eece5ea2e311eda875787", ref.metadataSha1());
    }

    @Test
    void rejectsManifestHostSubstitution() {
        String json = """
                {
                  "versions": [
                    {
                      "id": "1.8.9",
                      "type": "release",
                      "url": "https://example.invalid/1.8.9.json",
                      "sha1": "d546f1707a3f2b7d034eece5ea2e311eda875787"
                    }
                  ]
                }
                """;

        assertThrows(IllegalArgumentException.class, () -> parser.resolve(json, "1.8.9"));
    }

    @Test
    void rejectsMalformedSha1() {
        String json = """
                {
                  "versions": [
                    {
                      "id": "1.8.9",
                      "type": "release",
                      "url": "https://piston-meta.mojang.com/1.8.9.json",
                      "sha1": "not-a-hash"
                    }
                  ]
                }
                """;

        assertThrows(IllegalArgumentException.class, () -> parser.resolve(json, "1.8.9"));
    }
}
