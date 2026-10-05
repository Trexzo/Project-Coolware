package dev.trexzo.cleanroom.launcher.mojang;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

public final class TrustedMojangUri {
    private static final Set<String> ALLOWED_HOSTS = Set.of(
            "piston-meta.mojang.com",
            "piston-data.mojang.com",
            "launcher.mojang.com",
            "launchermeta.mojang.com",
            "libraries.minecraft.net",
            "resources.download.minecraft.net");

    private TrustedMojangUri() {}

    public static URI requireTrustedHttps(String raw) {
        return requireTrustedHttps(URI.create(Objects.requireNonNull(raw, "raw")));
    }

    public static URI requireTrustedHttps(URI uri) {
        Objects.requireNonNull(uri, "uri");
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("Mojang artifact URI must use HTTPS: " + uri);
        }
        String host = uri.getHost();
        if (host == null || !ALLOWED_HOSTS.contains(host.toLowerCase(java.util.Locale.ROOT))) {
            throw new IllegalArgumentException("Untrusted Mojang artifact host: " + uri);
        }
        if (uri.getUserInfo() != null || uri.getFragment() != null) {
            throw new IllegalArgumentException("Unexpected authority/fragment in Mojang artifact URI: " + uri);
        }
        return uri;
    }
}
