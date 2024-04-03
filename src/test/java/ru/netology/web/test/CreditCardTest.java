package ru.netology.web.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.web.data.DataHelper;
import ru.netology.web.data.SQLHelper;
import ru.netology.web.page.TravelPurchasePage;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.netology.web.data.SQLHelper.cleanDatabase;

public class CreditCardTest {
    private static SQLHelper.CreditRequestEntity credit;
    private static SQLHelper.OrderEntity order;
    private static String url = System.getProperty("app.url");

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open(url);
    }

    @AfterEach
    public void cleanData() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Credit card payment with the APPROVED status")
    void shouldSuccessPayByApprovedCreditCard() {

        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationOk();

        credit = SQLHelper.getCreditRequestEntity();
        order = SQLHelper.getOrderEntity();
        assertAll(() -> assertEquals(status, credit.getStatus()),
                () -> assertEquals(credit.getBank_id(), order.getPayment_id()),
                () -> assertEquals(credit.getId(), order.getCredit_id()));

    }

    @Test
    @DisplayName("Credit card payment with the DECLINED status")
    void shouldCancelPayByDeclinedCreditCard() {
        String status = "DECLINED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        assertAll(() -> page.waitNotificationError(),

                () -> assertEquals(status, SQLHelper.getCreditRequestEntity().getStatus()));
    }

    @Test
    @DisplayName("Empty fields in the form credit")
    void shouldDisplayMessageUnderFieldsCredit() {
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.clickContinue();
        assertAll(() -> page.waitNotificationMessageNumber("Поле обязательно для заполнения"),
                () -> page.waitNotificationMessageMonth("Поле обязательно для заполнения"),
                () -> page.waitNotificationMessageYear("Поле обязательно для заполнения"),
                () -> page.waitNotificationMessageOwner("Поле обязательно для заполнения"),
                () -> page.waitNotificationMessageCVC("Поле обязательно для заполнения"));
    }

    @Test
    @DisplayName("Credit card payment with the INVALID status")
    void shouldCancelPayByInvalidCreditCard() {
        String status = "INVALID";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        assertAll(() -> page.waitNotificationError(),
                () -> assertNull(SQLHelper.getOrderEntity(), "Таблица order_entity не пустая"),
                () -> assertNull(SQLHelper.getCreditRequestEntity(), "Таблица credit_request_entity не пустая"));
    }

    @Test
    @DisplayName("Credit card payment with the ZERO status")
    void shouldCancelPayByZeroDebitCard() {
        String status = "ZERO";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        assertAll(() -> page.waitNotificationError(),
                () -> assertNull(SQLHelper.getOrderEntity(), "Таблица order_entity не пустая"),
                () -> assertNull(SQLHelper.getCreditRequestEntity(), "Таблица credit_request_entity не пустая"));
    }

    @Test
    @DisplayName("Credit card payment with the FIFTEEN status")
    void shouldCancelPayByFifteenDigCreditCard() {
        String status = "FIFTEEN";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageNumber("Неверный формат");
    }

    @Test
    @DisplayName("Credit card payment with zero month")
    void shouldErrorZeroMonthCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getZero());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверно указан срок действия карты");

    }

    @Test
    @DisplayName("Credit card payment exceeding range the month field")
    void shouldErrorOverMonthCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOver());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Credit card payment with one digit in month field")
    void shouldErrorOneDigitMonthCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOneDig());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверный формат");

    }

    @Test
    @DisplayName("Credit card payment with zero year field")
    void shouldErrorZeroYearCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.getZero());
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Credit card payment with exceeding range the year field")
    void shouldErrorOverYearCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(6));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Credit card payment with previous year")
    void shouldErrorLessYearCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(-1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Credit card payment with cyrillic name field")
    void shouldErrorCyrillicNameCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderCyrillic());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Credit card payment with numeric name field")
    void shouldErrorNumericNameCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderNumeric());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Credit card payment with one symbol name field")
    void shouldErrorOneSymbolNameCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderOneSymbol());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Credit card payment with special characters name field")
    void shouldErrorSpecCharNameCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderSpecChar(6));
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Credit card payment with two digits in CVC field")
    void shouldErrorTwoDigCVCForPaymentCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(2);
        page.clickContinue();
        page.waitNotificationMessageCVC("Неверный формат");
    }

    @Test
    @DisplayName("Credit card payment with one digit in CVC field")
    void shouldErrorOneDigCVCForPaymentCredit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(1);
        page.clickContinue();
        page.waitNotificationMessageCVC("Неверный формат");
    }
}
