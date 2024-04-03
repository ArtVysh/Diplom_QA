package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TravelPurchasePage {

    private SelenideElement heading = $$("h2").findBy(Condition.text("Путешествие дня"));
    private SelenideElement buyButton = $$("button").findBy(Condition.text("Купить"));
    private SelenideElement buyInCreditButton = $$("button").findBy(Condition.text("Купить в кредит"));
    private SelenideElement buy = $$("h3").findBy(Condition.text("Оплата по карте"));
    private SelenideElement buyInCredit = $$("h3").findBy(Condition.text("Кредит по данным карты"));
    private ElementsCollection formFields = $$(".input_has-label");
    private SelenideElement numberCard = $("input[placeholder=\"0000 0000 0000 0000\"]");
    private SelenideElement month = $("input[placeholder=\"08\"]");
    private SelenideElement year = $("input[placeholder=\"22\"]");
    private SelenideElement cardOwner = formFields.findBy(Condition.text("Владелец")).$(".input__control");
    private SelenideElement CVC = $("input[placeholder=\"999\"]");
    private SelenideElement continueButton = $$("button").findBy(Condition.text("Продолжить"));
    private SelenideElement notificationOk = $(".notification_status_ok");
    private SelenideElement notificationError = $(".notification_status_error");

    private SelenideElement ownerField = formFields.findBy(Condition.text("Владелец"));
    private SelenideElement numberField = formFields.findBy(Condition.text("Номер карты"));
    private SelenideElement monthField = formFields.findBy(Condition.text("Месяц"));
    private SelenideElement yearField = formFields.findBy(Condition.text("Год"));
    private SelenideElement cvcField = formFields.findBy(Condition.text("CVC/CVV"));


    public TravelPurchasePage() {
        heading.shouldBe(visible);
        buyButton.shouldBe(visible);
        buyInCreditButton.shouldBe(visible);
    }

    public void buy() {
        buyButton.click();
        buy.shouldBe(visible);

    }

    public void buyInCredit() {
        buyInCreditButton.click();
        buyInCredit.shouldBe(visible);
    }

    public int getPriceInKops() {
        String[] str = $$("li").findBy(Condition.text("руб")).getText().split(" ");
        return Integer.parseInt(str[1] + str[2]) * 100;

    }

    public void inputNumberCard(String status) {
        numberCard.setValue(DataHelper.getCardNumberByStatus(status));
    }

    public void inputMonth(String value) {
        month.setValue(value);
    }

    public void inputYear(String value) {
        year.setValue(value);
    }

    public void inputOwner(String value) {
        cardOwner.setValue(value);
    }

    public void inputCVC(int amount) {
        CVC.setValue(DataHelper.generateCVC(amount));
    }

    public void clickContinue() {
        continueButton.click();
    }

    public void waitNotificationOk() {
        notificationOk.shouldBe(visible, Duration.ofSeconds(18));
    }

    public void waitNotificationError() {
        notificationError.shouldBe(visible, Duration.ofSeconds(18));
    }

    public void waitNotificationMessageOwner(String message) {
        ownerField.shouldBe(Condition.text(message));
    }

    public void waitNotificationMessageNumber(String message) {
        numberField.shouldBe(Condition.text(message));
    }

    public void waitNotificationMessageMonth(String message) {
        monthField.shouldBe(Condition.text(message));
    }

    public void waitNotificationMessageYear(String message) {
        yearField.shouldBe(Condition.text(message));
    }

    public void waitNotificationMessageCVC(String message) {
        cvcField.shouldBe(Condition.text(message));
    }


}
