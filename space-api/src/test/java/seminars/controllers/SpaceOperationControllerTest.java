package seminars.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import seminars.domains.satellites.CommunicationSatelliteParam;
import seminars.domains.satellites.ImagingSatelliteParam;
import seminars.domains.satellites.requests.AddSatelliteRequest;
import seminars.domains.satellites.requests.MissionRequest;
import seminars.domains.satellites.requests.MissionTargetType;
import seminars.services.SpaceOperationCenterService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    @DisplayName("POST /api/add-satellites — возвращает 201")
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

        doNothing().when(spaceOperationCenterService).addSatellite(any(AddSatelliteRequest.class));

        mockMvc.perform(post("/api/add-satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(spaceOperationCenterService).addSatellite(any(AddSatelliteRequest.class));
    }

    @Test
    @DisplayName("POST /api/missions (CONSTELLATION) — возвращает 200")
    void shouldExecuteConstellationMissionReturn200() throws Exception {
        String constellationName = "TestConstellation";
        MissionRequest missionRequest = new MissionRequest(
                MissionTargetType.CONSTELLATION,
                constellationName, null);

        doNothing().when(spaceOperationCenterService).executeMission(any(MissionRequest.class));

        mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missionRequest)))
                .andExpect(status().isOk());

        verify(spaceOperationCenterService).executeMission(any(MissionRequest.class));
    }

    @Test
    @DisplayName("POST /api/missions (SINGLE_SATELLITE) — возвращает 200")
    void shouldExecuteSingleSatelliteMissionReturn200() throws Exception {
        String constellationName = "TestConstellation";
        String satelliteName = "TestSatellite";
        MissionRequest missionRequest = new MissionRequest(
                MissionTargetType.SINGLE_SATELLITE,
                constellationName, satelliteName);

        doNothing().when(spaceOperationCenterService).executeMission(any(MissionRequest.class));

        mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missionRequest)))
                .andExpect(status().isOk());

        verify(spaceOperationCenterService).executeMission(any(MissionRequest.class));
    }

    @Test
    @DisplayName("GET /api/overview — возвращает 200 с системной сводкой")
    void shouldGetSystemOverviewReturn200() throws Exception {
        String overview = "=== СИСТЕМНАЯ СВОДКА ===\nВсего группировок: 1";
        when(spaceOperationCenterService.getSystemOverview()).thenReturn(overview);

        mockMvc.perform(get("/api/overview"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(containsString("СИСТЕМНАЯ СВОДКА")));

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
}
