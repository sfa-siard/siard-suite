package ch.admin.bar.siardsuite.localwebapp.connections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import ch.admin.bar.siardsuite.localwebapp.support.TempConnectionStoreProfile;
import io.quarkus.arc.Arc;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Spec requirements "List saved connections", "Save a connection", "Update a saved connection", "Delete a saved connection"
 * and "Passwords are never stored or transmitted". Each test class gets an isolated store via
 * {@link TempConnectionStoreProfile}.
 */
@QuarkusTest
@TestProfile(TempConnectionStoreProfile.class)
class ConnectionsResourceTest {

    private static Map<String, Object> validPostgresBody() {
        return Map.of(
                "name", "Archive A",
                "dbmsId", "postgresql",
                "host", "db.example.org",
                "port", "5432",
                "dbName", "archive",
                "user", "archivist",
                "options", "ssl=true");
    }

    private static RequestSpecification authenticated() {
        return given().cookie(Sessions.cookieName(), Sessions.cookieValue());
    }

    // the store is shared by all tests of this class; empty it before each one
    @BeforeEach
    void resetStore() {
        ConnectionStore store = Arc.container().instance(ConnectionStore.class).get();
        for (SavedConnection connection : store.list()) {
            store.delete(connection.id());
        }
    }

    @Test
    void emptyList() {
        authenticated().get("/api/connections")
                .then()
                .statusCode(200)
                .body("$", empty());
    }

    @Test
    void populatedListIsOrderedByNameAndHasNoPassword() {
        authenticated().body(Map.of("name", "Archive B", "dbmsId", "mysql", "host", "h",
                        "port", "3306", "dbName", "d", "user", "u"))
                .contentType("application/json")
                .post("/api/connections")
                .then().statusCode(201);
        authenticated().body(validPostgresBody())
                .contentType("application/json")
                .post("/api/connections")
                .then().statusCode(201);

        Response response = authenticated().get("/api/connections").then().statusCode(200).extract().response();

        assertFalse(response.asString().contains("password"), "no password in any entry");
        response.then()
                .body("size()", is(2))
                .body("[0].name", is("Archive A"))
                .body("[0].dbmsId", is("postgresql"))
                .body("[0].host", is("db.example.org"))
                .body("[0].port", is("5432"))
                .body("[0].dbName", is("archive"))
                .body("[0].user", is("archivist"))
                .body("[0].options", is("ssl=true"))
                .body("[0].file", nullValue())
                .body("[0].id", notNullValue())
                .body("[0].createdAt", notNullValue())
                .body("[0].updatedAt", notNullValue())
                .body("[1].name", is("Archive B"));
    }

    @Test
    void saveReturns201AndProfileIsListed() {
        authenticated().body(validPostgresBody())
                .contentType("application/json")
                .post("/api/connections")
                .then()
                .statusCode(201)
                .body("name", is("Archive A"))
                .body("dbmsId", is("postgresql"));

        authenticated().get("/api/connections")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", is("Archive A"));
    }

    @Test
    void duplicateNameIsRejected() {
        authenticated().body(validPostgresBody())
                .contentType("application/json")
                .post("/api/connections")
                .then().statusCode(201);

        authenticated().body(validPostgresBody())
                .contentType("application/json")
                .post("/api/connections")
                .then()
                .statusCode(409)
                .contentType(startsWith("application/problem+json"));

        authenticated().get("/api/connections")
                .then()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void invalidFieldsAreRejected() {
        // missing name
        authenticated().body(Map.of("dbmsId", "postgresql", "host", "h", "port", "1",
                        "dbName", "d", "user", "u"))
                .contentType("application/json")
                .post("/api/connections")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"));

        // unknown DBMS identifier
        authenticated().body(Map.of("name", "X", "dbmsId", "sqlite", "host", "h",
                        "port", "1", "dbName", "d", "user", "u"))
                .contentType("application/json")
                .post("/api/connections")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"));

        // file-based DBMS is not a valid profile target
        authenticated().body(Map.of("name", "X", "dbmsId", "access", "host", "h",
                        "port", "1", "dbName", "d", "user", "u"))
                .contentType("application/json")
                .post("/api/connections")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"));

        // non-numeric port
        authenticated().body(Map.of("name", "X", "dbmsId", "postgresql", "host", "h",
                        "port", "abc", "dbName", "d", "user", "u"))
                .contentType("application/json")
                .post("/api/connections")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"));

        // port out of range
        for (String port : new String[] {"0", "65536", "123456789012345678901234567890"}) {
            authenticated().body(Map.of("name", "X", "dbmsId", "postgresql", "host", "h",
                            "port", port, "dbName", "d", "user", "u"))
                    .contentType("application/json")
                    .post("/api/connections")
                    .then()
                    .statusCode(400)
                    .contentType(startsWith("application/problem+json"));
        }

        authenticated().get("/api/connections")
                .then()
                .statusCode(200)
                .body("$", empty());
    }

    @Test
    void updateReplacesTheProfile() {
        String id = createValidPostgres();
        Map<String, Object> changed = new java.util.HashMap<>(validPostgresBody());
        changed.put("name", "Archive Renamed");
        changed.put("port", "6543");

        authenticated().body(changed)
                .contentType("application/json")
                .put("/api/connections/" + id)
                .then()
                .statusCode(200)
                .body("id", is(Integer.valueOf(id)))
                .body("name", is("Archive Renamed"))
                .body("port", is("6543"));

        authenticated().get("/api/connections")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", is("Archive Renamed"));
    }

    @Test
    void updateWithUnchangedNameSucceeds() {
        String id = createValidPostgres();

        authenticated().body(validPostgresBody())
                .contentType("application/json")
                .put("/api/connections/" + id)
                .then()
                .statusCode(200);
    }

    @Test
    void updateToAnotherProfilesNameIsConflict() {
        createValidPostgres();
        String id = authenticated().body(Map.of("name", "Archive B", "dbmsId", "mysql", "host", "h",
                        "port", "3306", "dbName", "d", "user", "u"))
                .contentType("application/json")
                .post("/api/connections")
                .then().statusCode(201)
                .extract().jsonPath().getString("id");

        authenticated().body(validPostgresBody())
                .contentType("application/json")
                .put("/api/connections/" + id)
                .then()
                .statusCode(409)
                .contentType(startsWith("application/problem+json"));
    }

    @Test
    void updateValidatesAndRejectsUnknownIdentifiers() {
        String id = createValidPostgres();
        Map<String, Object> invalid = new java.util.HashMap<>(validPostgresBody());
        invalid.put("port", "70000");

        authenticated().body(invalid)
                .contentType("application/json")
                .put("/api/connections/" + id)
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"));

        authenticated().body(validPostgresBody())
                .contentType("application/json")
                .put("/api/connections/999")
                .then()
                .statusCode(404)
                .contentType(startsWith("application/problem+json"));

        authenticated().body(validPostgresBody())
                .contentType("application/json")
                .put("/api/connections/not-a-number")
                .then()
                .statusCode(404)
                .contentType(startsWith("application/problem+json"));

        given().body(validPostgresBody())
                .contentType("application/json")
                .put("/api/connections/" + id)
                .then()
                .statusCode(401);
    }

    private static String createValidPostgres() {
        return authenticated().body(validPostgresBody())
                .contentType("application/json")
                .post("/api/connections")
                .then().statusCode(201)
                .extract().jsonPath().getString("id");
    }

    @Test
    void passwordFieldIsNeverStored() {
        // even if a client sends a password, it is not part of the profile and never echoed back
        java.util.Map<String, Object> body = new java.util.HashMap<>(validPostgresBody());
        body.put("password", "super-secret");

        Response response = authenticated().body(body)
                .contentType("application/json")
                .post("/api/connections")
                .then().extract().response();

        assertFalse(response.asString().contains("super-secret"),
                "the password is not echoed in the response");

        String list = authenticated().get("/api/connections").then().statusCode(200).extract().asString();
        assertFalse(list.contains("super-secret"), "the password is not persisted");
        assertFalse(list.contains("\"password\""), "no password field in the profile");
    }

    @Test
    void deleteRemovesTheProfile() {
        String id = authenticated().body(validPostgresBody())
                .contentType("application/json")
                .post("/api/connections")
                .then().statusCode(201)
                .extract().jsonPath().getString("id");

        authenticated().delete("/api/connections/" + id)
                .then().statusCode(204);

        authenticated().get("/api/connections")
                .then().statusCode(200).body("$", empty());
    }

    @Test
    void unknownIdentifierIsNotFound() {
        authenticated().delete("/api/connections/999")
                .then()
                .statusCode(404)
                .contentType(startsWith("application/problem+json"));

        authenticated().delete("/api/connections/not-a-number")
                .then()
                .statusCode(404)
                .contentType(startsWith("application/problem+json"));
    }

    @Test
    void requestsWithoutSessionAreRejected() {
        given().get("/api/connections")
                .then()
                .statusCode(401)
                .contentType(startsWith("application/problem+json"));

        given().body(validPostgresBody())
                .contentType("application/json")
                .post("/api/connections")
                .then()
                .statusCode(401)
                .contentType(startsWith("application/problem+json"));

        // a profile created with a session survives a rejected delete
        String id = authenticated().body(validPostgresBody())
                .contentType("application/json")
                .post("/api/connections")
                .then().statusCode(201)
                .extract().jsonPath().getString("id");

        Map<String, Object> renamed = new java.util.HashMap<>(validPostgresBody());
        renamed.put("name", "Archive Renamed");
        given().body(renamed)
                .contentType("application/json")
                .put("/api/connections/" + id)
                .then()
                .statusCode(401)
                .contentType(startsWith("application/problem+json"));

        given().delete("/api/connections/" + id)
                .then()
                .statusCode(401)
                .contentType(startsWith("application/problem+json"));

        authenticated().get("/api/connections")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", is("Archive A"));
    }
}
