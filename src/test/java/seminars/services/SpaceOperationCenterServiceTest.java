package seminars.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import requests.AddSatelliteRequest;
import requests.MissionRequest;
import requests.MissionTargetType;
import seminars.SatelliteConstellation;
import seminars.domains.satellites.CommunicationSatelliteParam;
import seminars.domains.satellites.ImagingSatelliteParam;
import seminars.domains.satellites.Satellite;
import seminars.repository.ConstellationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Интеграционные тесты фасада SpaceOperationCenterService")
class SpaceOperationCenterServiceTest {

    @Autowired
    private  SpaceOperationCenterService spaceOperationCenterService;

    @Autowired
    private ConstellationRepository constellationRepository;

    private String uniqueName(String namePrefix) {
        return namePrefix + "_" + System.currentTimeMillis();
    }

    @Test
    @DisplayName("Добавление спутников в группировку через фасад")
    void addSatelliteTest() {
        String constellationName = uniqueName("TestConstellation");
        String commSatName = "Спутник связи 1";
        String imgSatName = "Спуьник ДЗЗ 1";

        var commParam = new CommunicationSatelliteParam(commSatName, 0.85, 300);
        var imgParam = new ImagingSatelliteParam(imgSatName, 0.8, 5.5);
        var request = new AddSatelliteRequest(
                constellationName,
                List.of(commParam, imgParam)
        );

        spaceOperationCenterService.addSatellite(request);

        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation, "Группировка должна существовать");
        assertEquals(2, constellation.getSatellites().size(), "В группировке должно быть 2 спутника");

        List<String> satelliteNames = constellation.getSatellites().stream()
                .map(Satellite::getName)
                .toList();
        assertTrue(satelliteNames.contains(commSatName));
        assertTrue(satelliteNames.contains(imgSatName));

    }

    @Test
    @DisplayName("Выполнение миссий для всей группировки")
    void executeConstellationMissionTest() {
        String constellationName = uniqueName("MissionConstellation");
        String commSatName = "Спутник связи 1";

        var commParam = new CommunicationSatelliteParam(commSatName, 0.85, 300);
        var addRequest = new AddSatelliteRequest(
                constellationName,
                List.of(commParam)
        );
        spaceOperationCenterService.addSatellite(addRequest);

        var missionRequest = new MissionRequest(
                MissionTargetType.CONSTELLATION,
                constellationName,
                null
        );
        spaceOperationCenterService.executeMission(missionRequest);

        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        Satellite satellite = constellation.getSatellites().getFirst();
        assertTrue(satellite.getState().isActive(), "Спутник должен быть активен после миссии");

    }

    @Test
    @DisplayName("Получение системной сводки")
    void getSystemOverviewTest() {
        String constellationName = uniqueName("Группировка_сводка_тестовая");
        String satName = "Спутник связи - получение сводки";

        var commParam = new CommunicationSatelliteParam(satName, 0.8, 500.0);
        var addRequest = new AddSatelliteRequest(
                constellationName,
                List.of(commParam)
        );
        spaceOperationCenterService.addSatellite(addRequest);

        String overview = spaceOperationCenterService.getSystemOverview();
        assertTrue(overview.contains(constellationName), "Сводка должна содержать имя группировки");
        assertTrue(overview.contains(satName), "Сводка должна содержать имя спутника");
        assertTrue(overview.contains("заряд: 80%") || overview.contains("заряд: 80"),
                "Сводка должна содержать уровень заряда (80%)");


    }
}
