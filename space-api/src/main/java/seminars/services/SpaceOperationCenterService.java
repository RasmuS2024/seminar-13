package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.aop.LogExecutionTime;
import seminars.domains.satellites.requests.AddSatelliteRequest;
import seminars.domains.satellites.requests.MissionRequest;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpaceOperationCenterService {
    private final ConstellationService constellationService;
    private final SatelliteService satelliteService;

/**
 * Добавляет спутник в репозиторий.
 * @param addSatelliteRequest   запрос на добавление спутника
 */
    @LogExecutionTime()
    public void addSatellite(AddSatelliteRequest addSatelliteRequest) {
        try {
            constellationService.showConstellationStatus(addSatelliteRequest.constellationName());
        } catch (Exception e) {
            constellationService.createConstellation(addSatelliteRequest.constellationName());
        }

        for (SatelliteParam param : addSatelliteRequest.satelliteParams()) {
            Satellite satellite = satelliteService.createSatellite(param);
            var constellation = constellationService.getConstellationByName(addSatelliteRequest.constellationName());
            constellationService.addSatelliteToConstellation(constellation.getId(), satellite.getId());
        }
    }

/**
 * Выполняет миссии в соответствии с запросом.
 * Для группировки активирует все спутники и запускает их миссии.
 * Для одного спутника - активирует его и выполняет миссию.
 * @param missionRequest    запрос на выполнение миссий
 */
    public void executeMission(MissionRequest missionRequest) {
        switch (missionRequest.targetType()) {
            case CONSTELLATION -> {
                constellationService.activateAllSatellites(missionRequest.constellationName());
                constellationService.executeConstellationMission(missionRequest.constellationName());
            }
            case SINGLE_SATELLITE -> {
                var satellite = satelliteService.getSatelliteByName(missionRequest.satelliteName());
                satelliteService.activateSatellite(satellite.getId());
                satelliteService.performSatelliteMission(satellite.getId());
            }
            default -> throw new IllegalArgumentException("Данный тип цели не поддерживается: "
                    + missionRequest.targetType());

        }

    }

/**
 * Выводит спутник из эксплуатации (удаляет из группировки).
 * @param constellationName имя группировки
 * @param satelliteName     имя спутника
 */
    public void decommissionSatellite(String constellationName, String satelliteName) {
        var constellation = constellationService.getConstellationByName(constellationName);
        var satellite = satelliteService.getSatelliteByName(satelliteName);

        constellation.removeSatellite(satellite);
        log.info("Спутник {} выведен из эксплуатации (удален из группировки {})", satelliteName, constellationName);
    }

/**
 * Возвращает общую сводку по всем группировкам и спутникам.
 * @return строка с информацией о всех группировках
 */
    @Transactional(readOnly = true)
    public String getSystemOverview() {
        var allConstellations = constellationService.getAllConstellationsWithSatellites();

        StringBuilder sb = new StringBuilder("=== СИСТЕМНАЯ СВОДКА ===\n");
        sb.append("Всего группировок: ").append(allConstellations.size()).append("\n");
        allConstellations.forEach(cons -> {
            sb.append("  Группировка '").append(cons.getName())
                    .append("': спутников: ").append(cons.getSatellites().size()).append("\n");
            cons.getSatellites().forEach(sat ->
                    sb.append("    - ").append(sat.getName())
                            .append(" [").append(sat.getState().isActive() ? "Активен" : "Неактивен")
                            .append("], заряд: ").append((int) (sat.getEnergy().getBatteryLevel() * 100)).append("%\n")
            );
        });
        return sb.toString();
    }

}
