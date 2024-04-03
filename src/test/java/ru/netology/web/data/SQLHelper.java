package ru.netology.web.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;


public class SQLHelper {
    private static QueryRunner runner = new QueryRunner();

    public SQLHelper() {
    }

    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");


    @SneakyThrows
    private static Connection getConn() {
        return DriverManager.getConnection(url, user, password);
    }
    @SneakyThrows
    public static void cleanDatabase() {
        Connection conn = getConn();
        runner.execute(conn, "DELETE FROM payment_entity");
        runner.execute(conn, "DELETE FROM credit_request_entity");
        runner.execute(conn, "DELETE FROM order_entity");
    }

    @SneakyThrows
    public static SQLHelper.PaymentEntity getPaymentEntity() {
        String codeSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(SQLHelper.PaymentEntity.class));
    }

    @SneakyThrows
    public static SQLHelper.CreditRequestEntity getCreditRequestEntity() {
        String codeSQL = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(SQLHelper.CreditRequestEntity.class));
    }

    @SneakyThrows
    public static SQLHelper.OrderEntity getOrderEntity() {
        String codeSQL = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(SQLHelper.OrderEntity.class));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentEntity {
        private String id;
        private int amount;
        private Timestamp created;
        private String status;
        private String transaction_id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditRequestEntity {
        private String id;
        private String bank_id;
        private Timestamp created;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderEntity {
        private String id;
        private Timestamp created;
        private String credit_id;
        private String payment_id;
    }

}
