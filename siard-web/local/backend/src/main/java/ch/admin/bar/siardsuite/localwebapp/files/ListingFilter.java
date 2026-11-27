package ch.admin.bar.siardsuite.localwebapp.files;

import java.util.List;
import java.util.Locale;

/**
 * Which entries a listing keeps (spec "Listing filters"): hidden entries only on request, folders
 * always (unless hidden), files only when no extension is given or their name ends with one of
 * them, ignoring case, and no files at all for {@code foldersOnly}.
 */
record ListingFilter(List<String> extensions, boolean foldersOnly, boolean hidden) {

    ListingFilter {
        extensions = extensions == null ? List.of() : extensions.stream()
                .filter(extension -> extension != null && !extension.isBlank())
                .map(extension -> extension.toLowerCase(Locale.ROOT))
                .toList();
    }

    boolean accepts(FileEntry entry) {
        if (entry.hidden() && !hidden) {
            return false;
        }
        if (entry.folder()) {
            return true;
        }
        if (foldersOnly) {
            return false;
        }
        String name = entry.name().toLowerCase(Locale.ROOT);
        return extensions.isEmpty() || extensions.stream().anyMatch(name::endsWith);
    }
}
