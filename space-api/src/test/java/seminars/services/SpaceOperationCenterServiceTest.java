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
import seminars.domains.satellites.SatelliteParam;
import seminars.exceptions.SpaceOperationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
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

    @InjectMocks
    private SpaceOperationCenterService spaceOperationCenterService;

    @Test
    @DisplayName("addSatellite создаёт группировку и добавляет спутники")
    void addSatelliteCreatesConstellationAndAddsSatellites() {
        String constellationName = "TestConstellation";
        String satName = "TestSatellite";
        SatelliteParam param = new seminars.domains.satellites.ImagingSatelliteParam(satName, 0.8, 1.0);
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
        doAnswer(invocation -> {
            satellite.activate();
            return null;
        }).when(satelliteService).activateSatellite(any());
        doNothing().when(satelliteService).performSatelliteMission(any());

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

        spaceOperationCenterService.decommissionSatellite(constellationName, satelliteName);

        assertFalse(constellation.getSatellites().contains(satellite));
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
    @DisplayName("getSystemOverview возвращает сводку")
    void getSystemOverviewReturnsOverview() {
        SatelliteConstellation constellation = new SatelliteConstellation("TestConstellation");
        Satellite satellite = new ImagingSatellite("TestSatellite", 0.8, 1.0);
        constellation.addSatellite(satellite);

        when(constellationService.getAllConstellations()).thenReturn(List.of(constellation));

        String overview = spaceOperationCenterService.getSystemOverview();

        assertNotNull(overview);
        assertTrue(overview.contains("TestConstellation"));
        assertTrue(overview.contains("TestSatellite"));
    }
}
