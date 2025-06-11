package org.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

/**
 * Test for Quarkus SOAP Service
 */
@QuarkusTest
public class AppTest {

    @Test
    public void testHealthEndpoint() {
        given()
          .when().get("/health")
          .then()
             .statusCode(200)
             .body("status", is("UP"))
             .body("service", is("Quarkus SOAP Service"));
    }

    @Test
    public void testInfoEndpoint() {
        given()
          .when().get("/health/info")
          .then()
             .statusCode(200)
             .body(containsString("Quarkus SOAP Service"));
    }
}
