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
import seminars.exceptions.SpaceOperationException;
import seminars.services.EnergySystemService;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("POST /api/energy-systems — создание, возвращает 201")
    void shouldCreateEnergySystemReturn201() throws Exception {
        EnergySystem energySystem = EnergySystem.builder()
                .id(1L)
                .batteryLevel(0.85)
                .lowBatteryThreshold(0.2)
                .maxBattery(1.0)
                .minBattery(0.0)
                .build();

        when(energySystemService.createEnergySystem(0.85)).thenReturn(energySystem);

        mockMvc.perform(post("/api/energy-systems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("batteryLevel", 0.85))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.batteryLevel").value(0.85));

        verify(energySystemService).createEnergySystem(0.85);
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
    @DisplayName("GET /api/energy-systems/{id} — не найден, возвращает 500")
    void shouldGetEnergySystemByIdReturn500WhenNotFound() throws Exception {
        when(energySystemService.getEnergySystemById(999L))
                .thenThrow(new SpaceOperationException("Энергосистема не найдена по id: 999"));

        mockMvc.perform(get("/api/energy-systems/999"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /api/energy-systems/{id}/consume — успешное потребление")
    void shouldConsumeEnergyReturnSuccess() throws Exception {
        when(energySystemService.consumeEnergy(1L, 10.0)).thenReturn(true);

        mockMvc.perform(post("/api/energy-systems/1/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("amount", 10.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(energySystemService).consumeEnergy(1L, 10.0);
    }

    @Test
    @DisplayName("POST /api/energy-systems/{id}/consume — недостаточно энергии")
    void shouldConsumeEnergyReturnFalseWhenInsufficient() throws Exception {
        when(energySystemService.consumeEnergy(1L, 100.0)).thenReturn(false);

        mockMvc.perform(post("/api/energy-systems/1/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("amount", 100.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));

        verify(energySystemService).consumeEnergy(1L, 100.0);
    }

    @Test
    @DisplayName("GET /api/energy-systems/{id}/power-status — проверка питания")
    void shouldGetPowerStatusReturn200() throws Exception {
        when(energySystemService.hasSufficientPower(1L)).thenReturn(true);

        mockMvc.perform(get("/api/energy-systems/1/power-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasSufficientPower").value(true));

        verify(energySystemService).hasSufficientPower(1L);
    }
}
