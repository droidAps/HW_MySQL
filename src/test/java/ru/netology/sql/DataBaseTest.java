package ru.netology.sql;

import ru.netology.base.DBHelper;
import ru.netology.data.DataHelper;
import ru.netology.data.User;
import org.junit.jupiter.api.*;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class DataBaseTest {

    @BeforeEach
    void setUp() {
        var firstPass = DataHelper.getFirstPassword().getEncryptedPassword();
        var secondPass = DataHelper.getSecondPassword().getEncryptedPassword();

        DBHelper.cleanAllTables();

        // добавление тестовых пользователей в БД
        DBHelper.addUserInDatabase(1, firstPass);
        DBHelper.addUserInDatabase(2, secondPass);
        DBHelper.addUserInDatabase(3, firstPass);
        DBHelper.addUserInDatabase(4, firstPass);
        DBHelper.addUserInDatabase(5, secondPass);
    }

    @BeforeEach
    void openURL() {
        open("http://localhost:9999");
    }

    @Test
    void authorizationTest() {
        int idUser = 5;
        var pass = DataHelper.getSecondPassword().getPassword();
        String userName = DBHelper.getUserLogin(idUser);
        var user = new User(idUser, userName, pass);

        var loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(user);
        String verificationCode = DBHelper.getVerificationCode(idUser);
        var dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    void errorMessageTest() {
        int idUser = 3;
        var pass = DataHelper.getFirstPassword().getPassword();
        String userName = DBHelper.getUserLogin(idUser);
        var user = new User(idUser, userName, pass);

        // 3 раза выполняем попытку входа в л/к с невалидным верификационным кодом
        var loginPage = new LoginPage();
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
        String verificationCode = DBHelper.getVerificationCode(idUser);

        verificationPage.checkError(verificationCode);
    }
}
