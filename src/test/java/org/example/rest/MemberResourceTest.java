package org.example.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@Transactional
public class MemberResourceTest {

    @Test
    public void testCreateMember() {
        // Create a new member
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "John Doe",
                            "email": "john1@example.com",
                            "phoneNumber": "1234567890"
                        }
                        """)
                .when()
                .post("/api/members")
                .then()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("john1@example.com"));

        // Verify the member exists in the list
        given()
                .when()
                .get("/api/members")
                .then()
                .statusCode(200)
                .body("name", hasItem("John Doe"));
    }

    @Test
    public void testInvalidMemberData() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "John123",
                            "email": "not-an-email",
                            "phoneNumber": "invalid"
                        }
                        """)
                .when()
                .post("/api/members")
                .then()
                .statusCode(400)
                .body("violations.field", hasItems(
                        containsString("phoneNumber"),
                        containsString("email")
                ))
                .body("violations.message", hasItems(
                        containsString("size must be between 10 and 12"),
                        containsString("must be a well-formed email address")
                ));
    }

    @Test
    public void testGetMember() {
        // First create a member
        String id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Jane Doe",
                            "email": "jane@example.com",
                            "phoneNumber": "1234567890"
                        }
                        """)
                .when()
                .post("/api/members")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getString("id");

        // Then retrieve it
        given()
                .when()
                .get("/api/members/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Jane Doe"))
                .body("email", equalTo("jane@example.com"));
    }
}