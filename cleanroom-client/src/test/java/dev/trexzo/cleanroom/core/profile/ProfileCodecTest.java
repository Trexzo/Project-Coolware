package dev.trexzo.cleanroom.core.profile;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProfileCodecTest {
    private final ProfileCodec codec = new ProfileCodec();

    @Test
    void encodingIsDeterministicRegardlessOfInputMapOrder() {
        Map<String, Boolean> modulesA = new LinkedHashMap<>();
        modulesA.put("render.esp", true);
        modulesA.put("combat.aura", false);

        Map<String, Boolean> modulesB = new LinkedHashMap<>();
        modulesB.put("combat.aura", false);
        modulesB.put("render.esp", true);

        Map<String, String> settingsA = new LinkedHashMap<>();
        settingsA.put("render.esp.label", "Åäö ✓");
        settingsA.put("combat.aura.range", "4");

        Map<String, String> settingsB = new LinkedHashMap<>();
        settingsB.put("combat.aura.range", "4");
        settingsB.put("render.esp.label", "Åäö ✓");

        assertArrayEquals(
                codec.encode(ProfileSnapshot.current(modulesA, settingsA)),
                codec.encode(ProfileSnapshot.current(modulesB, settingsB)));
    }

    @Test
    void roundTripsUnicodeAndEmptyValues() {
        ProfileSnapshot expected = ProfileSnapshot.current(
                Map.of("render.esp", true),
                Map.of(
                        "render.esp.label", "Spelare ✓",
                        "render.esp.note", ""));

        ProfileSnapshot decoded = codec.decode(codec.encode(expected));

        assertEquals(expected, decoded);
    }

    @Test
    void rejectsDuplicateRecords() {
        String profile = String.join(
                "\n",
                "cleanroom-profile\t1",
                "m\trender.esp\t1",
                "m\trender.esp\t0",
                "");

        assertThrows(
                IllegalArgumentException.class,
                () -> codec.decode(profile.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void rejectsUnsupportedSchema() {
        assertThrows(
                IllegalArgumentException.class,
                () -> codec.decode("cleanroom-profile\t2\n".getBytes(StandardCharsets.UTF_8)));
    }
}
