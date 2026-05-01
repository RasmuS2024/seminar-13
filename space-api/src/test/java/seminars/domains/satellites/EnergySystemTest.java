package seminars.domains.satellites;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тесты EnergySystem")
class EnergySystemTest {

    private EnergySystem energy;
    private static final double INITIAL_BATTERY = 0.5;
    private static final double MIN_BATTERY = 0.0;
    private static final double MAX_BATTERY = 1.0;
    private static final double BATTERY_THRESHOLD = 0.2;

    @BeforeEach
    void setUp() {
        energy = EnergySystem.builder()
                .batteryLevel(INITIAL_BATTERY)
                .minBattery(MIN_BATTERY)
                .maxBattery(MAX_BATTERY)
                .lowBatteryThreshold(BATTERY_THRESHOLD)
                .build();
    }

    @Test
    @DisplayName("Успешное потребление энергии")
    void testConsumeSuccess() {
        // Act
        boolean result = energy.consume(0.2);

        // Assert
        assertTrue(result, "Метод должен вернуть true");
        assertEquals(0.3, energy.getBatteryLevel(), 0.001, "Заряд должен уменьшиться на 0.2");
    }

    @Test
    @DisplayName("Отказ при потреблении отрицательного или нулевого значения")
    void testConsumeInvalidAmount() {
        // Act
        boolean resultZero = energy.consume(0);
        boolean resultNegative = energy.consume(-0.1);

        // Assert
        assertFalse(resultZero);
        assertFalse(resultNegative);
        assertEquals(INITIAL_BATTERY, energy.getBatteryLevel(), "Заряд не должен измениться");
    }

    @Test
    @DisplayName("Отказ при потреблении, если батарея уже на минимуме")
    void testConsumeWhenEmpty() {
        // Arrange: разряжаем в ноль
        energy.setBatteryLevel(MIN_BATTERY);

        // Act
        boolean result = energy.consume(0.1);

        // Assert
        assertFalse(result, "Нельзя потреблять энергию при минимальном заряде");
        assertEquals(MIN_BATTERY, energy.getBatteryLevel());
    }

    @Test
    @DisplayName("Заряд не должен падать ниже минимального при большом потреблении")
    void testConsumeLimitToMin() {
        // Arrange: доступно 0.5 - 0.1 = 0.4
        double tooMuch = 0.41;

        // Act
        boolean result = energy.consume(tooMuch);

        // Assert
        assertFalse(result, "Метод должен вернуть false при попытке потребления больше доступного");
        assertEquals(INITIAL_BATTERY, energy.getBatteryLevel(),
                "Заряд не должен измениться");
    }
}

