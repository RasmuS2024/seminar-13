package seminars.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import seminars.domains.constellations.SatelliteConstellation;
import seminars.domains.satellites.CommunicationSatelliteParam;
import seminars.domains.satellites.ImagingSatelliteParam;
import seminars.domains.satellites.SatelliteParam;
import seminars.domains.satellites.requests.AddSatelliteRequest;
import seminars.domains.satellites.requests.MissionRequest;
import seminars.domains.satellites.requests.MissionTargetType;
import seminars.repository.ConstellationRepository;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционные тесты SpaceOperationController")
class SpaceOperationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConstellationRepository constellationRepository;

    private String constellationName;
    private String commSatName;
    private String imgSatName;

    private String uniqueName() {
        return "CtrlTestConst-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @BeforeEach
    void setUp() {
        constellationName = uniqueName();
        commSatName = "CtrlComm-" + UUID.randomUUID().toString().substring(0, 6);
        imgSatName = "CtrlImg-" + UUID.randomUUID().toString().substring(0, 6);
    }

    @AfterEach
    void tearDown() {
        if (constellationRepository.containsConstellation(constellationName)) {
            constellationRepository.deleteConstellation(constellationName);
        }
    }

    /**
     * Вспомогательный метод для добавления спутников
     */
    private void addSatellites(String constellationName, SatelliteParam... params) throws Exception {
        AddSatelliteRequest request = new AddSatelliteRequest(constellationName, List.of(params));
        mockMvc.perform(post("/api/add-satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/add-satellites — создаёт группировку и добавляет спутники, возвращает 201")
    void addSatellite_ShouldReturn201() throws Exception {
        AddSatelliteRequest request = new AddSatelliteRequest(
                constellationName,
                List.of(
                        new CommunicationSatelliteParam(commSatName, 0.85, 300.0),
                        new ImagingSatelliteParam(imgSatName, 0.8, 5.5)
                )
        );

        mockMvc.perform(post("/api/add-satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation, "Группировка должна быть создана");
        assertEquals(2, constellation.getSatellites().size(), "В группировке должно быть два спутника");
    }

    @Test
    @DisplayName("POST /api/missions (CONSTELLATION) — активирует спутники и выполняет миссии, возвращает 200")
    void executeConstellationMission_ShouldReturn200() throws Exception {
        constellationRepository.addConstellation(new SatelliteConstellation(constellationName));
        addSatellites(constellationName, new CommunicationSatelliteParam(commSatName, 0.85, 300.0));

        MissionRequest missionRequest = new MissionRequest(
                MissionTargetType.CONSTELLATION,
                constellationName,
                null
        );

        mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation, "Группировка должна существовать");
        assertTrue(constellation.getSatellites().getFirst().getState().isActive(),
                "Спутник должен быть активен после выполнения миссии");
    }

    @Test
    @DisplayName("POST /api/missions (SINGLE_SATELLITE) — активирует один спутник и запускает миссию, возвращает 200")
    void executeSingleSatelliteMission_ShouldReturn200() throws Exception {
        constellationRepository.addConstellation(new SatelliteConstellation(constellationName));
        addSatellites(constellationName,
                new CommunicationSatelliteParam(commSatName, 0.85, 300.0),
                new ImagingSatelliteParam(imgSatName, 0.8, 5.5));

        MissionRequest missionRequest = new MissionRequest(
                MissionTargetType.SINGLE_SATELLITE,
                constellationName,
                commSatName
        );

        mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation);

        var targetSatellite = constellation.getSatellites().stream()
                .filter(s -> s.getName().equals(commSatName))
                .findFirst()
                .orElseThrow();
        assertTrue(targetSatellite.getState().isActive(),
                "Целевой спутник должен быть активен");

        var otherSatellite = constellation.getSatellites().stream()
                .filter(s -> !s.getName().equals(commSatName))
                .findFirst()
                .orElseThrow();
        assertFalse(otherSatellite.getState().isActive(),
                "Другой спутник не должен быть активен");
    }

    @Test
    @DisplayName("GET /api/overview — возвращает 200 с системной сводкой")
    void getSystemOverview_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/overview"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        addSatellites(constellationName,
                new CommunicationSatelliteParam(commSatName, 0.85, 300.0));

        mockMvc.perform(get("/api/overview"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(containsString(constellationName)))
                .andExpect(content().string(containsString(commSatName)));
    }

    @Test
    @DisplayName("DELETE /api/constellations/{name}/satellites/{name} — удаляет спутник, возвращает 204")
    void decommissionSatellite_ShouldReturn204() throws Exception {
        addSatellites(constellationName,
                new CommunicationSatelliteParam(commSatName, 0.85, 300.0),
                new ImagingSatelliteParam(imgSatName, 0.8, 5.5));

        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation);
        assertEquals(2, constellation.getSatellites().size(),
                "Изначально в группировке должно быть два спутника");

        mockMvc.perform(delete("/api/constellations/{constellationName}/satellites/{satelliteName}",
                        constellationName, commSatName))
                .andExpect(status().isNoContent());

        constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation);
        assertEquals(1, constellation.getSatellites().size(),
                "После удаления должен остаться один спутник");
        assertEquals(imgSatName, constellation.getSatellites().getFirst().getName(),
                "Оставшийся спутник должен иметь имя второго спутника");
    }
}
