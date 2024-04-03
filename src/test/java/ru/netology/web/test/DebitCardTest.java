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

public class DebitCardTest {
    private static SQLHelper.PaymentEntity payment;
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
    @DisplayName("Debit card payment with the APPROVED status")
    void shouldSuccessPayByApprovedDebitCard() {

        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        int price = page.getPriceInKops();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationOk();

        payment = SQLHelper.getPaymentEntity();
        order = SQLHelper.getOrderEntity();
        assertAll(() -> assertEquals(status, payment.getStatus()),
                () -> assertEquals(price, payment.getAmount()),
                () -> assertEquals(payment.getTransaction_id(), order.getPayment_id()));

    }

    @Test
    @DisplayName("Debit card payment with the DECLINED status")
    void shouldCancelPayByDeclinedDebitCard() {
        String status = "DECLINED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        assertAll(() -> page.waitNotificationError(),

                () -> assertEquals(status, SQLHelper.getPaymentEntity().getStatus()));
    }

    @Test
    @DisplayName("Empty fields in the form debit")
    void shouldDisplayMessageUnderFieldsDebit() {
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.clickContinue();
        assertAll(() -> page.waitNotificationMessageNumber("Поле обязательно для заполнения"),
                () -> page.waitNotificationMessageMonth("Поле обязательно для заполнения"),
                () -> page.waitNotificationMessageYear("Поле обязательно для заполнения"),
                () -> page.waitNotificationMessageOwner("Поле обязательно для заполнения"),
                () -> page.waitNotificationMessageCVC("Поле обязательно для заполнения"));
    }

    @Test
    @DisplayName("Debit card payment with the INVALID status")
    void shouldCancelPayByInvalidDebitCard() {
        String status = "INVALID";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        assertAll(() -> page.waitNotificationError(),
                () -> assertNull(SQLHelper.getOrderEntity(), "Таблица order_entity не пустая"),
                () -> assertNull(SQLHelper.getPaymentEntity(), "Таблица payment_entity не пустая"));
    }

    @Test
    @DisplayName("Debit card payment with the ZERO status")
    void shouldCancelPayByZeroDebitCard() {
        String status = "ZERO";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        assertAll(() -> page.waitNotificationError(),
                () -> assertNull(SQLHelper.getOrderEntity(), "Таблица order_entity не пустая"),
                () -> assertNull(SQLHelper.getPaymentEntity(), "Таблица payment_entity не пустая"));
    }

    @Test
    @DisplayName("Debit card payment with the FIFTEEN status")
    void shouldCancelPayByFifteenDigDebitCard() {
        String status = "FIFTEEN";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageNumber("Неверный формат");
    }

    @Test
    @DisplayName("Debit card payment with zero month")
    void shouldErrorZeroMonthDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getZero());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверно указан срок действия карты");

    }

    @Test
    @DisplayName("Debit card payment exceeding range the month field")
    void shouldErrorOverMonthDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOver());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Debit card payment with one digit in month field")
    void shouldErrorOneDigitMonthDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOneDig());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверный формат");

    }

    @Test
    @DisplayName("Debit card payment with zero year field")
    void shouldErrorZeroYearDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.getZero());
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Debit card payment with exceeding range the year field")
    void shouldErrorOverYearDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(6));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Debit card payment with previous year")
    void shouldErrorLessYearDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(-1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Debit card payment with cyrillic name field")
    void shouldErrorCyrillicNameDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderCyrillic());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Debit card payment with numeric name field")
    void shouldErrorNumericNameDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderNumeric());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Debit card payment with one symbol name field")
    void shouldErrorOneSymbolNameDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderOneSymbol());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Debit card payment with special characters name field")
    void shouldErrorSpecCharNameDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderSpecChar(6));
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Debit card payment with two digits in CVC field")
    void shouldErrorTwoDigCVCForPaymentDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(2);
        page.clickContinue();
        page.waitNotificationMessageCVC("Неверный формат");
    }

    @Test
    @DisplayName("Debit card payment with one digit in CVC field")
    void shouldErrorOneDigCVCForPaymentDebit() {
        String status = "APPROVED";
        TravelPurchasePage page = new TravelPurchasePage();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(1);
        page.clickContinue();
        page.waitNotificationMessageCVC("Неверный формат");
    }
}
