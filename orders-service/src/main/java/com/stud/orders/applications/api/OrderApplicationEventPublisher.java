package com.stud.orders.applications.api;

public interface OrderApplicationEventPublisher {

    void publish(ApplicationAcceptedEvent event);
}