package seminars.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import seminars.dto.ConstellationStatusResponse;
import seminars.dto.SatelliteStatusResponse;
import seminars.services.ConstellationService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConstellationController.class)
@DisplayName("Unit-тесты ConstellationController")
class ConstellationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConstellationService constellationService;

    @Test
    @DisplayName("PATCH /api/constellations/{name}/satellites/{name} — возвращает 200 с JSON")
    void shouldAddSatelliteToConstellationReturn200() throws Exception {
        ConstellationStatusResponse response = new ConstellationStatusResponse(
                1L, "TestConstellation", 1, 0,
                List.of(new SatelliteStatusResponse(1L, "TestSat", "ImagingSatellite", false, 0.8)));
        doNothing().when(constellationService).addSatelliteToConstellation(anyString(), anyString());
        when(constellationService.getConstellationStatus("TestConstellation")).thenReturn(response);

        mockMvc.perform(patch("/api/constellations/TestConstellation/satellites/TestSat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestConstellation"))
                .andExpect(jsonPath("$.satelliteCount").value(1));

        verify(constellationService).addSatelliteToConstellation("TestConstellation", "TestSat");
        verify(constellationService).getConstellationStatus("TestConstellation");
    }
}
