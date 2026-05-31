package seminars.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import seminars.services.SatelliteIdRepository;

@RequiredArgsConstructor
@Slf4j
@Component
public class SatelliteEventListener {

    private static final String TOPIC = "satellite-events";
    private final SatelliteIdRepository satelliteIdRepository;

    @KafkaListener(topics = TOPIC)
    public void handleSatelliteEvent(ConsumerRecord<String, SatelliteEvent> record) {
        try {
            SatelliteEvent event = record.value();
            log.info("Получено событие: type={}, satelliteId={}, name={}, offset={}",
                    event.eventType(), event.satelliteId(), event.satelliteName(), record.offset());

            switch (event.eventType()) {
                case CREATED -> satelliteIdRepository.add(event.satelliteId());
                case DELETED -> satelliteIdRepository.remove(event.satelliteId());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события (offset={}): {}", record.offset(), e.getMessage(), e);
        }

    }

}
