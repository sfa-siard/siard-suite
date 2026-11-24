package ch.admin.bar.siardsuite.localwebapp.connections;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.startsWith;

/**
 * Spec requirement "DBMS catalog": the five server-based entries with fields and placeholder
 * examples, behind the session check.
 */
@QuarkusTest
class DbmsResourceTest {

    @Test
    void catalogListsFiveServerBasedDbms() {
        Sessions.authenticated()
                .get("/api/dbms")
                .then()
                .statusCode(200)
                .body("size()", is(5))
                .body("id", not(hasItem("access")))
                .body("name", everyItem(notNullValue()))
                .body("kind", everyItem(is("server")))
                .body("fields", everyItem(notNullValue()))
                .body("fields", everyItem(not(empty())));
    }

    @Test
    void exampleHostsArePlaceholders() {
        Sessions.authenticated()
                .get("/api/dbms")
                .then()
                .statusCode(200)
                .body("examples.host", everyItem(containsString("exampleHost")));
    }

    @Test
    void catalogRequiresSession() {
        given().get("/api/dbms")
                .then()
                .statusCode(401)
                .contentType(startsWith("application/problem+json"));

        given().cookie(Sessions.cookieName(), "not-the-secret")
                .get("/api/dbms")
                .then()
                .statusCode(401)
                .contentType(startsWith("application/problem+json"));
    }
}
