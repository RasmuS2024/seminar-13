package requests;

import seminars.domains.satellites.SatelliteParam;

import java.util.List;

/**
 * Запрос на выполнение миссий
 * @param targetType тип цели - группировка или конкретный спутник
 * @param constellationName имя группировки
 * @param satelliteName имя спутника (если targetType = SINGLE_SATELLITE)
 */
public record MissionRequest(MissionTargetType targetType, String constellationName, String satelliteName) {
}
