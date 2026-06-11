package com.stud.backend.common.outbox;

public interface OutboxEventHandler {

    String eventType();

    void handle(String payload);
}