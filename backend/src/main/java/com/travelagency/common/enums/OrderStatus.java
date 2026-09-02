package com.travelagency.common.enums;

public final class OrderStatus {
    public static final String WAIT_PAY = "WAIT_PAY";
    public static final String PAID_WAIT_CONFIRM = "PAID_WAIT_CONFIRM";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String TRAVELLING = "TRAVELLING";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";
    public static final String REFUND_APPLYING = "REFUND_APPLYING";
    public static final String REFUND_PROCESSING = "REFUND_PROCESSING";
    public static final String REFUNDED = "REFUNDED";
    public static final String REFUND_REJECTED = "REFUND_REJECTED";

    private OrderStatus() {
    }
}
