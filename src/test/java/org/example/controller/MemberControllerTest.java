package org.example.controller;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@Transactional
public class MemberControllerTest {

    @Test
    public void testHomePage() {
        when()
                .get("/")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Member Registration"))
                .body(containsString("Register"));
    }

    @Test
    public void testValidFormSubmission() {
        // Create form data
        Map<String, String> formParams = new HashMap<>();
        formParams.put("name", "John Doe");
        formParams.put("email", "john@example.com");
        formParams.put("phoneNumber", "1234567890");

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(formParams)
                .when()
                .post("/")
                .then()
                .statusCode(200);

        // Verify member appears in the list
        when()
                .get("/")
                .then()
                .statusCode(200)
                .body(containsString("John Doe"))
                .body(containsString("john@example.com"));
    }

    @Test
    public void testInvalidFormSubmission() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("name", "John123");  // Invalid name with numbers
        formParams.put("email", "not-an-email");
        formParams.put("phoneNumber", "123");

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(formParams)
                .when()
                .post("/")
                .then()
                .statusCode(400)
                .contentType(containsString("text/html"))
                .body(containsString("Must not contain numbers"))  // Name validation error
                .body(containsString("must be a well-formed email address"))  // Email validation error
                .body(containsString("size must be between 10 and 12"));  // Phone validation error
    }

    @Test
    public void testDuplicateEmailSubmission() {
        // First submission
        Map<String, String> formParams = new HashMap<>();
        formParams.put("name", "John Doe");
        formParams.put("email", "duplicate@example.com");
        formParams.put("phoneNumber", "1234567890");

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(formParams)
                .when()
                .post("/")
                .then()
                .statusCode(200);

        // Second submission with same email
        formParams.put("name", "Jane Doe");

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(formParams)
                .when()
                .post("/")
                .then()
                .statusCode(400)
                .contentType(containsString("text/html"))
                .body(containsString("Email already exists"));
    }

    @Test
    public void testFormRetainsValuesOnError() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("name", "John123");
        formParams.put("email", "not-an-email");
        formParams.put("phoneNumber", "123");

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(formParams)
                .when()
                .post("/")
                .then()
                .statusCode(400)
                .body(containsString("value=\"John123\""))  // Name field retained
                .body(containsString("value=\"not-an-email\""))  // Email field retained
                .body(containsString("value=\"123\""));  // Phone field retained
    }

    @Test
    public void testSuccessMessageDisplay() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("name", "John Doe");
        formParams.put("email", "success@example.com");
        formParams.put("phoneNumber", "1234567890");

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(formParams)
                .when()
                .post("/")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Registered!"));
    }

}