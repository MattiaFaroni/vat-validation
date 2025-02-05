import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ViesStatusTest {

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api/vies";
    }

    @Test
    public void testViesStatus() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/status")
                .then()
                .statusCode(200)
                .body("data.size()", greaterThan(0))
                .body("data[0].iso2", notNullValue())
                .body("data[0].status", notNullValue())
                .body("system.code", equalTo(0))
                .body("system.description", equalTo("Ok"));
    }
}
