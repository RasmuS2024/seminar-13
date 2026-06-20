package seminars.kafka;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import seminars.kafka.inbox.InboxService;
import seminars.kafka.inbox.InboxUtils;
import seminars.services.SatelliteIdRepository;

@RequiredArgsConstructor
@Slf4j
@Component
public class SatelliteEventListener {

    private static final String TOPIC = "satellite-events";
    private final SatelliteIdRepository satelliteIdRepository;
    private final InboxService inboxService;

    @Transactional
    @KafkaListener(topics = TOPIC)
    public void handleSatelliteEvent(ConsumerRecord<String, SatelliteEvent> consumerRecord) {
        try {
            SatelliteEvent event = consumerRecord.value();
            log.info("Получено событие: type={}, satelliteId={}, name={}, offset={}",
                    event.eventType(), event.satelliteId(), event.satelliteName(), consumerRecord.offset());

            if (inboxService.existsByEventId(InboxUtils.generateStableEventId(event))) {
                log.debug("Событие {} уже обработано, пропускаем", event.satelliteId());
                return;
            }

            switch (event.eventType()) {
                case CREATED -> satelliteIdRepository.add(event.satelliteId());
                case DELETED -> satelliteIdRepository.remove(event.satelliteId());
                default -> log.warn("Неизвестный тип события: {}", event.eventType());
            }

            inboxService.saveToInbox(InboxUtils.createInboxEvent(event));

        } catch (Exception e) {
            log.error("Ошибка обработки события (offset={}): {}", consumerRecord.offset(), e.getMessage(), e);
        }

    }

}
