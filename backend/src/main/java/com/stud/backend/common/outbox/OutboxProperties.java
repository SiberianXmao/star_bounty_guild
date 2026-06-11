package com.stud.backend.common.outbox;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.outbox")
public class OutboxProperties {

    private long pollDelayMs = 5000;
    private int batchSize = 100;
    private int maxAttempts = 3;
    private int maxErrorLength = 2000;
}