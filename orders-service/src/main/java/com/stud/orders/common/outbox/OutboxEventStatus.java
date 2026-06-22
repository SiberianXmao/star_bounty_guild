package com.stud.orders.common.outbox;

public enum OutboxEventStatus {
    PENDING,
    PROCESSED,
    FAILED
}
