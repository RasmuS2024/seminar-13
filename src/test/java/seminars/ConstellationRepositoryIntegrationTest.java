package seminars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import seminars.domains.satellites.CommunicationSatellite;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.Satellite;
import seminars.factory.impl.CommunicationSatelliteFactory;
import seminars.factory.impl.ImagingSatelliteFactory;
import seminars.repository.ConstellationRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Интеграционные тесты для ConstellationRepository")
public class ConstellationRepositoryIntegrationTest {

    private static final String CONSTELLATION_BASE_NAME = "Орбита";
    private static final String SATELLITE_COMM_1 = "Спутник связи-1";
    private static final String SATELLITE_IMG_1 = "Спутник ДЗЗ-1";

    private static final double FULL_BATTERY = 1.0;
    private static final double LOW_BATTERY = 0.1;
    private static final double THRESHOLD_BATTERY = 0.15;

    private static final double COMM_POWER = 100.0;
    private static final double IMG_RESOLUTION = 5.0;

    @Autowired
    private ConstellationRepository repository;
    @Autowired
    private CommunicationSatelliteFactory commFactory;
    @Autowired
    private ImagingSatelliteFactory imgFactory;

    private String uniqueConstellationName;

    @BeforeEach
    void setUp() {
        // Создается уникальное имя для каждой группировки в каждом тесте
        uniqueConstellationName = CONSTELLATION_BASE_NAME + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @DisplayName("Создание группировки и сохранение в репозитории")
    void createConstellation_ShouldSaveInRepository() {
        // Arrange
        SatelliteConstellation constellation = new SatelliteConstellation(uniqueConstellationName);

        // Act
        repository.addConstellation(constellation);

        // Assert
        SatelliteConstellation retrieved = repository.getConstellation(uniqueConstellationName);
        assertSame(constellation, retrieved, "Репозиторий должен вернуть тот же объект");
        assertTrue(repository.containsConstellation(uniqueConstellationName),
                "Группировка должна присутствовать в репозитории");
        assertTrue(retrieved.getSatellites().isEmpty(),
                "Новая группировка должна быть без спутников");
    }

    @Test
    @DisplayName("Добавление спутников в группировку и проверка сохранения в репозитории")
    void addSatellites_ShouldUpdateInRepository() {
        // Arrange
        SatelliteConstellation constellation = new SatelliteConstellation(uniqueConstellationName);
        repository.addConstellation(constellation);

        Satellite commSat = commFactory.createSatelliteWithParameter(SATELLITE_COMM_1, FULL_BATTERY, COMM_POWER);
        Satellite imagingSat = imgFactory.createSatelliteWithParameter(SATELLITE_IMG_1, FULL_BATTERY, IMG_RESOLUTION);

        // Act
        constellation.addSatellite(commSat);
        constellation.addSatellite(imagingSat);

        // Assert
        SatelliteConstellation retrieved = repository.getConstellation(uniqueConstellationName);
        assertEquals(2, retrieved.getSatellites().size(), "Должно быть два спутника");
    }

    @Test
    @DisplayName("Активация всех спутников в группировке при достаточном заряде")
    void activateAllSatellites_WithSufficientBattery_ShouldActivateAll() {
        // Arrange
        SatelliteConstellation constellation = new SatelliteConstellation(uniqueConstellationName);
        repository.addConstellation(constellation);

        Satellite commSat = commFactory.createSatelliteWithParameter(SATELLITE_COMM_1, FULL_BATTERY, COMM_POWER);
        Satellite imagingSat = imgFactory.createSatelliteWithParameter(SATELLITE_IMG_1, FULL_BATTERY, IMG_RESOLUTION);

        constellation.addSatellite(commSat);
        constellation.addSatellite(imagingSat);

        // Act
        constellation.activateAllSatellites();

        // Assert
        assertTrue(commSat.getState().isActive(), "Спутник связи должен активироваться");
        assertTrue(imagingSat.getState().isActive(), "Спутник ДЗЗ должен активироваться");

        // Проверяем сохранение в репозитории
        SatelliteConstellation retrieved = repository.getConstellation(uniqueConstellationName);
        CommunicationSatellite retrievedComm = (CommunicationSatellite) retrieved.getSatellites().get(0);
        ImagingSatellite retrievedImaging = (ImagingSatellite) retrieved.getSatellites().get(1);

        assertTrue(retrievedComm.getState().isActive(), "Состояние должно сохраниться в репозитории");
        assertTrue(retrievedImaging.getState().isActive(), "Состояние должно сохраниться в репозитории");
    }

    @Test
    @DisplayName("Активация спутника с низким зарядом (ниже порога 15%) должна провалиться")
    void activateSatellite_WithLowBattery_ShouldFail() {
        // Arrange
        SatelliteConstellation constellation = new SatelliteConstellation(uniqueConstellationName);
        repository.addConstellation(constellation);

        Satellite lowBatterySat = imgFactory.createSatelliteWithParameter("Тестовый-спутник", LOW_BATTERY, IMG_RESOLUTION);

        constellation.addSatellite(lowBatterySat);

        // Act
        boolean activationResult = lowBatterySat.activate();

        // Assert
        assertFalse(activationResult, "Метод activate() должен вернуть false при низком заряде");
        assertFalse(lowBatterySat.getState().isActive(), "Спутник не должен активироваться");

        // Проверяем сохранение в репозитории
        SatelliteConstellation retrieved = repository.getConstellation(uniqueConstellationName);
        ImagingSatellite retrievedSat = (ImagingSatellite) retrieved.getSatellites().getFirst();
        assertFalse(retrievedSat.getState().isActive(), "Состояние должно сохраниться в репозитории");
    }

    @Test
    @DisplayName("Граничный случай: спутник с зарядом точно на пороге (15%) не должен активироваться")
    void activateSatellite_WithBatteryExactlyAtThreshold_ShouldActivate() {
        // Arrange
        SatelliteConstellation constellation = new SatelliteConstellation(uniqueConstellationName);
        repository.addConstellation(constellation);

        Satellite thresholdSat = imgFactory.createSatelliteWithParameter("Пороговый-спутник", THRESHOLD_BATTERY, IMG_RESOLUTION);
        constellation.addSatellite(thresholdSat);

        // Act
        boolean activationResult = thresholdSat.activate();

        // Assert
        assertFalse(activationResult, "Спутник с зарядом на пороге не должен активироваться");
        assertFalse(thresholdSat.getState().isActive(), "Спутник должен остаться неактивным");
    }

    @Test
    @DisplayName("Выполнение миссий активными спутниками уменьшает уровень заряда")
    void executeMissions_WithActiveSatellites_ShouldConsumeEnergy() {
        // Arrange
        SatelliteConstellation constellation = new SatelliteConstellation(uniqueConstellationName);
        repository.addConstellation(constellation);

        Satellite commSat = commFactory.createSatelliteWithParameter(SATELLITE_COMM_1, FULL_BATTERY, COMM_POWER);
        Satellite imagingSat = imgFactory.createSatelliteWithParameter(SATELLITE_IMG_1, FULL_BATTERY, IMG_RESOLUTION);

        constellation.addSatellite(commSat);
        constellation.addSatellite(imagingSat);
        constellation.activateAllSatellites();

        double initialCommBattery = commSat.getEnergy().getBatteryLevel();
        double initialImagingBattery = imagingSat.getEnergy().getBatteryLevel();

        // Act
        constellation.executeAllMissions();

        // Assert
        assertTrue(commSat.getEnergy().getBatteryLevel() < initialCommBattery,
                "Энергия спутника связи должна уменьшиться");
        assertTrue(imagingSat.getEnergy().getBatteryLevel() < initialImagingBattery,
                "Энергия спутника ДЗЗ должна уменьшиться");
    }
}