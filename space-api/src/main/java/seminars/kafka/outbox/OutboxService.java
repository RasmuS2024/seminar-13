package seminars.kafka.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import seminars.exceptions.SpaceOperationException;
import seminars.kafka.KafkaService;
import seminars.kafka.SatelliteEvent;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxService {
    private final KafkaService kafkaService;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private static final String SATELLITE_EVENTS_TOPIC = "satellite-events";
    private static final int BATCH_SIZE = 50;

    public void publishToOutbox(Long satId, SatelliteEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = OutboxEvent.builder()
                    .aggregateId(satId)
                    .eventType(event.eventType().name())
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .build();
            outboxRepository.save(outbox);
        } catch (Exception e) {
            log.error("Ошибка сериализации outbox-события", e);
            throw new SpaceOperationException("Ошибка сериализации outbox-события", e);
        }
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEvent.OutboxStatus.PENDING,
                PageRequest.of(0, BATCH_SIZE));

        if (pending.isEmpty()) {
            return;
        }

        for (OutboxEvent event : pending) {
            try {
                SatelliteEvent satelliteEvent = objectMapper.readValue(event.getPayload(), SatelliteEvent.class);
                kafkaService.sendToKafkaSatellite(
                        SATELLITE_EVENTS_TOPIC,
                        satelliteEvent
                );

                outboxRepository.updateStatus(event.getId(), OutboxEvent.OutboxStatus.SENT);
                log.info("Outbox-событие {} отправлено в Kafka", event.getId());
            } catch (Exception e) {
                log.error("Ошибка отправки outbox-события {}: {}", event.getId(), e.getMessage());
                outboxRepository.updateStatus(event.getId(), OutboxEvent.OutboxStatus.FAILED);
            }
        }
    }
}
