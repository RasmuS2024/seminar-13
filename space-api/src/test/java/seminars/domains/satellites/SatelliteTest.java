package seminars.domains.satellites;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit-тесты логики Спутника (Domain)")
class SatelliteTest {

    private ImagingSatellite satellite;
    private static final String NAME = "Тестовый-ДЗЗ";

    @BeforeEach
    void setUp() {
        satellite = new ImagingSatellite(NAME, 0.8, 5.5);
    }

    @Test
    @DisplayName("Спутник должен успешно активироваться при достаточном заряде")
    void testSuccessfulActivation() {
        // Act
        boolean result = satellite.activate();

        // Assert
        assertTrue(result, "Метод activate() должен вернуть true");
        assertTrue(satellite.getState().isActive(), "Состояние должно измениться на active");
    }

    @Test
    @DisplayName("Спутник не должен активироваться при низком заряде")
    void testActivationFailureLowBattery() {
        // Arrange
        ImagingSatellite weakSatellite = new ImagingSatellite(NAME, 0.01, 5.5);

        // Act
        boolean result = weakSatellite.activate();

        // Assert
        assertFalse(result, "Активация не должна состояться");
        assertFalse(weakSatellite.getState().isActive(), "Состояние должно остаться неактивным");
    }

    @Test
    @DisplayName("Активация: Отказ при повторной активации")
    void testActivateAlreadyActive() {
        // Arrange
        ImagingSatellite sat = new ImagingSatellite("Alpha", 0.9, 5.0);
        sat.activate();

        // Act
        boolean result = sat.activate();

        // Assert
        assertFalse(result, "Повторная активация должна вернуть false");
    }

    @Test
    @DisplayName("Метод deActivate должен корректно выключать активный спутник")
    void testDeactivation() {
        // Arrange
        satellite.activate();

        // Act
        satellite.deActivate();

        // Assert
        assertFalse(satellite.getState().isActive(), "После deActivate состояние должно быть false");
    }

    @Test
    @DisplayName("Повторная деактивация уже выключенного спутника не должна вызывать ошибок")
    void testIdempotentDeactivation() {
        // Arrange (спутник по умолчанию выключен)

        // Act & Assert
        assertDoesNotThrow(() -> {
            satellite.deActivate();
            satellite.deActivate();
        });
        assertFalse(satellite.getState().isActive());
    }
}
