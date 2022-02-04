package ru.netology.clean;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;

public class CleanTables {
    private CleanTables() {
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
}
