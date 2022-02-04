package ru.netology.sql;

import com.github.javafaker.Faker;
import ru.netology.data.User;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class InvalidMethods {
    private InvalidMethods() {
    }

    public static void incorrectInput3Times(LoginPage loginPage, User user) {
        Faker faker = new Faker();
        for (int i = 0; i < 3; i++) {
            var verificationPage = loginPage.validLogin(user);
            verificationPage.invalidVerify(faker.number().digits(4));
            open("http://localhost:9999");
        }
    }
}
