package ru.netology.data;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import static io.restassured.RestAssured.given;
import static ru.netology.data.DataHelper.*;

public class APIHelper {

    @Value
    static class EndPoints {
        public static final String auth = "/api/auth";
        public static final String verification = "/api/auth/verification";
        public static final String cards = "/api/cards";
        public static final String transfer = "/api/transfer";
    }

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
        .setBaseUri("http://localhost/")
        .setPort(9999)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build();

    public static void setUpCommonRequestSpec() {
        RestAssured.requestSpecification = requestSpec;
    }

    public static void loginRequest(DataHelper.AuthInfo info, int expectStatusCode) {
        given()
            .body(info)
            .when()
            .post(EndPoints.auth)
            .then()
            .statusCode(expectStatusCode);
    }

    public static String verificationRequest(AuthInfo info, int expectStatusCode) {
        return given()
            .body(getVerificationInfo(info))
            .when()
            .post(EndPoints.verification)
            .then()
            .statusCode(expectStatusCode)
            .extract()
            .path("token");
    }

    public static void verificationRequestBadFormatCode(AuthInfo info, int expectStatusCode) {
        loginRequest(info, 200);
        given()
            .body(getBadFormatVerificationCode())
            .when()
            .post(EndPoints.verification)
            .then()
            .statusCode(expectStatusCode);
    }

    public static String verificationRequestInvalidCode(AuthInfo info, int expectStatusCode) {
        loginRequest(info, 200);
        String code = getVerificationInfo(info).getCode();
        String invalidCode = getInvalidVerificationCode();
        while (invalidCode.equals(code)) {
            invalidCode = getInvalidVerificationCode();
        }
        return given()
            .body(invalidCode)
            .when()
            .post(EndPoints.verification)
            .then()
            .statusCode(expectStatusCode)
            .extract()
            .path("token");
    }

    public static void verificationRequestEmptyCode(AuthInfo info, int expectStatusCode) {
        loginRequest(info, 200);
        given()
            .body("")
            .when()
            .post(EndPoints.verification)
            .then()
            .statusCode(expectStatusCode);
    }

    public static String getCardsRequest(AuthInfo info, String token, int expectedStatusCode) {
        loginRequest(info, 200);
        Response response = given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get(EndPoints.cards)
            .then()
            .statusCode(expectedStatusCode)
            .extract()
            .response();
        return response.getBody().asString();
    }

    public static void makeTransaction(String from, String to, int amount, String token) {
        given()
            .header("Authorization", "Bearer " + token)
            .body(DataHelper.getTransaction(from, to , amount))
            .when()
            .post(EndPoints.transfer)
            .then();
    }
}