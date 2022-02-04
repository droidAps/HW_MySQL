package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.time.Duration;

public class VerificationPage {
    private SelenideElement codeField = $("[data-test-id=code] input");
    private SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private SelenideElement errorMessage = $("[data-test-id=error-notification] .notification__content");

    public VerificationPage() {
        codeField.shouldBe(visible);
    }


    public DashboardPage validVerify(String verificationCode) {
        codeField.setValue(verificationCode);
        verifyButton.click();
        return new DashboardPage();
    }

    public void invalidVerify(String code) {
        codeField.setValue(code);
        verifyButton.click();
    }

    public void checkError(String verificationCode) {
        codeField.setValue(verificationCode);
        verifyButton.click();
        errorMessage.shouldBe(visible, Duration.ofSeconds(3))
                .shouldHave(text("Ошибка! Превышено количество попыток ввода кода!"));
    }
}