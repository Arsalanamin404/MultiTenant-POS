package com.arsalan.tenanttable.order.enums;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    SERVED,
    COMPLETED,
    CANCELLED,
    REJECTED;

    public boolean isTerminal() {
        return this == COMPLETED
                || this == CANCELLED
                || this == REJECTED;
    }
}
