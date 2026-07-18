package com.arsalan.tenanttable.order.enums;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    SERVED,
    PAID,
    COMPLETED,
    CANCELLED,
    REJECTED;

    public boolean isTerminal() {
        return this == COMPLETED
                || this == CANCELLED
                || this == PAID;
    }
}
