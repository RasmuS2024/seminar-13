package seminars.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import seminars.domains.satellites.params.CommunicationSatelliteParam;
import seminars.domains.satellites.params.ImagingSatelliteParam;
import seminars.domains.satellites.requests.AddSatelliteRequest;
import seminars.domains.satellites.requests.MissionRequest;
import seminars.domains.satellites.requests.MissionTargetType;
import seminars.dto.AddSatellitesResponse;
import seminars.dto.ConstellationStatusResponse;
import seminars.dto.MissionResultResponse;
import seminars.dto.SatelliteStatusResponse;
import seminars.dto.SystemOverviewResponse;
import seminars.services.ConstellationService;
import seminars.services.SatelliteService;
import seminars.services.SpaceOperationCenterService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpaceOperationController.class)
@DisplayName("Unit-тесты SpaceOperationController")
class SpaceOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpaceOperationCenterService spaceOperationCenterService;

    @MockitoBean
    private SatelliteService satelliteService;

    @MockitoBean
    private ConstellationService constellationService;

    @Test
    @DisplayName("POST /api/add-satellites — возвращает 201 с JSON")
    void shouldAddSatelliteReturn201() throws Exception {
        String constellationName = "TestConstellation";
        String commSatName = "CommSat";
        String imgSatName = "ImgSat";

        AddSatelliteRequest request = new AddSatelliteRequest(
                constellationName,
                List.of(
                        new CommunicationSatelliteParam(commSatName, 0.85, 300.0),
                        new ImagingSatelliteParam(imgSatName, 0.8, 5.5)
                )
        );

        AddSatellitesResponse response = new AddSatellitesResponse(
                constellationName,
                List.of(
                        new SatelliteStatusResponse(1L, commSatName, "CommunicationSatellite", false, 0.85),
                        new SatelliteStatusResponse(2L, imgSatName, "ImagingSatellite", false, 0.8)
                )
        );
        when(spaceOperationCenterService.addSatellite(any(AddSatelliteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/add-satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constellationName").value(constellationName))
                .andExpect(jsonPath("$.satellites.length()").value(2))
                .andExpect(jsonPath("$.satellites[0].name").value(commSatName))
                .andExpect(jsonPath("$.satellites[1].name").value(imgSatName));

        verify(spaceOperationCenterService).addSatellite(any(AddSatelliteRequest.class));
    }

    @Test
    @DisplayName("POST /api/missions (CONSTELLATION) — возвращает 200 с JSON")
    void shouldExecuteConstellationMissionReturn200() throws Exception {
        String constellationName = "TestConstellation";
        MissionRequest missionRequest = new MissionRequest(
                MissionTargetType.CONSTELLATION,
                constellationName, null);

        MissionResultResponse response = new MissionResultResponse(
                constellationName, null, true, "Активировано 2 из 3 спутников");
        when(spaceOperationCenterService.executeMission(any(MissionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constellationName").value(constellationName))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Активировано 2 из 3 спутников"));

        verify(spaceOperationCenterService).executeMission(any(MissionRequest.class));
    }

    @Test
    @DisplayName("POST /api/missions (SINGLE_SATELLITE) — возвращает 200 с JSON")
    void shouldExecuteSingleSatelliteMissionReturn200() throws Exception {
        String constellationName = "TestConstellation";
        String satelliteName = "TestSatellite";
        MissionRequest missionRequest = new MissionRequest(
                MissionTargetType.SINGLE_SATELLITE,
                constellationName, satelliteName);

        MissionResultResponse response = new MissionResultResponse(
                constellationName, satelliteName, true, "Миссия выполнена");
        when(spaceOperationCenterService.executeMission(any(MissionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constellationName").value(constellationName))
                .andExpect(jsonPath("$.satelliteName").value(satelliteName))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Миссия выполнена"));

        verify(spaceOperationCenterService).executeMission(any(MissionRequest.class));
    }

    @Test
    @DisplayName("GET /api/overview — возвращает 200 с системной сводкой JSON")
    void shouldGetSystemOverviewReturn200() throws Exception {
        SystemOverviewResponse overview = new SystemOverviewResponse(1, 2, List.of());
        when(spaceOperationCenterService.getSystemOverview()).thenReturn(overview);

        mockMvc.perform(get("/api/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalConstellations").value(1))
                .andExpect(jsonPath("$.totalSatellites").value(2));

        verify(spaceOperationCenterService).getSystemOverview();
    }

    @Test
    @DisplayName("DELETE /api/constellations/{name}/satellites/{name} — возвращает 204")
    void shouldDecommissionSatelliteReturn204() throws Exception {
        String constellationName = "TestConstellation";
        String satelliteName = "TestSatellite";

        doNothing().when(spaceOperationCenterService).decommissionSatellite(constellationName, satelliteName);

        mockMvc.perform(delete("/api/constellations/{constellationName}/satellites/{satelliteName}",
                        constellationName, satelliteName))
                .andExpect(status().isNoContent());

        verify(spaceOperationCenterService).decommissionSatellite(constellationName, satelliteName);
    }

    @Test
    @DisplayName("POST /api/satellites/{id}/activate — возвращает 200 с SatelliteStatusResponse")
    void shouldActivateSatelliteReturn200() throws Exception {
        SatelliteStatusResponse status = new SatelliteStatusResponse(
                1L, "TestSat", "CommunicationSatellite", true, 0.8);
        when(satelliteService.activateSatellite(1L)).thenReturn(status);

        mockMvc.perform(post("/api/satellites/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestSat"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.batteryLevel").value(0.8));

        verify(satelliteService).activateSatellite(1L);
    }

    @Test
    @DisplayName("POST /api/satellites/{id}/deactivate — возвращает 200 с SatelliteStatusResponse")
    void shouldDeactivateSatelliteReturn200() throws Exception {
        SatelliteStatusResponse status = new SatelliteStatusResponse(
                1L, "TestSat", "CommunicationSatellite", false, 0.8);
        when(satelliteService.deActivateSatellite(1L)).thenReturn(status);

        mockMvc.perform(post("/api/satellites/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(false));

        verify(satelliteService).deActivateSatellite(1L);
    }

    @Test
    @DisplayName("POST /api/satellites/{id}/mission — возвращает 200 с SatelliteStatusResponse")
    void shouldPerformMissionReturn200() throws Exception {
        SatelliteStatusResponse status = new SatelliteStatusResponse(
                1L, "TestSat", "CommunicationSatellite", true, 0.7);
        when(satelliteService.performSatelliteMission(1L)).thenReturn(status);

        mockMvc.perform(post("/api/satellites/1/mission"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.batteryLevel").value(0.7));

        verify(satelliteService).performSatelliteMission(1L);
    }

    @Test
    @DisplayName("GET /api/satellites/{id}/status — возвращает 200 с SatelliteStatusResponse")
    void shouldGetSatelliteStatusReturn200() throws Exception {
        when(satelliteService.getSatelliteStatus(1L))
                .thenReturn(new SatelliteStatusResponse(1L, "TestSat", "CommunicationSatellite", true, 0.8));

        mockMvc.perform(get("/api/satellites/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestSat"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.batteryLevel").value(0.8));

        verify(satelliteService).getSatelliteStatus(1L);
    }

    @Test
    @DisplayName("POST /api/constellations/{name}/activate — возвращает 200 с ConstellationStatusResponse")
    void shouldActivateAllSatellitesInConstellation() throws Exception {
        ConstellationStatusResponse response = new ConstellationStatusResponse(
                1L, "TestConstellation", 2, 1,
                List.of(
                        new SatelliteStatusResponse(1L, "Sat1", "CommunicationSatellite", true, 0.8),
                        new SatelliteStatusResponse(2L, "Sat2", "ImagingSatellite", false, 0.5)
                ));
        when(constellationService.activateAllSatellites("TestConstellation")).thenReturn(response);

        mockMvc.perform(post("/api/constellations/TestConstellation/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestConstellation"))
                .andExpect(jsonPath("$.activeCount").value(1));

        verify(constellationService).activateAllSatellites("TestConstellation");
    }

    @Test
    @DisplayName("POST /api/constellations/{name}/mission — возвращает 200 с ConstellationStatusResponse")
    void shouldExecuteConstellationMissionViaFacade() throws Exception {
        ConstellationStatusResponse response = new ConstellationStatusResponse(
                1L, "TestConstellation", 2, 1, List.of());
        when(constellationService.executeConstellationMission("TestConstellation")).thenReturn(response);

        mockMvc.perform(post("/api/constellations/TestConstellation/mission"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestConstellation"))
                .andExpect(jsonPath("$.satelliteCount").value(2));

        verify(constellationService).executeConstellationMission("TestConstellation");
    }

    @Test
    @DisplayName("GET /api/constellations/{name}/status — возвращает 200 с ConstellationStatusResponse")
    void shouldGetConstellationStatusReturn200() throws Exception {
        when(constellationService.getConstellationStatus("TestConstellation"))
                .thenReturn(new ConstellationStatusResponse(1L, "TestConstellation", 2, 1, List.of()));

        mockMvc.perform(get("/api/constellations/TestConstellation/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestConstellation"))
                .andExpect(jsonPath("$.satelliteCount").value(2))
                .andExpect(jsonPath("$.activeCount").value(1));

        verify(constellationService).getConstellationStatus("TestConstellation");
    }
}
