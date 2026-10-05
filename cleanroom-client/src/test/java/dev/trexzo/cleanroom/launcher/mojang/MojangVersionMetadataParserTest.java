package dev.trexzo.cleanroom.launcher.mojang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.trexzo.cleanroom.launcher.mojang.MojangVersionMetadata.Action;
import org.junit.jupiter.api.Test;

class MojangVersionMetadataParserTest {
    private final MojangVersionMetadataParser parser = new MojangVersionMetadataParser();

    @Test
    void parsesClientAssetsLibrariesNativesAndRules() {
        String json = """
                {
                  "id": "1.8.9",
                  "mainClass": "net.minecraft.client.main.Main",
                  "minecraftArguments": "--username ${auth_player_name}",
                  "assetIndex": {
                    "id": "1.8",
                    "sha1": "f6ad102bcaa53b1a58358f16e376d548d44933ec",
                    "size": 78494,
                    "totalSize": 114885064,
                    "url": "https://launchermeta.mojang.com/v1/packages/f6ad102bcaa53b1a58358f16e376d548d44933ec/1.8.json"
                  },
                  "downloads": {
                    "client": {
                      "sha1": "3870888a6c3d349d3771a3e9d16c9bf5e076b908",
                      "size": 8461484,
                      "url": "https://launcher.mojang.com/v1/objects/3870888a6c3d349d3771a3e9d16c9bf5e076b908/client.jar"
                    }
                  },
                  "libraries": [
                    {
                      "name": "org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209",
                      "downloads": {
                        "artifact": {
                          "path": "org/lwjgl/lwjgl/lwjgl/2.9.4-nightly-20150209/lwjgl-2.9.4-nightly-20150209.jar",
                          "sha1": "697517568c68e78ae0b4544145af031c81082dfe",
                          "size": 1040055,
                          "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl/2.9.4-nightly-20150209/lwjgl-2.9.4-nightly-20150209.jar"
                        },
                        "classifiers": {
                          "natives-windows": {
                            "path": "org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar",
                            "sha1": "b84d5102b9dbfabfeb5e43c7e2828d98a7fc80e0",
                            "size": 613748,
                            "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar"
                          }
                        }
                      },
                      "natives": {"windows": "natives-windows"},
                      "rules": [
                        {"action": "allow", "os": {"name": "windows"}}
                      ],
                      "extract": {"exclude": ["META-INF/"]}
                    }
                  ]
                }
                """;

        MojangVersionMetadata metadata = parser.parse(json, "1.8.9");

        assertEquals("1.8.9", metadata.id());
        assertEquals("net.minecraft.client.main.Main", metadata.mainClass());
        assertEquals(8461484L, metadata.client().size());
        assertEquals("3870888a6c3d349d3771a3e9d16c9bf5e076b908", metadata.client().sha1());
        assertEquals("1.8", metadata.assetIndex().id());
        assertEquals(1, metadata.libraries().size());
        assertEquals("natives-windows", metadata.libraries().getFirst().natives().get("windows"));
        assertEquals(Action.ALLOW, metadata.libraries().getFirst().rules().getFirst().action());
        assertEquals("META-INF/", metadata.libraries().getFirst().extractExcludes().getFirst());
    }

    @Test
    void rejectsWrongVersionId() {
        String json = """
                {
                  "id": "1.8.8",
                  "mainClass": "net.minecraft.client.main.Main",
                  "assetIndex": {
                    "id": "1.8",
                    "sha1": "f6ad102bcaa53b1a58358f16e376d548d44933ec",
                    "size": 1,
                    "totalSize": 1,
                    "url": "https://launchermeta.mojang.com/1.8.json"
                  },
                  "downloads": {
                    "client": {
                      "sha1": "3870888a6c3d349d3771a3e9d16c9bf5e076b908",
                      "size": 1,
                      "url": "https://launcher.mojang.com/client.jar"
                    }
                  },
                  "libraries": []
                }
                """;

        assertThrows(IllegalArgumentException.class, () -> parser.parse(json, "1.8.9"));
    }
}
