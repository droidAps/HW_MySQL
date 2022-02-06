package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class PasswordInfo {
        private String password;
        private String encryptedPassword;
    }

    public static PasswordInfo getFirstPassword() {
        return new PasswordInfo("qwerty123", "$2a$10$HBheUFg1Rj4v7uDunGiCte9aezeh3TLmclHRC9Bln9QudvS6sdXDi");
    }

    public static PasswordInfo getSecondPassword() {
        return new PasswordInfo("123qwerty", "$2a$10$DuxVMvnTDIfjxrPWUeGLl.6kJtteZ1W1xfgtzk1DIgElOWVdocJ7C");
    }

    public static String getRandomLogin() {
        var faker = new Faker();
        return faker.name().username();
    }

    public static String getInvalidCode() {
        var faker = new Faker();
        return faker.number().digits(4);
    }
}
