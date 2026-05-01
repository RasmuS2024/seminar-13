package seminars.domains.satellites.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на выполнение миссий.
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
}
