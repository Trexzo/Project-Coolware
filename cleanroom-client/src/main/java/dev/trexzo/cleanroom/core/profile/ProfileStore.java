package dev.trexzo.cleanroom.core.profile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;

public final class ProfileStore {
    private final ProfileCodec codec;

    public ProfileStore(ProfileCodec codec) {
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    public Optional<ProfileSnapshot> load(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        long size = Files.size(path);
        if (size > ProfileCodec.MAX_PROFILE_BYTES) {
            throw new IOException("Profile exceeds " + ProfileCodec.MAX_PROFILE_BYTES + " bytes: " + size);
        }
        try {
            return Optional.of(codec.decode(Files.readAllBytes(path)));
        } catch (IllegalArgumentException failure) {
            throw new IOException("Invalid profile: " + path, failure);
        }
    }

    public void save(Path path, ProfileSnapshot snapshot) throws IOException {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(snapshot, "snapshot");

        Path absolute = path.toAbsolutePath();
        Path parent = absolute.getParent();
        if (parent == null) {
            throw new IOException("Profile path has no parent: " + path);
        }
        Files.createDirectories(parent);

        byte[] bytes = codec.encode(snapshot);
        Path temp = Files.createTempFile(parent, absolute.getFileName().toString() + ".", ".tmp");
        boolean moved = false;
        try {
            try (FileChannel channel = FileChannel.open(
                    temp,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.force(true);
            }

            try {
                Files.move(
                        temp,
                        absolute,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING);
            }
            moved = true;
        } finally {
            if (!moved) {
                Files.deleteIfExists(temp);
            }
        }
    }
}
