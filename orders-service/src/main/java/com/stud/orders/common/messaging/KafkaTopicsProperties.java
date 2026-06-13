package com.stud.orders.common.messaging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicsProperties {

    private String applicationAccepted = "bounty.applications.application-accepted.v1";
}
