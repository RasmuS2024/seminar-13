package seminars.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import seminars.domains.satellites.CommunicationSatellite;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.CommunicationSatelliteParam;
import seminars.exceptions.SpaceOperationException;
import seminars.services.SatelliteService;

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

@WebMvcTest(SatelliteController.class)
@DisplayName("Unit-тесты SatelliteController")
class SatelliteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SatelliteService satelliteService;

    @Test
    @DisplayName("POST /api/satellites — создание, возвращает 201")
    void shouldCreateSatelliteReturn201() throws Exception {
        Satellite satellite = new CommunicationSatellite("TestSat", 0.85, 300.0);

        when(satelliteService.createSatellite(any(CommunicationSatelliteParam.class))).thenReturn(satellite);

        CommunicationSatelliteParam param = new CommunicationSatelliteParam("TestSat", 0.85, 300.0);

        mockMvc.perform(post("/api/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("TestSat"));

        verify(satelliteService).createSatellite(any(CommunicationSatelliteParam.class));
    }

    @Test
    @DisplayName("GET /api/satellites — получение всех, возвращает 200")
    void shouldGetAllSatellitesReturn200() throws Exception {
        List<Satellite> satellites = List.of(
                new CommunicationSatellite("Sat1", 0.8, 125.0),
                new CommunicationSatellite("Sat2", 0.9, 125.0)
        );

        when(satelliteService.getAllSatellites()).thenReturn(satellites);

        mockMvc.perform(get("/api/satellites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Sat1"))
                .andExpect(jsonPath("$[1].name").value("Sat2"));

        verify(satelliteService).getAllSatellites();
    }

    @Test
    @DisplayName("GET /api/satellites/{id} — получение по id, возвращает 200")
    void shouldGetSatelliteByIdReturn200() throws Exception {
        Satellite satellite = new CommunicationSatellite("TestSat", 0.75, 125.0);

        when(satelliteService.getSatelliteById(1L)).thenReturn(satellite);

        mockMvc.perform(get("/api/satellites/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestSat"));

        verify(satelliteService).getSatelliteById(1L);
    }

    @Test
    @DisplayName("GET /api/satellites/{id} — не найден, возвращает 500")
    void shouldGetSatelliteByIdReturn500WhenNotFound() throws Exception {
        when(satelliteService.getSatelliteById(999L))
                .thenThrow(new SpaceOperationException("Спутник не найден по id: 999"));

        mockMvc.perform(get("/api/satellites/999"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/satellites/name/{name} — получение по имени, возвращает 200")
    void shouldGetSatelliteByNameReturn200() throws Exception {
        Satellite satellite = new CommunicationSatellite("TestSat", 0.7, 125.0);

        when(satelliteService.getSatelliteByName("TestSat")).thenReturn(satellite);

        mockMvc.perform(get("/api/satellites/name/TestSat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestSat"));

        verify(satelliteService).getSatelliteByName("TestSat");
    }

    @Test
    @DisplayName("GET /api/satellites/constellation/{id} — получение по constellationId")
    void shouldGetSatellitesByConstellationIdReturn200() throws Exception {
        List<Satellite> satellites = List.of(new CommunicationSatellite("Sat1", 0.8, 125.0));

        when(satelliteService.getSatellitesByConstellationId(1L)).thenReturn(satellites);

        mockMvc.perform(get("/api/satellites/constellation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(satelliteService).getSatellitesByConstellationId(1L);
    }

    @Test
    @DisplayName("POST /api/satellites/{id}/activate — активация, возвращает 200")
    void shouldActivateSatelliteReturn200() throws Exception {
        doNothing().when(satelliteService).activateSatellite(1L);

        mockMvc.perform(post("/api/satellites/1/activate"))
                .andExpect(status().isOk());

        verify(satelliteService).activateSatellite(1L);
    }

    @Test
    @DisplayName("POST /api/satellites/{id}/deactivate — деактивация, возвращает 200")
    void shouldDeactivateSatelliteReturn200() throws Exception {
        doNothing().when(satelliteService).deActivateSatellite(1L);

        mockMvc.perform(post("/api/satellites/1/deactivate"))
                .andExpect(status().isOk());

        verify(satelliteService).deActivateSatellite(1L);
    }

    @Test
    @DisplayName("POST /api/satellites/{id}/mission — выполнение миссии, возвращает 200")
    void shouldPerformMissionReturn200() throws Exception {
        doNothing().when(satelliteService).performSatelliteMission(1L);

        mockMvc.perform(post("/api/satellites/1/mission"))
                .andExpect(status().isOk());

        verify(satelliteService).performSatelliteMission(1L);
    }

    @Test
    @DisplayName("GET /api/satellites/{id}/status — получение статуса, возвращает 200")
    void shouldGetSatelliteStatusReturn200() throws Exception {
        when(satelliteService.getSatelliteStatus(1L)).thenReturn("ACTIVE");

        mockMvc.perform(get("/api/satellites/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("ACTIVE"));

        verify(satelliteService).getSatelliteStatus(1L);
    }

    @Test
    @DisplayName("DELETE /api/satellites/{id} — удаление, возвращает 204")
    void shouldDeleteSatelliteReturn204() throws Exception {
        doNothing().when(satelliteService).deleteSatellite(1L);

        mockMvc.perform(delete("/api/satellites/1"))
                .andExpect(status().isNoContent());

        verify(satelliteService).deleteSatellite(1L);
    }

    @Test
    @DisplayName("POST /api/satellites — batteryLevel > 1.0, возвращает 400")
    void shouldReturn400WhenBatteryLevelExceedsMax() throws Exception {
        CommunicationSatelliteParam param = new CommunicationSatelliteParam("TestSat", 10.99, 300.0);

        mockMvc.perform(post("/api/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("batteryLevel"));
    }
}
