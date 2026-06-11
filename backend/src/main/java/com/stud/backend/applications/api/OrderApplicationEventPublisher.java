package com.stud.backend.applications.api;

public interface OrderApplicationEventPublisher {

    void publish(ApplicationAcceptedEvent event);
}