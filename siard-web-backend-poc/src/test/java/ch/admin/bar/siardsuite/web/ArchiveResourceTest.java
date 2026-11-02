package ch.admin.bar.siardsuite.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ArchiveResourceTest {

    private static final Path MASTER_SIARD = Path.of("..", "master.siard").toAbsolutePath().normalize();
    private static final Path NOT_AN_ARCHIVE = Path.of("..", "settings.gradle.kts").toAbsolutePath().normalize();

    @Inject
    SessionToken sessionToken;

    @Test
    void openValidArchiveReturnsCreated() {
        given()
                .queryParam("token", sessionToken.get())
                .contentType(ContentType.JSON)
                .body("{\"path\":\"" + jsonPath(MASTER_SIARD) + "\"}")
                .when().post("/api/archives")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("master.siard"));
    }

    @Test
    void openNonexistentPathReturnsBadRequest() {
        given()
                .queryParam("token", sessionToken.get())
                .contentType(ContentType.JSON)
                .body("{\"path\":\"/does/not/exist.siard\"}")
                .when().post("/api/archives")
                .then()
                .statusCode(400)
                .body("error", notNullValue());
    }

    @Test
    void openNonSiardFileReturnsBadRequest() {
        given()
                .queryParam("token", sessionToken.get())
                .contentType(ContentType.JSON)
                .body("{\"path\":\"" + jsonPath(NOT_AN_ARCHIVE) + "\"}")
                .when().post("/api/archives")
                .then()
                .statusCode(400)
                .body("error", notNullValue());
    }

    @Test
    void openWithoutTokenReturnsUnauthorized() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"path\":\"" + jsonPath(MASTER_SIARD) + "\"}")
                .when().post("/api/archives")
                .then().statusCode(401);
    }

    @Test
    void treeOfOpenedArchiveContainsSchemasTablesAndColumns() {
        String id = openMaster();

        given()
                .queryParam("token", sessionToken.get())
                .when().get("/api/archives/" + id + "/tree")
                .then()
                .statusCode(200)
                .body("schemas.size()", greaterThan(0))
                .body("schemas[0]", hasKey("name"))
                .body("schemas[0]", hasKey("tables"))
                .body("schemas[0].tables[0]", hasKey("name"))
                .body("schemas[0].tables[0]", hasKey("rowCount"))
                .body("schemas[0].tables[0].columns[0]", hasKey("name"))
                .body("schemas[0].tables[0].columns[0]", hasKey("type"));
    }

    @Test
    void treeOfUnknownArchiveReturnsNotFound() {
        given()
                .queryParam("token", sessionToken.get())
                .when().get("/api/archives/unknown-id/tree")
                .then().statusCode(404);
    }

    @Test
    void treeWithoutTokenReturnsUnauthorized() {
        given()
                .when().get("/api/archives/" + openMaster() + "/tree")
                .then().statusCode(401);
    }

    private String openMaster() {
        return given()
                .queryParam("token", sessionToken.get())
                .contentType(ContentType.JSON)
                .body("{\"path\":\"" + jsonPath(MASTER_SIARD) + "\"}")
                .when().post("/api/archives")
                .then().statusCode(201)
                .extract().path("id");
    }

    private static String jsonPath(Path path) {
        return path.toString().replace("\\", "\\\\");
    }
}
