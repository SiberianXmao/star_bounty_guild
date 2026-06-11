package com.stud.backend.common.outbox;

public enum OutboxEventStatus {
    PENDING,
    PROCESSED,
    FAILED
}
