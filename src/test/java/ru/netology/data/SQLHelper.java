package ru.netology.data;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.*;

public class SQLHelper {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    public static void cleanDB() {
        String auth_codes = "DELETE FROM auth_codes";
        String card_transactions = "DELETE FROM card_transactions";
        String cards = "DELETE FROM cards";
        String users = "DELETE FROM users";
        QueryRunner runner = new QueryRunner();
        try (Connection connection = getConnection()) {
            runner.update(connection, auth_codes);
            runner.update(connection, card_transactions);
            runner.update(connection, cards);
            runner.update(connection, users);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static String getVerificationCode(DataHelper.AuthInfo info) {
        String login = info.getLogin();
        String verificationCodeQuery =
                "SELECT auth_codes.code" + " " +
                "FROM users INNER JOIN auth_codes ON users.id=auth_codes.user_id" + " " +
                "WHERE users.id =" + " " +
                    "(SELECT id" + " " +
                    "FROM users" + " " +
                    "WHERE login = ?)" + " " +
                "ORDER BY auth_codes.created DESC LIMIT 1;";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(verificationCodeQuery);
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("code");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public static String getUserStatus(DataHelper.AuthInfo info) {
        String login = info.getLogin();
        String userStatusQuery =
                "SELECT status" + " " +
                "FROM users" + " " +
                "WHERE login = ?;";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(userStatusQuery);
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("status");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public static void blockUser(DataHelper.AuthInfo info) {
        String login = info.getLogin();
        String blockUserQuery =
                "UPDATE users" + " " +
                "SET status = 'blocked'" + " " +
                "WHERE login = ?;";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(blockUserQuery);
            preparedStatement.setString(1, login);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static int getCardBalance(String cardNumber) {
        String getCardNumberQuery =
                "SELECT balance_in_kopecks" + " " +
                "FROM cards" + " " +
                "WHERE number = ?;";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(getCardNumberQuery);
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("balance_in_kopecks") / 100;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return 0;
    }
}