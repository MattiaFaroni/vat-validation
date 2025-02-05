import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VatCheckTest {

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api/vies";
    }

    @Test
    public void testValidVatNumber() {
        String requestBody = "{\"iso2\": \"IT\", \"vatNumber\": \"00159560366\" }";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/check")
                .then()
                .statusCode(200)
                .body("data.valid", equalTo(true))
                .body("data.name", equalTo("FERRARI S.P.A."))
                .body("system.code", equalTo(0))
                .body("system.description", equalTo("Ok"));
    }

    @Test
    public void testInvalidVatNumber() {
        String requestBody = "{\"iso2\": \"IT\", \"vatNumber\": \"123456789\" }";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/check")
                .then()
                .statusCode(200)
                .body("data.valid", equalTo(false))
                .body("system.code", equalTo(0))
                .body("system.description", equalTo("Ok"));
    }
}
