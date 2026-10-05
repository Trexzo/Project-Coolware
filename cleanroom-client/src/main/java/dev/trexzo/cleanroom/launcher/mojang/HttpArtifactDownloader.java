package dev.trexzo.cleanroom.launcher.mojang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Objects;

public final class HttpArtifactDownloader implements ArtifactCache.Downloader {
    private static final int MAX_REDIRECTS = 4;
    private final HttpClient client;

    public HttpArtifactDownloader() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build());
    }

    HttpArtifactDownloader(HttpClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    @Override
    public void download(URI uri, Path destination) throws IOException, InterruptedException {
        URI current = TrustedMojangUri.requireTrustedHttps(uri);

        for (int redirects = 0; redirects <= MAX_REDIRECTS; redirects++) {
            HttpRequest request = HttpRequest.newBuilder(current)
                    .timeout(Duration.ofMinutes(2))
                    .header("User-Agent", "TrexzoCleanroomLauncher/0.0.1")
                    .GET()
                    .build();

            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            try (InputStream body = response.body()) {
                int status = response.statusCode();
                if (status == 200) {
                    Files.copy(body, destination, StandardCopyOption.REPLACE_EXISTING);
                    return;
                }

                if (isRedirect(status)) {
                    var locationHeader = response.headers().firstValue("location");
                    if (locationHeader.isEmpty()) {
                        throw new IOException("Redirect without Location from " + current);
                    }
                    current = TrustedMojangUri.requireTrustedHttps(current.resolve(locationHeader.get()));
                    continue;
                }

                throw new IOException("Unexpected HTTP " + status + " from " + current);
            }
        }

        throw new IOException("Too many redirects while downloading " + uri);
    }

    private static boolean isRedirect(int status) {
        return status == 301
                || status == 302
                || status == 303
                || status == 307
                || status == 308;
    }
}
