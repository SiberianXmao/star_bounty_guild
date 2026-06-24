package com.stud.profiles.rating.messaging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka.topics")
public class RatingTopicsProperties {

    private String hunterRated = "bounty.reviews.hunter-rated.v1";
}
