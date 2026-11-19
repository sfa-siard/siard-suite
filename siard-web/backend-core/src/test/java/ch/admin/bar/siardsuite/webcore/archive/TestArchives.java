package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import io.restassured.response.Response;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static io.restassured.RestAssured.given;

/**
 * Helpers for the archive tests: fixtures read in place from siard-api's test resources, fresh
 * scopes, and requests in a scope.
 */
final class TestArchives {

    private TestArchives() {
    }

    static Path fixture(String name) {
        return Path.of(System.getProperty("siard.testfiles"), name);
    }

    static ArchiveScope newScope() {
        return new ArchiveScope(UUID.randomUUID().toString());
    }

    static Response get(ArchiveScope scope, String path) {
        return given().header(TestScopeResolver.HEADER, scope.value()).get(path);
    }

    /** Index path {@code /schemas/{s}/tables/{t}} of the named table in a fixture. */
    static String tablePath(String fixture, String table) throws IOException {
        Archive archive = ArchiveImpl.newInstance();
        archive.openReadOnly(fixture(fixture).toFile());
        try {
            for (int s = 0; s < archive.getSchemas(); s++) {
                for (int t = 0; t < archive.getSchema(s).getTables(); t++) {
                    if (archive.getSchema(s).getTable(t).getMetaTable().getName().equals(table)) {
                        return "/schemas/" + s + "/tables/" + t;
                    }
                }
            }
        } finally {
            archive.close();
        }
        throw new IllegalArgumentException(table);
    }

    static Response delete(ArchiveScope scope, String path) {
        return given().header(TestScopeResolver.HEADER, scope.value()).delete(path);
    }
}
