package seminars.domains.mission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на выполнение миссий
 * @param targetType тип цели - группировка или конкретный спутник
 * @param constellationName имя группировки
 * @param satelliteName имя спутника (если targetType = SINGLE_SATELLITE)
 */
public record MissionRequest(
        @NotNull MissionTargetType targetType,
        @NotBlank String constellationName,
        String satelliteName
) {
    public MissionRequest {
        if (targetType == MissionTargetType.SINGLE_SATELLITE && (satelliteName == null || satelliteName.isBlank())) {
            throw new IllegalArgumentException("satelliteName обязателен для SINGLE_SATELLITE");
        }
        if (targetType == MissionTargetType.CONSTELLATION && satelliteName != null) {
            throw new IllegalArgumentException("satelliteName не должен указываться для CONSTELLATION");
        }
    }
}
