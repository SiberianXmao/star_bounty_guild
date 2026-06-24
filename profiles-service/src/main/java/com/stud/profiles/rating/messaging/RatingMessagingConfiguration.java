package com.stud.profiles.rating.messaging;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RatingTopicsProperties.class)
public class RatingMessagingConfiguration {
}
