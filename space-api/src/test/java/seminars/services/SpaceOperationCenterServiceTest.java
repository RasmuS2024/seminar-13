package seminars.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import seminars.domains.constellations.SatelliteConstellation;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.ImagingSatelliteParam;
import seminars.domains.satellites.params.SatelliteParam;
import seminars.dto.ConstellationStatusResponse;
import seminars.dto.SystemOverviewResponse;
import seminars.exceptions.SpaceOperationException;
import seminars.repository.ConstellationRepository;

import java.util.List;

import seminars.dto.SatelliteStatusResponse;
import seminars.dto.AddSatellitesResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты SpaceOperationCenterService")
class SpaceOperationCenterServiceTest {

    @Mock
    private ConstellationService constellationService;

    @Mock
    private SatelliteService satelliteService;

    @Mock
    private ConstellationRepository constellationRepository;

    @InjectMocks
    private SpaceOperationCenterService spaceOperationCenterService;

    @Test
    @DisplayName("addSatellite создаёт группировку и добавляет спутники")
    void addSatelliteCreatesConstellationAndAddsSatellites() {
        String constellationName = "TestConstellation";
        String satName = "TestSatellite";
        SatelliteParam param = new ImagingSatelliteParam(satName, 0.8, 1.0);
        var request = new seminars.domains.satellites.requests.AddSatelliteRequest(
                constellationName, List.of(param));

        Satellite satellite = new ImagingSatellite(satName, 0.8, 1.0);
        SatelliteConstellation constellation = new SatelliteConstellation(constellationName);

        doThrow(new SpaceOperationException("Не найдена"))
                .when(constellationService)
                .showConstellationStatus(anyString());
        when(constellationService.createConstellation(anyString())).thenReturn(constellation);
        when(constellationService.getConstellationByName(anyString())).thenReturn(constellation);
        when(satelliteService.createSatellite(param)).thenReturn(satellite);
        doNothing().when(constellationService).addSatelliteToConstellation(any(), any());

        spaceOperationCenterService.addSatellite(request);

        verify(constellationService).createConstellation(constellationName);
        verify(satelliteService).createSatellite(param);
    }

    @Test
    @DisplayName("executeMission для CONSTELLATION активирует и выполняет миссии")
    void executeMissionForConstellationActivatesAndExecutes() {
        String constellationName = "TestConstellation";
        var missionRequest = new seminars.domains.satellites.requests.MissionRequest(
                seminars.domains.satellites.requests.MissionTargetType.CONSTELLATION,
                constellationName, null);

        when(constellationService.activateAllSatellites(constellationName))
                .thenReturn(new ConstellationStatusResponse(1L, constellationName, 1, 0, List.of()));
        when(constellationService.executeConstellationMission(constellationName))
                .thenReturn(new ConstellationStatusResponse(1L, constellationName, 1, 0, List.of()));

        spaceOperationCenterService.executeMission(missionRequest);

        verify(constellationService).activateAllSatellites(constellationName);
        verify(constellationService).executeConstellationMission(constellationName);
    }

    @Test
    @DisplayName("executeMission для SINGLE_SATELLITE активирует один спутник")
    void executeMissionForSingleSatelliteActivatesOne() {
        String constellationName = "TestConstellation";
        String satelliteName = "TestSatellite";
        SatelliteConstellation constellation = new SatelliteConstellation(constellationName);
        Satellite satellite = new ImagingSatellite(satelliteName, 0.8, 1.0);
        constellation.addSatellite(satellite);

        when(satelliteService.getSatelliteByName(satelliteName)).thenReturn(satellite);
        when(satelliteService.activateSatellite(any())).thenAnswer(invocation -> {
            satellite.activate();
            return new SatelliteStatusResponse(
                    invocation.getArgument(0), satelliteName, "ImagingSatellite", true, 0.8);
        });
        when(satelliteService.performSatelliteMission(any()))
                .thenReturn(new SatelliteStatusResponse(1L, satelliteName, "ImagingSatellite", true, 0.7));

        var missionRequest = new seminars.domains.satellites.requests.MissionRequest(
                seminars.domains.satellites.requests.MissionTargetType.SINGLE_SATELLITE,
                constellationName, satelliteName);

        spaceOperationCenterService.executeMission(missionRequest);

        assertTrue(satellite.getState().isActive());
    }

    @Test
    @DisplayName("executeMission для SINGLE_SATELLITE с несуществующим спутником выбрасывает исключение")
    void executeMissionForSingleSatelliteWithNonExistentSatelliteThrowsException() {
        String constellationName = "TestConstellation";
        String satelliteName = "NonExistentSat";

        when(satelliteService.getSatelliteByName(satelliteName))
                .thenThrow(new SpaceOperationException("Спутник не найден по имени: " + satelliteName));

        var missionRequest = new seminars.domains.satellites.requests.MissionRequest(
                seminars.domains.satellites.requests.MissionTargetType.SINGLE_SATELLITE,
                constellationName, satelliteName);

        assertThrows(SpaceOperationException.class,
                () -> spaceOperationCenterService.executeMission(missionRequest));
    }

    @Test
    @DisplayName("decommissionSatellite удаляет спутник из группировки")
    void decommissionSatelliteRemovesSatellite() {
        String constellationName = "TestConstellation";
        String satelliteName = "TestSatellite";
        SatelliteConstellation constellation = new SatelliteConstellation(constellationName);
        Satellite satellite = new ImagingSatellite(satelliteName, 0.8, 1.0);
        constellation.addSatellite(satellite);

        when(constellationService.getConstellationByName(constellationName)).thenReturn(constellation);
        when(satelliteService.getSatelliteByName(satelliteName)).thenReturn(satellite);
        when(constellationRepository.save(any())).thenReturn(constellation);

        spaceOperationCenterService.decommissionSatellite(constellationName, satelliteName);

        assertFalse(constellation.getSatellites().contains(satellite));
        verify(constellationRepository).save(constellation);
    }

    @Test
    @DisplayName("decommissionSatellite для несуществующего спутника выбрасывает исключение")
    void decommissionSatelliteWithNonExistentSatelliteThrowsException() {
        String constellationName = "TestConstellation";
        String satelliteName = "NonExistentSat";
        SatelliteConstellation constellation = new SatelliteConstellation(constellationName);

        when(constellationService.getConstellationByName(constellationName)).thenReturn(constellation);
        when(satelliteService.getSatelliteByName(satelliteName))
                .thenThrow(new SpaceOperationException("Спутник не найден по имени: " + satelliteName));

        assertThrows(SpaceOperationException.class,
                () -> spaceOperationCenterService.decommissionSatellite(constellationName, satelliteName));
    }

    @Test
    @DisplayName("getSystemOverview возвращает сводку в DTO")
    void getSystemOverviewReturnsOverview() {
        SatelliteConstellation constellation = new SatelliteConstellation("TestConstellation");
        Satellite satellite = new ImagingSatellite("TestSatellite", 0.8, 1.0);
        constellation.addSatellite(satellite);

        when(constellationService.getAllConstellationsWithSatellites()).thenReturn(List.of(constellation));
        when(constellationService.getConstellationStatus("TestConstellation"))
                .thenReturn(new ConstellationStatusResponse(1L, "TestConstellation", 1, 1, List.of()));

        var overview = spaceOperationCenterService.getSystemOverview();

        assertNotNull(overview);
        assertTrue(overview.totalConstellations() > 0);
    }
}
