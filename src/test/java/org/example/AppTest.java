package org.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

/**
 * Test for Quarkus SOAP & REST Service
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
             .body("service", is("Quarkus SOAP & REST Service"));
    }

    @Test
    public void testInfoEndpoint() {
        given()
          .when().get("/health/info")
          .then()
             .statusCode(200)
             .body(containsString("Quarkus SOAP & REST Service"));
    }

    @Test
    public void testRestHelloEndpoint() {
        given()
          .when().get("/api/hello/say/TestUser")
          .then()
             .statusCode(200)
             .body("result", containsString("Hello, TestUser"))
             .body("status", is("SUCCESS"));
    }

    @Test
    public void testRestTimeEndpoint() {
        given()
          .when().get("/api/hello/time")
          .then()
             .statusCode(200)
             .body("result", containsString("Current server time"))
             .body("status", is("SUCCESS"));
    }

    @Test
    public void testRestEchoEndpoint() {
        given()
          .when().get("/api/hello/echo?message=test")
          .then()
             .statusCode(200)
             .body("result", is("Echo: test"))
             .body("status", is("SUCCESS"));
    }
}
