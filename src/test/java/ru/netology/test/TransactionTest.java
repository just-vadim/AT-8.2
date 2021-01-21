package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.APIHelper.*;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.cleanDB;

public class TransactionTest {

    @BeforeAll
    static void setUpAll(){
        setUpCommonRequestSpec();
    }

    @AfterAll
    static void cleanDataBase() {
        cleanDB();
    }

    @Test
    void shouldMakeTransactionFromFirstCardToSecond() {
        DataHelper.AuthInfo info = getAuthInfo();
        loginRequest(info, 200);
        String token = verificationRequest(info, 200);
        int amount = getValidTransactionAmount(getFirstCardNumber());
        int expectedFirstCardBalance = getFirstCardBalance() - amount;
        int expectedSecondCardBalance = getSecondCardBalance() + amount;
        loginRequest(info, 200);
        makeTransaction(getFirstCardNumber(),
                getSecondCardNumber(),
                amount,
                token);
        int actualFirstCardBalance = getFirstCardBalance();
        int actualSecondCardBalance = getSecondCardBalance();
        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldMakeTransactionFromSecondCardToFirst() {
        DataHelper.AuthInfo info = getAuthInfo();
        loginRequest(info, 200);
        String token = verificationRequest(info, 200);
        int amount = getValidTransactionAmount(getSecondCardNumber());
        int expectedFirstCardBalance = getFirstCardBalance() + amount;
        int expectedSecondCardBalance = getSecondCardBalance() - amount;
        loginRequest(info, 200);
        makeTransaction(getSecondCardNumber(),
                getFirstCardNumber(),
                amount,
                token);
        int actualFirstCardBalance = getFirstCardBalance();
        int actualSecondCardBalance = getSecondCardBalance();
        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldMakeTransactionToOwnSameCard() {
        DataHelper.AuthInfo info = getAuthInfo();
        loginRequest(info, 200);
        String token = verificationRequest(info, 200);
        int amount = getValidTransactionAmount(getSecondCardNumber());
        int expectedSecondCardBalance = getSecondCardBalance();
        loginRequest(info, 200);
        makeTransaction(getSecondCardNumber(),
                getSecondCardNumber(),
                amount,
                token);
        int actualSecondCardBalance = getSecondCardBalance();
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }
}