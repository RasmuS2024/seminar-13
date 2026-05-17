package seminars.domains.satellites.requests;

import jakarta.validation.Valid;
import seminars.domains.satellites.params.SatelliteParam;
import java.util.List;

/**
 * Запрос на добавление спутников в группировку.
 * @param constellationName имя
 * @param satelliteParams   список параметров
 */
public record AddSatelliteRequest(
        String constellationName,
        @Valid List<SatelliteParam> satelliteParams) {
}
