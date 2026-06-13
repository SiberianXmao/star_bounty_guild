package com.stud.orders.common.messaging;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class KafkaMessagingConfiguration {

    @Bean
    KafkaAdmin.NewTopics bountyTopics(KafkaTopicsProperties topicsProperties) {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(topicsProperties.getApplicationAccepted())
                        .partitions(1)
                        .replicas(1)
                        .build()
        );
    }
}
