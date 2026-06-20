package seminars.kafka.inbox;

import seminars.kafka.SatelliteEvent;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

public class InboxUtils {
    public static InboxEvent createInboxEvent(SatelliteEvent event) {
        return InboxEvent
                .builder()
                .eventId(generateStableEventId(event))
                .aggregateId(event.satelliteId())
                .eventType(event.eventType().name())
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static UUID generateStableEventId(SatelliteEvent event) {
        String key = event.satelliteId()
                    + ":" + event.eventType()
                    + ":" + event.timestamp();
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }
}
