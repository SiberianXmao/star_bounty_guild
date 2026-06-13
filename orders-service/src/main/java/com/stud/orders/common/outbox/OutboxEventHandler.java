package com.stud.orders.common.outbox;

public interface OutboxEventHandler {

    String eventType();

    void handle(String payload);
}