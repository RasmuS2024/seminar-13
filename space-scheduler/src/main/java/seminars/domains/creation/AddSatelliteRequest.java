package seminars.domains.creation;

import seminars.domains.creation.params.SatelliteParam;

import java.util.List;

/**
 * Запрос на добавление спутников в группировку.
 * @param constellationName имя
 * @param satelliteParams   список параметров
 */
public record AddSatelliteRequest(String constellationName, List<SatelliteParam> satelliteParams) {
}
