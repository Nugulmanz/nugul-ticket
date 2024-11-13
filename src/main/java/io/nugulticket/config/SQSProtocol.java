package io.nugulticket.config;

public class SQSProtocol {
    // SQS 공통
    public static final String ATTRIBUTE_NAME_TYPE = "type";

    // 주문 관련
    public static final String ATTRIBUTE_NAME_AMOUNT = "amount";
    public static final String ATTRIBUTE_NAME_ORDER_ID = "orderId";
    public static final String ATTRIBUTE_NAME_TICKET_ID = "ticketId";
    public static final String ATTRIBUTE_NAME_PAYMENT_KEY = "paymentKey";
    public static final String ATTRIBUTE_NAME_USER_ID = "userId";

    public static final String ATTRIBUTE_NAME_MESSAGE = "message";

    public static String TYPE_PRE_ORDER = "PRE_ORDER";
    public static String TYPE_APPROVE_PAYMENT = "APPROVE_PAYMENT";
    public static String TYPE_SUCCESS_PAYMENT = "SUCCESS_PAYMENT";
    public static String TYPE_CANCEL_PAYMENT = "CANCEL_PAYMENT";

}
