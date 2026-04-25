package seminars.domains.satellites.requests;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на выполнение миссий
 * @param targetType тип цели - группировка или конкретный спутник
 * @param constellationName имя группировки
 * @param satelliteName имя спутника (если targetType = SINGLE_SATELLITE)
 */
public record MissionRequest(
        @NotNull(message = "Тип цели обязателен")
        MissionTargetType targetType,

        @NotBlank(message = "Имя группировки обязательно")
        String constellationName,

        String satelliteName
) {
    @AssertTrue(message = "Имя спутника обязательно при SINGLE_SATELLITE")
    private boolean isSatelliteNameValid() {
        if (targetType == MissionTargetType.SINGLE_SATELLITE) {
            return satelliteName != null && !satelliteName.isBlank();
        }
        return true;
    }

    @AssertTrue(message = "Имя спутника не должно указываться при CONSTELLATION")
    private boolean isSatelliteNameNotPresentForConstellation() {
        if (targetType == MissionTargetType.CONSTELLATION) {
            return satelliteName == null || satelliteName.isBlank();
        }
        return true;
    }
}