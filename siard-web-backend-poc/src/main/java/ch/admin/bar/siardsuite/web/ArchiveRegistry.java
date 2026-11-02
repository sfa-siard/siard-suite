package ch.admin.bar.siardsuite.web;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of archives opened during this agent session, keyed by a generated
 * identifier.
 */
@ApplicationScoped
public class ArchiveRegistry {

    private final Map<String, Archive> archives = new ConcurrentHashMap<>();

    public OpenedArchive open(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Not a readable file: " + path);
        }
        Archive archive = ArchiveImpl.newInstance();
        archive.open(path.toFile());
        String id = UUID.randomUUID().toString();
        archives.put(id, archive);
        return new OpenedArchive(id, path.getFileName().toString());
    }

    public Archive get(String id) {
        return archives.get(id);
    }

    public record OpenedArchive(String id, String name) {
    }
}
