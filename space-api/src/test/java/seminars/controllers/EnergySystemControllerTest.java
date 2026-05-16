package seminars.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import seminars.domains.satellites.EnergySystem;
import seminars.domains.satellites.requests.EnergySystemUpdateRequest;
import seminars.exceptions.ResourceNotFoundException;
import seminars.exceptions.SpaceOperationException;
import seminars.services.EnergySystemService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnergySystemController.class)
@DisplayName("Unit-тесты EnergySystemController")
class EnergySystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EnergySystemService energySystemService;

    @Test
    @DisplayName("GET /api/energy-systems — получить все системы, возвращает 200")
    void shouldGetAllEnergySystemsReturn200() throws Exception {
        List<EnergySystem> systems = List.of(
                EnergySystem.builder().id(1L).batteryLevel(0.85).lowBatteryThreshold(0.2).maxBattery(1.0).minBattery(0.0).build(),
                EnergySystem.builder().id(2L).batteryLevel(0.50).lowBatteryThreshold(0.2).maxBattery(1.0).minBattery(0.0).build()
        );

        when(energySystemService.getAllEnergySystems()).thenReturn(systems);

        mockMvc.perform(get("/api/energy-systems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].batteryLevel").value(0.85))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].batteryLevel").value(0.50));

        verify(energySystemService).getAllEnergySystems();
    }

    @Test
    @DisplayName("GET /api/energy-systems/{id} — получение по id, возвращает 200")
    void shouldGetEnergySystemByIdReturn200() throws Exception {
        EnergySystem energySystem = EnergySystem.builder()
                .id(1L)
                .batteryLevel(0.75)
                .lowBatteryThreshold(0.2)
                .maxBattery(1.0)
                .minBattery(0.0)
                .build();

        when(energySystemService.getEnergySystemById(1L)).thenReturn(energySystem);

        mockMvc.perform(get("/api/energy-systems/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.batteryLevel").value(0.75));

        verify(energySystemService).getEnergySystemById(1L);
    }

    @Test
    @DisplayName("GET /api/energy-systems/{id} — не найден, возвращает 404")
    void shouldGetEnergySystemByIdReturn404WhenNotFound() throws Exception {
        when(energySystemService.getEnergySystemById(999L))
                .thenThrow(new ResourceNotFoundException("Энергосистема не найдена по id: 999"));

        mockMvc.perform(get("/api/energy-systems/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/energy-systems/{id} — обновление батареи, возвращает 200")
    void shouldUpdateEnergySystemReturn200() throws Exception {
        EnergySystem updatedSystem = EnergySystem.builder()
                .id(1L)
                .batteryLevel(0.90)
                .lowBatteryThreshold(0.2)
                .maxBattery(1.0)
                .minBattery(0.0)
                .build();

        when(energySystemService.updateEnergySystem(1L, 0.90)).thenReturn(updatedSystem);

        EnergySystemUpdateRequest request = new EnergySystemUpdateRequest(0.90);

        mockMvc.perform(patch("/api/energy-systems/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.batteryLevel").value(0.90));

        verify(energySystemService).updateEnergySystem(1L, 0.90);
    }
}