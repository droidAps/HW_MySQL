package ru.netology.sql;

import ru.netology.base.DBHelper;
import ru.netology.data.DataHelper;
import ru.netology.data.User;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.*;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class DataBaseTest {

    @BeforeEach
    @SneakyThrows
    void setUp() {
        var runner = new QueryRunner();
        var firstPass = DataHelper.getFirstPassword().getEncryptedPassword();
        var secondPass = DataHelper.getSecondPassword().getEncryptedPassword();

        try (
                var conn = DBHelper.connectInDatabase("mydb", "user", "pass");

        ) { // удаление данных из таблиц БД
            DBHelper.cleanAllTables(conn, runner);

            // вставка тестовых данных
            DBHelper.addUserInDatabase(conn, runner, 1, firstPass);
            DBHelper.addUserInDatabase(conn, runner, 2, secondPass);
            DBHelper.addUserInDatabase(conn, runner, 3, firstPass);
            DBHelper.addUserInDatabase(conn, runner, 4, firstPass);
            DBHelper.addUserInDatabase(conn, runner, 5, secondPass);
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
        var runner = new QueryRunner();

        try (
                var conn = DBHelper.connectInDatabase("mydb", "user", "pass");
        ) {
            String userName = DBHelper.getUserLogin(conn, runner, idUser);
            var user = new User(idUser, userName, pass);

            var loginPage = new LoginPage();
            var verificationPage = loginPage.validLogin(user);
            String verificationCode = DBHelper.getVerificationCode(conn, runner, idUser);
            var dashboardPage = verificationPage.validVerify(verificationCode);
        }
    }

    @Test
    @SneakyThrows
    void errorMessageTest() {
        int idUser = 3;
        var pass = DataHelper.getFirstPassword().getPassword();
        var runner = new QueryRunner();

        try (
                var conn = DBHelper.connectInDatabase("mydb", "user", "pass");
        ) {
            String userName = DBHelper.getUserLogin(conn, runner, idUser);
            var user = new User(idUser, userName, pass);
            var loginPage = new LoginPage();

            // 3 раза выполняем попытку входа в л/к с невалидным верификационным кодом
            var verificationPage = loginPage.validLogin(user);
            verificationPage.invalidVerify(DataHelper.getInvalidCode());
            openURL();
            verificationPage = loginPage.validLogin(user);
            verificationPage.invalidVerify(DataHelper.getInvalidCode());
            openURL();
            verificationPage = loginPage.validLogin(user);
            verificationPage.invalidVerify(DataHelper.getInvalidCode());

            // выполняем попытку входа в л/к с валидным верификационным кодом
            openURL();
            verificationPage = loginPage.validLogin(user);
            String verificationCode = DBHelper.getVerificationCode(conn, runner, idUser);

            verificationPage.checkError(verificationCode);
        }
    }
}
