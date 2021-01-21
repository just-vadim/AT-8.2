package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.util.Random;
import java.util.stream.Collectors;

import static ru.netology.data.SQLHelper.getCardBalance;
import static ru.netology.data.SQLHelper.getVerificationCode;

public class DataHelper {

    private DataHelper(){}

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    @Value
    public static class VerificationInfo {
        String login;
        String code;
    }

    @Value
    public static class Transaction {
        String from;
        String to;
        int amount;
    }

    @Value
    public static class Card {
        String id;
        String number;
        int balance;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static VerificationInfo getVerificationInfo(AuthInfo info) {
        return new VerificationInfo(info.getLogin(), getVerificationCode(info));
    }

    public static Transaction getTransaction(String from, String to, int amount) {
        return new Transaction(from, to, amount);
    }

    public static AuthInfo getInvalidAuthInfoWithInvalidLogin(){
        Faker faker = new Faker();
        return new AuthInfo(faker.name().firstName(),"qwerty123");
    }

    public static AuthInfo getInvalidAuthInfoWithInvalidPassword(){
        Faker faker = new Faker();
        return new AuthInfo("vasya", faker.internet().password(8,12, true, false, true));
    }

    public static String getBadFormatVerificationCode() {
        Faker faker = new Faker();
        return faker.numerify("###");
    }

    public static String getInvalidVerificationCode() {
        Faker faker = new Faker();
        return faker.numerify("######");
    }

    public static String getBadFormatToken() {
        String symbolsLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String symbols = symbolsLowerCase.toUpperCase() + symbolsLowerCase + "." + "1234567890";
        return new Random().ints(100, 0, symbols.length())
                .mapToObj(symbols::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    public static String getInvalidToken() {
        String symbolsLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String symbols = symbolsLowerCase.toUpperCase() + symbolsLowerCase + ".";
        return new Random().ints(104, 0, symbols.length())
                .mapToObj(symbols::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    public static String getFirstCardNumber() {
        return "5559 0000 0000 0001";
    }

    public static String getSecondCardNumber() {
        return "5559 0000 0000 0002";
    }

    public static int getFirstCardBalance() {
        return getCardBalance(getFirstCardNumber());
    }

    public static int getSecondCardBalance() {
        return getCardBalance(getSecondCardNumber());
    }

    public static int getValidTransactionAmount(String cardNumber) {
        int cardBalance = getCardBalance(cardNumber);
        Random random = new Random();
        return random.nextInt(cardBalance) + 1;
    }
}