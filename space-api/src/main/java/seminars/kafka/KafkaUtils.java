package seminars.kafka;

import lombok.experimental.UtilityClass;
import seminars.domains.satellites.Satellite;

import java.time.Instant;

@UtilityClass
public class KafkaUtils {
    public static SatelliteEvent createEvent(Satellite satellite, SatelliteEvent.EventType eventType) {
        return new SatelliteEvent(
                satellite.getId(), satellite.getName() ,eventType, Instant.now()
        );
    }
}
