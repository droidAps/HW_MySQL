package ru.netology.sql;

import ru.netology.clean.CleanTables;
import ru.netology.data.DataHelper;
import ru.netology.data.User;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.junit.jupiter.api.*;
import ru.netology.page.LoginPage;

import java.sql.DriverManager;

import static com.codeborne.selenide.Selenide.open;

public class DataBaseTest {

    @BeforeEach
    @SneakyThrows
    void setUp() {
        var faker = new Faker();
        var runner = new QueryRunner();
        var firstPass = DataHelper.getFirstPassword();
        var secondPass = DataHelper.getSecondPassword();
        var dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb", "user", "pass"
                );

        ) { // удаление данных из таблиц БД
            CleanTables.cleanAllTables(conn, runner);

            // вставка тестовых данных
            runner.update(conn, dataSQL, 1, faker.name().username(), firstPass.getEncryptedPassword());
            runner.update(conn, dataSQL, 2, faker.name().username(), secondPass.getEncryptedPassword());
            runner.update(conn, dataSQL, 3, faker.name().username(), firstPass.getEncryptedPassword());
            runner.update(conn, dataSQL, 4, faker.name().username(), firstPass.getEncryptedPassword());
            runner.update(conn, dataSQL, 5, faker.name().username(), secondPass.getEncryptedPassword());
        }
    }

    @BeforeEach
    void openURL() {
        open("http://localhost:9999");
    }

    @Test
    @SneakyThrows
    void authorizationTest() {
        int idUser = 5;
        var pass = DataHelper.getSecondPassword().getPassword();
        var userSQL = "SELECT login FROM users WHERE id = " + idUser;
        var codeSQL = "SELECT code FROM auth_codes WHERE user_id = " + idUser +
                " ORDER BY created DESC LIMIT 1;";
        var runner = new QueryRunner();

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb", "user", "pass"
                );
        ) {
            String userName = runner.query(conn, userSQL, new ScalarHandler<>());
            var user = new User(idUser, userName, pass);

            var loginPage = new LoginPage();
            var verificationPage = loginPage.validLogin(user);
            String verificationCode = runner.query(conn, codeSQL, new ScalarHandler<>());
            var dashboardPage = verificationPage.validVerify(verificationCode);
        }
    }

    @Test
    @SneakyThrows
    void errorMessageTest() {
        int idUser = 3;
        var pass = DataHelper.getFirstPassword().getPassword();
        var userSQL = "SELECT login FROM users WHERE id = " + idUser;
        var codeSQL = "SELECT code FROM auth_codes WHERE user_id = " + idUser +
                " ORDER BY created DESC LIMIT 1;";
        var runner = new QueryRunner();

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb", "user", "pass"
                );
        ) {
            String userName = runner.query(conn, userSQL, new ScalarHandler<>());
            var user = new User(idUser, userName, pass);

            var loginPage = new LoginPage();
            InvalidMethods.incorrectInput3Times(loginPage, user);
            var verificationPage = loginPage.validLogin(user);
            String verificationCode = runner.query(conn, codeSQL, new ScalarHandler<>());
            verificationPage.checkError(verificationCode);
        }
    }
}
