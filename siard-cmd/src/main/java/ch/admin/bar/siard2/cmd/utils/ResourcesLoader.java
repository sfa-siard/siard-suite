package ch.admin.bar.siard2.cmd.utils;

import java.io.InputStream;
import java.util.Optional;

/**
 * Helper class for loading class-path-resources.
 */
public class ResourcesLoader {
    private ResourcesLoader() {
    }

    /**
     * Checks if a resource exists and returns it as input stream.
     *
     * @param resource The path from the source root to the resource.
     * @return The resource as input stream.
     * @throws IllegalArgumentException if the specified resource does not exist.
     */
    public static InputStream loadResource(final String resource) {
        final Optional<InputStream> urlToResource = Optional.ofNullable(ResourcesLoader.class
                .getClassLoader()
                .getResourceAsStream(resource));

        return urlToResource
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource \"%s\" not found", resource)));
    }
}
