package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.aop.LogExecutionTime;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.requests.AddSatelliteRequest;
import seminars.domains.satellites.requests.MissionRequest;
import seminars.dto.AddSatellitesResponse;
import seminars.dto.ConstellationStatusResponse;
import seminars.dto.MissionResultResponse;
import seminars.dto.SatelliteStatusResponse;
import seminars.dto.SystemOverviewResponse;
import seminars.repository.ConstellationRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SpaceOperationCenterService {
    private final ConstellationService constellationService;
    private final SatelliteService satelliteService;
    private final ConstellationRepository constellationRepository;

    @LogExecutionTime()
    public AddSatellitesResponse addSatellite(AddSatelliteRequest addSatelliteRequest) {
        if (!constellationRepository.existsByName(addSatelliteRequest.constellationName())) {
            constellationService.createConstellation(addSatelliteRequest.constellationName());
        }

        List<SatelliteStatusResponse> createdSatellites = addSatelliteRequest.satelliteParams().stream()
                .map(param -> {
                    Satellite satellite = satelliteService.createSatellite(param);
                    var constellation = constellationService.getConstellationByName(
                            addSatelliteRequest.constellationName());
                    constellationService.addSatelliteToConstellation(constellation.getId(), satellite.getId());
                    return new SatelliteStatusResponse(
                            satellite.getId(),
                            satellite.getName(),
                            satellite.getClass().getSimpleName(),
                            satellite.getState().isActive(),
                            satellite.getEnergy().getBatteryLevel()
                    );
                })
                .toList();

        return new AddSatellitesResponse(addSatelliteRequest.constellationName(), createdSatellites);
    }

    public MissionResultResponse executeMission(MissionRequest missionRequest) {
        return switch (missionRequest.targetType()) {
            case CONSTELLATION -> {
                ConstellationStatusResponse status = constellationService.activateAllSatellites(
                        missionRequest.constellationName());
                constellationService.executeConstellationMission(missionRequest.constellationName());
                long activeCount = status.satellites().stream().filter(SatelliteStatusResponse::active).count();
                yield new MissionResultResponse(
                        missionRequest.constellationName(),
                        null,
                        activeCount > 0,
                        "Активировано " + activeCount + " из " + status.satelliteCount() + " спутников"
                );
            }
            case SINGLE_SATELLITE -> {
                var satellite = satelliteService.getSatelliteByName(missionRequest.satelliteName());
                SatelliteStatusResponse status = satelliteService.activateSatellite(satellite.getId());
                satelliteService.performSatelliteMission(satellite.getId());
                yield new MissionResultResponse(
                        missionRequest.constellationName(),
                        missionRequest.satelliteName(),
                        status.active(),
                        status.active() ? "Миссия выполнена" : "Не удалось активировать спутник"
                );
            }
            default -> throw new IllegalArgumentException("Данный тип цели не поддерживается: "
                    + missionRequest.targetType());
        };
    }

    public void decommissionSatellite(String constellationName, String satelliteName) {
        var constellation = constellationService.getConstellationByName(constellationName);
        var satellite = satelliteService.getSatelliteByName(satelliteName);

        constellation.removeSatellite(satellite);
        constellationRepository.save(constellation);
        log.info("Спутник {} выведен из эксплуатации (удален из группировки {})", satelliteName, constellationName);
    }

    @Transactional(readOnly = true)
    public SystemOverviewResponse getSystemOverview() {
        var allConstellations = constellationService.getAllConstellationsWithSatellites();

        int totalSatellites = allConstellations.stream()
                .mapToInt(c -> c.getSatellites().size())
                .sum();

        List<ConstellationStatusResponse> constellationResponses = allConstellations.stream()
                .map(c -> constellationService.getConstellationStatus(c.getName()))
                .toList();

        return new SystemOverviewResponse(
                allConstellations.size(),
                totalSatellites,
                constellationResponses
        );
    }
}
