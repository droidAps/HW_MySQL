package ru.netology.base;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.data.DataHelper;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHelper {
    private DBHelper() {
    }

    @SneakyThrows
    public static void cleanAllTables(Connection conn, QueryRunner runner) {
        var cleanUsers = "DELETE FROM users;";
        var cleanCards = "DELETE FROM cards;";
        var cleanCardTransactions = "DELETE FROM card_transactions;";
        var cleanAuthCodes = "DELETE FROM auth_codes;";

        runner.update(conn, cleanCardTransactions);
        runner.update(conn, cleanAuthCodes);
        runner.update(conn, cleanCards);
        runner.update(conn, cleanUsers);
    }

    @SneakyThrows
    public static String getUserLogin(Connection conn, QueryRunner runner, int idUser) {
        var userSQL = "SELECT login FROM users WHERE id = " + idUser;
        String userName = runner.query(conn, userSQL, new ScalarHandler<>());
        return userName;
    }

    @SneakyThrows
    public static String getVerificationCode(Connection conn, QueryRunner runner, int idUser) {
        var codeSQL = "SELECT code FROM auth_codes WHERE user_id = " + idUser +
                " ORDER BY created DESC LIMIT 1;";
        String verificationCode = runner.query(conn, codeSQL, new ScalarHandler<>());
        return verificationCode;
    }

    @SneakyThrows
    public static void addUserInDatabase(Connection conn, QueryRunner runner, int id, String password) {
        var dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";
        runner.update(conn, dataSQL, id, DataHelper.getRandomLogin(), password);
    }

    @SneakyThrows
    public static Connection connectInDatabase(String database, String user, String password) {
        var conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + database, user, password);
        return conn;
    }
}
