package ch.admin.bar.siardsuite.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.net.URL;
import java.util.Optional;

/**
 * Helper class for resolving class-path-resources in tests.
 * <p>
 * Caution: Does not working in packed jar-archives!
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourcesResolver {

    /**
     * Checks if a resource exists and returns the path to it.
     *
     * @param resource The path from the source root to the resource.
     * @return A File object representing the existing resource.
     * @throws IllegalArgumentException if the specified resource does not exist.
     */
    public static URL resolve(final String resource) {
        return Optional.ofNullable(ResourcesResolver.class
                .getClassLoader()
                .getResource(resource))
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource \"%s\" not found", resource)));
    }
}
