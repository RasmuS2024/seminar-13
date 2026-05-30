package seminars.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaService {
    private final KafkaTemplate<String, SatelliteEvent> kafkaTemplate;

    public void sendToKafkaSatellite(String topic, SatelliteEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.satelliteId()), event);
        log.info("Отправлено событие в Kafka: {}", event);
    }
}
