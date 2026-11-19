package ch.admin.bar.siardsuite.localwebapp.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Archive spec requirement "The archive API is described and stays consistent across apps",
 * scenario "App description carries the shared API": every {@code /api/archives/**} path of the
 * core's description, and every schema it references, appears unchanged in this app's description.
 */
class SharedApiDescriptionTest {

    private static final String SHARED_PREFIX = "/api/archives";
    private static final String SCHEMA_REF = "#/components/schemas/";

    @Test
    void appDescriptionCarriesTheSharedApi() throws IOException {
        JsonNode core = read("siard.openapi.core");
        JsonNode app = read("siard.openapi.app");
        String regenerate = " differs; run ./gradlew :siard-web:backend-core:openApiUpdate "
                + ":siard-web:local:backend:openApiUpdate";

        Set<String> corePaths = sharedPaths(core);
        assertFalse(corePaths.isEmpty(), "the core describes no " + SHARED_PREFIX + " path");
        assertEquals(corePaths, sharedPaths(app), "the set of " + SHARED_PREFIX + " paths" + regenerate);
        for (String path : corePaths) {
            assertEquals(core.path("paths").path(path), app.path("paths").path(path), "path " + path + regenerate);
        }
        for (String schema : referencedSchemas(core, corePaths)) {
            JsonNode expected = core.path("components").path("schemas").path(schema);
            JsonNode actual = app.path("components").path("schemas").path(schema);
            assertEquals(expected, actual, "schema " + schema + regenerate);
        }
    }

    private static JsonNode read(String property) throws IOException {
        return new YAMLMapper().readTree(Path.of(System.getProperty(property)).toFile());
    }

    private static Set<String> sharedPaths(JsonNode description) {
        Set<String> paths = new TreeSet<>();
        description.path("paths").fieldNames().forEachRemaining(path -> {
            if (path.equals(SHARED_PREFIX) || path.startsWith(SHARED_PREFIX + "/")) {
                paths.add(path);
            }
        });
        return paths;
    }

    /** The schemas the given paths reference, directly or through other schemas. */
    private static Set<String> referencedSchemas(JsonNode description, Set<String> paths) {
        Set<String> found = new TreeSet<>();
        Deque<JsonNode> pending = new ArrayDeque<>();
        paths.forEach(path -> pending.push(description.path("paths").path(path)));
        while (!pending.isEmpty()) {
            JsonNode node = pending.pop();
            JsonNode ref = node.get("$ref");
            if (ref != null && ref.asText().startsWith(SCHEMA_REF)) {
                String name = ref.asText().substring(SCHEMA_REF.length());
                if (found.add(name)) {
                    pending.push(description.path("components").path("schemas").path(name));
                }
            }
            node.forEach(pending::push);
        }
        return found;
    }
}
