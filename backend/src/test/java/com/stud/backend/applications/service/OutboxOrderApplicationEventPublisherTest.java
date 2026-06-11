package com.stud.backend.applications.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.backend.applications.api.ApplicationAcceptedEvent;
import com.stud.backend.common.outbox.OutboxEvent;
import com.stud.backend.common.outbox.OutboxEventRepository;
import com.stud.backend.common.outbox.OutboxEventStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OutboxOrderApplicationEventPublisherTest {

    @Test
    void publish_shouldSaveApplicationAcceptedEventToOutbox() throws Exception {
        OutboxEventRepository outboxEventRepository = mock(OutboxEventRepository.class);
        ObjectMapper objectMapper = new ObjectMapper();

        OutboxOrderApplicationEventPublisher publisher =
                new OutboxOrderApplicationEventPublisher(outboxEventRepository, objectMapper);

        UUID applicationId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID hunterProfileId = UUID.randomUUID();

        ApplicationAcceptedEvent event = new ApplicationAcceptedEvent(
                applicationId,
                orderId,
                hunterProfileId
        );

        publisher.publish(event);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent savedEvent = captor.getValue();

        assertThat(savedEvent.getAggregateType()).isEqualTo(ApplicationAcceptedEvent.AGGREGATE_TYPE);
        assertThat(savedEvent.getAggregateId()).isEqualTo(applicationId);
        assertThat(savedEvent.getEventType()).isEqualTo(ApplicationAcceptedEvent.EVENT_TYPE);
        assertThat(savedEvent.getStatus()).isEqualTo(OutboxEventStatus.PENDING);

        ApplicationAcceptedEvent payload =
                objectMapper.readValue(savedEvent.getPayload(), ApplicationAcceptedEvent.class);

        assertThat(payload).isEqualTo(event);
    }
}