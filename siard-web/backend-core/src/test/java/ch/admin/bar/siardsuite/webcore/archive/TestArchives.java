package ch.admin.bar.siardsuite.webcore.archive;

import io.restassured.response.Response;

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

    static Response delete(ArchiveScope scope, String path) {
        return given().header(TestScopeResolver.HEADER, scope.value()).delete(path);
    }
}
