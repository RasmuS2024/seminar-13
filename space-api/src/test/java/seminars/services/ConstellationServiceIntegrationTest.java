package seminars.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import seminars.domains.satellites.CommunicationSatellite;
import seminars.domains.satellites.CommunicationSatelliteParam;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.ImagingSatelliteParam;
import seminars.domains.satellites.Satellite;
import seminars.domains.constellations.SatelliteConstellation;
import seminars.exceptions.SpaceOperationException;
import seminars.factory.impl.CommunicationSatelliteFactory;
import seminars.factory.impl.ImagingSatelliteFactory;
import seminars.repository.ConstellationRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("Интеграционные тесты для ConstellationService")
class ConstellationServiceIntegrationTest {

    private static final String CONSTELLATION_BASE_NAME = "Тестовая-группировка";
    private static final String COMM_SATELLITE_NAME = "Спутник связи-1";
    private static final String IMAGING_SATELLITE_NAME = "Спутник ДЗЗ-1";
    private static final double FULL_BATTERY = 1.0;
    private static final double COMM_POWER = 100.0;
    private static final double IMG_RESOLUTION = 5.0;

    @Autowired
    private ConstellationService constellationService;

    @Autowired
    private ConstellationRepository constellationRepository;

    @Autowired
    private CommunicationSatelliteFactory communicationFactory;

    @Autowired
    private ImagingSatelliteFactory imagingFactory;

    private String uniqueConstellationName;

    @BeforeEach
    void setUp() {
        uniqueConstellationName = CONSTELLATION_BASE_NAME + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @DisplayName("Полный жизненный цикл: создание → добавление спутников → активация → выполнение миссий")
    void shouldFullLifecycleThroughServiceWorkCorrectly() {
        // Создание
        constellationService.createAndSaveConstellation(uniqueConstellationName);
        SatelliteConstellation constellation = constellationRepository.getConstellation(uniqueConstellationName);
        assertNotNull(constellation);
        assertEquals(0, constellation.getSatellites().size());

        // Добавление спутников
        Satellite commSat = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam(COMM_SATELLITE_NAME, FULL_BATTERY, COMM_POWER)
        );
        Satellite imgSat = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam(IMAGING_SATELLITE_NAME, FULL_BATTERY, IMG_RESOLUTION)
        );

        constellationService.addSatelliteToConstellation(uniqueConstellationName, commSat);
        constellationService.addSatelliteToConstellation(uniqueConstellationName, imgSat);

        constellation = constellationRepository.getConstellation(uniqueConstellationName);
        assertEquals(2, constellation.getSatellites().size());
        for (Satellite s : constellation.getSatellites()) {
            assertFalse(s.getState().isActive());
        }

        // Активация
        constellationService.activateAllSatellites(uniqueConstellationName);
        constellation = constellationRepository.getConstellation(uniqueConstellationName);
        for (Satellite s : constellation.getSatellites()) {
            assertTrue(s.getState().isActive());
        }

        // Выполнение миссии
        double initialCommBattery = commSat.getEnergy().getBatteryLevel();
        double initialImgBattery = imgSat.getEnergy().getBatteryLevel();

        constellationService.executeConstellationMission(uniqueConstellationName);

        constellation = constellationRepository.getConstellation(uniqueConstellationName);
        CommunicationSatellite updatedComm = (CommunicationSatellite) constellation.getSatellites()
                .stream().filter(s -> s.getName().equals(COMM_SATELLITE_NAME)).findFirst().orElse(null);
        ImagingSatellite updatedImg = (ImagingSatellite) constellation.getSatellites()
                .stream().filter(s -> s.getName().equals(IMAGING_SATELLITE_NAME)).findFirst().orElse(null);

        assertNotNull(updatedComm);
        assertNotNull(updatedImg);
        assertTrue(updatedComm.getEnergy().getBatteryLevel() < initialCommBattery);
        assertTrue(updatedImg.getEnergy().getBatteryLevel() < initialImgBattery);
        assertTrue(updatedComm.getState().isActive());
        assertTrue(updatedImg.getState().isActive());
    }

    @Test
    @DisplayName("Миссия для несуществующей группировки вызывает исключение")
    void shouldExecuteConstellationMissionWithNonExistentConstellationThrowSpaceOperationException() {
        String nonExistentName = "не-существует";

        SpaceOperationException exception = assertThrows(SpaceOperationException.class,
                () -> constellationService.executeConstellationMission(nonExistentName));

        String expectedMessage = "Группировка не найдена: " + nonExistentName;
        assertEquals(expectedMessage, exception.getMessage(),
                "Сообщение исключения должно содержать имя "
                        + "несуществующей группировки");
    }

}
