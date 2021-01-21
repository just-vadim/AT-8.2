package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.APIHelper.*;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.*;

public class AuthTest {

    @BeforeAll
    static void setUpAll(){
        setUpCommonRequestSpec();
    }

    @AfterAll
    static void cleanDataBase() {
//        cleanDB();
    }

    @Test
    void shouldLogin() {
        loginRequest(getAuthInfo(), 200);
    }

    @Test
    void shouldNotLoginIfPasswordInvalid() {
        loginRequest(getInvalidAuthInfoWithInvalidPassword(), 400);
    }

    @Test
    void shouldNotLoginIfLoginInvalid() {
        loginRequest(getInvalidAuthInfoWithInvalidLogin(), 400);
    }

    @Test /*Bug*/
    void shouldBlockUserIfLoginThreeTimesWithInvalidPassword() {
        String startExpectedStatus = "active";
        String startActualStatus = getUserStatus(getAuthInfo());
        assertEquals(startExpectedStatus, startActualStatus);
        loginRequest(getInvalidAuthInfoWithInvalidPassword(), 400);
        loginRequest(getInvalidAuthInfoWithInvalidPassword(), 400);
        loginRequest(getInvalidAuthInfoWithInvalidPassword(), 400);
        String finalExpectedStatus = "blocked";
        String finalActualStatus = getUserStatus(getAuthInfo());
        assertEquals(finalExpectedStatus, finalActualStatus);
    }

    @Test /*Bug*/
    void shouldNotLoginIfUserBlocked() {
        blockUser(getAuthInfo());
        String startExpectedStatus = "blocked";
        String startActualStatus = getUserStatus(getAuthInfo());
        assertEquals(startExpectedStatus, startActualStatus);
        loginRequest(getInvalidAuthInfoWithInvalidPassword(), 403);
    }

    @Test
    void shouldVerifyByOneTimePassword() {
        AuthInfo info = getAuthInfo();
        loginRequest(info, 200);
        verificationRequest(info,200);
    }

    @Test
    void shouldNotVerifyIfOneTimePasswordBadFormat() {
        verificationRequestBadFormatCode(getAuthInfo(), 500);
    }

    @Test /*Bug*/
    void shouldNotVerifyIfInvalidOneTimePassword() {
        verificationRequestInvalidCode(getAuthInfo(),403);
    }

    @Test
    void shouldNotVerifyIfEmptyOneTimePassword() {
        verificationRequestEmptyCode(getAuthInfo(), 500);
    }

    @Test
    void shouldGetCardList() {
        getCardsRequest(getAuthInfo(), verificationRequest(getAuthInfo(),200), 200);
    }

    @Test
    void shouldNotGetCardListIfTokenBadFormat() {
        getCardsRequest(getAuthInfo(), getBadFormatToken(), 401);
    }

    @Test
    void shouldNotGetCardListIfInvalidToken() {
        getCardsRequest(getAuthInfo(), getInvalidToken(), 401);
    }

    @Test
    void shouldNotGetCardListIfEmptyToken() {
        getCardsRequest(getAuthInfo(), "", 401);
    }
}