package ch.admin.bar.siardsuite.webcore.archive;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spec requirement "Large object content", scenarios "Aborted download releases the archive" and
 * "A download that is never written releases the archive".
 */
@QuarkusTest
@TestProfile(ShortLockTimeoutProfile.class)
class AbortedDownloadTest {

    @Inject
    ArchiveRegistry registry;

    @TestHTTPResource("/")
    URL server;

    @TempDir
    Path tmp;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    @Test
    void abortedDownloadReleasesTheArchive() throws Exception {
        byte[] large = new byte[20 * 1024 * 1024];
        for (int i = 0; i < large.length; i++) {
            large[i] = (byte) i;
        }
        Path crafted = CraftedArchive.of("sample.siard")
                .replaceLob("content/schema0/table0/lob9/record1.bin", "content/schema0/table0/lob9/record1.bin", large)
                .writeTo(tmp);
        ArchiveScope scope = newScope();
        String archive = "/api/archives/" + registry.register(scope, crafted, () -> { }).value();

        try (Socket socket = new Socket(server.getHost(), server.getPort())) {
            OutputStream out = socket.getOutputStream();
            out.write(("GET " + archive + "/schemas/0/tables/0/rows/1/columns/" + ContentTest.CBLOB + "/content HTTP/1.1\r\n"
                    + "Host: " + server.getHost() + ":" + server.getPort() + "\r\n"
                    + TestScopeResolver.HEADER + ": " + scope.value() + "\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
            out.flush();
            InputStream in = socket.getInputStream();
            in.readNBytes(256 * 1024); // only read first 256kb of the response
        } // socket closes mid download

        // the server notices the closed connection on its next write; allow it a moment
        int status = 0;
        for (int attempt = 0; attempt < 10 && status != 200; attempt++) {
            status = get(scope, archive).statusCode();
        }
        assertEquals(200, status);
    }

    @Test
    void aDownloadThatIsNeverWrittenReleasesTheArchive() throws Exception {
        ArchiveScope scope = newScope();
        ArchiveId id = registry.register(scope, fixture("sample.siard"), () -> { });
        String archive = "/api/archives/" + id.value();
        String content = archive + "/schemas/0/tables/0/rows/1/columns/" + ContentTest.CBLOB + "/content";

        given().header(TestScopeResolver.HEADER, scope.value()).head(content).then().statusCode(200);
        assertEquals(200, get(scope, archive).statusCode());

        // the request waits for the held archive, the client leaves, then it gets the archive
        // after the connection is closed, so Quarkus never calls the writer
        ArchiveLease held = registry.acquire(scope, id);
        try (Socket socket = new Socket(server.getHost(), server.getPort())) {
            OutputStream out = socket.getOutputStream();
            out.write(("GET " + content + " HTTP/1.1\r\n"
                    + "Host: " + server.getHost() + ":" + server.getPort() + "\r\n"
                    + TestScopeResolver.HEADER + ": " + scope.value() + "\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
            out.flush();
            Thread.sleep(200);
        }
        Thread.sleep(200);
        held.close();
        Thread.sleep(300);
        assertEquals(200, get(scope, archive).statusCode());
    }
}
