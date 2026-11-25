package ch.admin.bar.siardsuite.localwebapp.files;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Lists a folder on the calling thread (design D3). Every call may block for as long as the file
 * system does, so it only runs on a {@link FolderListings} worker. The entry cap is applied after
 * reading the whole folder (sorting needs every entry), so a huge folder on a slow share can run
 * into the listing's time limit (504) even though only 10000 entries would be returned.
 */
class FolderLister {

    static final int MAX_ENTRIES = 10_000;

    // Windows keeps "hidden" in the DOS attributes, read together with the basic ones; elsewhere it
    // is the leading dot of the name. Chosen by OS, not by the "dos" view: Linux offers that view
    // too (backed by extended attributes), which would cost an extra read per entry
    private static final boolean WINDOWS = Os.current() == Os.WINDOWS;

    private static final Comparator<FileEntry> ORDER = Comparator
            .comparing((FileEntry entry) -> !entry.folder())
            .thenComparing(FileEntry::name, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(FileEntry::name);

    private final int maxEntries;

    FolderLister() {
        this(MAX_ENTRIES);
    }

    FolderLister(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * @throws java.nio.file.NoSuchFileException   if nothing exists at the path
     * @throws NotDirectoryException               if the path is not a folder
     * @throws java.nio.file.AccessDeniedException if the folder may not be read
     */
    FolderListing list(Path folder, ListingFilter filter) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(folder, BasicFileAttributes.class);
        if (!attributes.isDirectory()) {
            throw new NotDirectoryException(folder.toString());
        }
        List<FileEntry> entries = new ArrayList<>();
        try (DirectoryStream<Path> children = open(folder)) {
            for (Path child : children) {
                entry(child).filter(filter::accepts).ifPresent(entries::add);
            }
        } catch (DirectoryIteratorException e) {
            // a read error while iterating arrives unchecked; unwrap it so it maps like any other
            throw e.getCause();
        }
        entries.sort(ORDER);
        boolean truncated = entries.size() > maxEntries;
        List<FileEntry> kept = truncated ? List.copyOf(entries.subList(0, maxEntries)) : List.copyOf(entries);
        return new FolderListing(folder.toString(), FilePaths.parentOf(folder), kept, truncated);
    }

    /** Opens the folder for iteration; replaceable in tests. */
    DirectoryStream<Path> open(Path folder) throws IOException {
        return Files.newDirectoryStream(folder);
    }

    // entries whose attributes cannot be read (broken links, races, no permission) are left out
    static Optional<FileEntry> entry(Path path) {
        try {
            BasicFileAttributes attributes;
            boolean hidden;
            if (WINDOWS) {
                DosFileAttributes dos = Files.readAttributes(path, DosFileAttributes.class);
                attributes = dos;
                hidden = dos.isHidden();
            } else {
                attributes = Files.readAttributes(path, BasicFileAttributes.class);
                hidden = Files.isHidden(path);
            }
            boolean folder = attributes.isDirectory();
            Path name = path.getFileName();
            return Optional.of(new FileEntry(
                    name == null ? path.toString() : name.toString(),
                    path.toString(),
                    folder,
                    folder ? null : attributes.size(),
                    attributes.lastModifiedTime().toInstant().toString(),
                    hidden));
        } catch (IOException | SecurityException e) {
            return Optional.empty();
        }
    }
}
