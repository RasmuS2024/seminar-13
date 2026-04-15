package seminars.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seminars.aop.LogExecutionTime;
import seminars.domains.satellites.requests.AddSatelliteRequest;
import seminars.domains.satellites.requests.MissionRequest;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.SatelliteParam;
import seminars.exceptions.SpaceOperationException;

@RequiredArgsConstructor
@Service
public class SpaceOperationCenterService {
    private final ConstellationService constellationService;

    private final SatelliteServiceImpl satelliteService;

    /**
     * Добавляет спутник в репозиторий
     * @param addSatelliteRequest   запрос на добавление спутника
     */
    @LogExecutionTime()
    public void addSatellite(AddSatelliteRequest addSatelliteRequest) {
        try {
            constellationService.showConstellationStatus(addSatelliteRequest.constellationName());
        } catch (Exception e) {
            constellationService.createAndSaveConstellation(addSatelliteRequest.constellationName());
        }

        for (SatelliteParam param : addSatelliteRequest.satelliteParams()) {
            Satellite satellite = satelliteService.createSatellite(param);
            constellationService.addSatelliteToConstellation(addSatelliteRequest.constellationName(), satellite);
        }
    }

    /**
     * Выполняет миссии в соответствии с запросом
     * Для группировки активирует все спутники и запускает их миссии
     * Для одного спутника - активирует его и выполняет миссию
     * @param missionRequest    запрос на выполнение миссий
     */
    public void executeMission(MissionRequest missionRequest) {
        switch (missionRequest.targetType()) {
            case CONSTELLATION -> {
                constellationService.activateAllSatellites(missionRequest.constellationName());
                constellationService.executeConstellationMission(missionRequest.constellationName());
            }
            case SINGLE_SATELLITE -> {
                var constellation = constellationService.getConstellation(missionRequest.constellationName());
                var satellite = constellation.getSatellites().stream()
                        .filter(s -> s.getName().equals(missionRequest.satelliteName()))
                        .findFirst()
                        .orElseThrow(() -> new SpaceOperationException("Спутник не найден: " + missionRequest.satelliteName()));
                satellite.activate();
                satellite.performMission();
            }
        }

    }

    /**
     * Выводит спутник из эксплуатации (удаляет из группировки)
     * @param constellationName имя группировки
     * @param satelliteName     имя спутника
     */
    public void decommissionSatellite(String constellationName, String satelliteName) {
        var constellation = constellationService.getConstellation(constellationName);
        if (constellation == null) {
            throw new SpaceOperationException("Группировка не найдена: " + constellationName);
        }

        boolean removed = constellationService.removeSatelliteFromConstellation(constellationName, satelliteName);
        if (!removed) {
            throw new SpaceOperationException("Спутник не найден в группировке: " + satelliteName);
        }
    }

    /**
     * Возвращает общую сводку по всем группировкам и спутникам
     */
    public String getSystemOverview() {
        var allConstellations = constellationService.getAllConstellations();

        StringBuilder sb = new StringBuilder("=== СИСТЕМНАЯ СВОДКА ===\n");
        sb.append("Всего группировок: ").append(allConstellations.size()).append("\n");
        allConstellations.values().forEach(cons -> {
            sb.append("  Группировка '").append(cons.getConstellationName())
                    .append("': спутников: ").append(cons.getSatellites().size()).append("\n");
            cons.getSatellites().forEach(sat ->
                    sb.append("    - ").append(sat.getName())
                            .append(" [").append(sat.getState().isActive() ? "Активен" : "Неактивен")
                            .append("], заряд: ").append((int)(sat.getEnergy().getBatteryLevel()*100)).append("%\n")
            );
        });
        return sb.toString();
    }

}
