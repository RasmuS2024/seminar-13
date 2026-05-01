package seminars.domains.satellites.requests;

import seminars.domains.satellites.SatelliteParam;
import java.util.List;

/**
 * Запрос на добавление спутников в группировку.
 * @param constellationName имя
 * @param satelliteParams   список параметров
 */
public record AddSatelliteRequest(String constellationName, List<SatelliteParam> satelliteParams) {
}
