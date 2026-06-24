package com.stud.orders.reviews.service;

import com.stud.orders.reviews.api.HunterRatedEvent;

public interface HunterReviewEventPublisher {

    void publish(HunterRatedEvent event);
}
