package ru.netology.base;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.data.DataHelper;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHelper {
    private static String url = "jdbc:mysql://localhost:3306/";
    private static String nameDB = "mydb";
    private static String user = "user";
    private static String password = "pass";

    private DBHelper() {
    }

    @SneakyThrows
    public static void cleanAllTables() {
        var runner = new QueryRunner();
        try (
                var conn = DriverManager.getConnection(url + nameDB, user, password);
        ) {
            var cleanUsers = "DELETE FROM users;";
            var cleanCards = "DELETE FROM cards;";
            var cleanCardTransactions = "DELETE FROM card_transactions;";
            var cleanAuthCodes = "DELETE FROM auth_codes;";

            runner.update(conn, cleanCardTransactions);
            runner.update(conn, cleanAuthCodes);
            runner.update(conn, cleanCards);
            runner.update(conn, cleanUsers);
        }
    }

    @SneakyThrows
    public static String getUserLogin(int idUser) {
        var runner = new QueryRunner();
        try (
                var conn = DriverManager.getConnection(url + nameDB, user, password);
        ) {
            var userSQL = "SELECT login FROM users WHERE id = " + idUser;
            String userName = runner.query(conn, userSQL, new ScalarHandler<>());
            return userName;
        }
    }

    @SneakyThrows
    public static String getVerificationCode(int idUser) {
        var runner = new QueryRunner();
        try (
                var conn = DriverManager.getConnection(url + nameDB, user, password);
        ) {
            var codeSQL = "SELECT code FROM auth_codes WHERE user_id = " + idUser +
                    " ORDER BY created DESC LIMIT 1;";
            String verificationCode = runner.query(conn, codeSQL, new ScalarHandler<>());
            return verificationCode;
        }
    }

    @SneakyThrows
    public static void addUserInDatabase(int id, String pass) {
        var runner = new QueryRunner();
        try (
                var conn = DriverManager.getConnection(url + nameDB, user, password);
        ) {
            var dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";
            runner.update(conn, dataSQL, id, DataHelper.getRandomLogin(), pass);
        }
    }
}
