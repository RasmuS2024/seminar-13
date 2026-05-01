package seminars.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.ImagingSatelliteParam;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.SatelliteParam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisplayName("Тесты сервиса спутников")
class SatelliteServiceTest {
    private static final String NAME = "TestSatellite";
    private static final double BATTERY_LEVEL = 0.6;
    private static final double PARAM = 0.1;

    @Autowired
    private SatelliteService satelliteService;

    @Test
    @DisplayName("Спутник ДЗЗ создается с нужными параметрами")
    void createSatelliteTest() {
        // Arrange
        SatelliteParam imagingParam = new ImagingSatelliteParam(NAME, BATTERY_LEVEL, PARAM);

        // Act
        Satellite satellite = satelliteService.createSatellite(imagingParam);

        // Assert
        assertNotNull(satellite);
        assertInstanceOf(ImagingSatellite.class, satellite);
        ImagingSatellite imagingSatellite = (ImagingSatellite) satellite;

        assertEquals(NAME, imagingSatellite.getName());
        assertEquals(BATTERY_LEVEL, imagingSatellite.getEnergy().getBatteryLevel());
        assertEquals(PARAM, imagingSatellite.getResolution());
    }


}
