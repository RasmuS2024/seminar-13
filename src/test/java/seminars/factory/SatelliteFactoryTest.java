package seminars.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import seminars.domains.satellites.CommunicationSatellite;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.Satellite;
import seminars.factory.impl.CommunicationSatelliteFactory;
import seminars.factory.impl.ImagingSatelliteFactory;

@DisplayName("Тесты фабрик спутников")
public class SatelliteFactoryTest {
    private static CommunicationSatelliteFactory communicationFactory;
    private static ImagingSatelliteFactory imagingFactory;

    private static final double DEFAULT_BANDWIDTH = 100.0;
    private static final double DEFAULT_RESOLUTION = 100.0;

    @BeforeAll
    static void setUp() {
        communicationFactory = new CommunicationSatelliteFactory();
        imagingFactory = new ImagingSatelliteFactory();
    }

    @Test
    @DisplayName("Фабрика спутников связи создает спутник с дефолтными параметрами")
    void communicationFactoryCreateSatelliteWithDefaultParameters() {
        // Arrange
        String name = "Спутник связи 1";
        double batteryLevel = 0.8;

        // Act
        Satellite satellite = communicationFactory.createSatellite(name, batteryLevel);

        // Assert
        assertNotNull(satellite);
        assertInstanceOf(CommunicationSatellite.class, satellite);
        assertEquals(name, satellite.getName());
        assertEquals(batteryLevel, satellite.getEnergy().getBatteryLevel(), 0.001);

        CommunicationSatellite commSatellite = (CommunicationSatellite) satellite;
        assertEquals(DEFAULT_BANDWIDTH, commSatellite.getBandwidth(), 0.001);
    }

    @Test
    @DisplayName("Фабрика спутников ДЗЗ создает спутник с дефолтными параметрами")
    void imagingFactoryCreateSatelliteWithDefaultParameters() {
        // Arrange
        String name = "Спутник ДЗЗ 1";
        double batteryLevel = 0.8;

        // Act
        Satellite satellite = imagingFactory.createSatellite(name, batteryLevel);

        // Assert
        assertNotNull(satellite);
        assertInstanceOf(ImagingSatellite.class, satellite);
        assertEquals(name, satellite.getName());
        assertEquals(batteryLevel, satellite.getEnergy().getBatteryLevel(), 0.001);

        ImagingSatellite imagingSatellite = (ImagingSatellite) satellite;
        assertEquals(DEFAULT_RESOLUTION, imagingSatellite.getResolution(), 0.001);
    }

    @Test
    @DisplayName("Фабрика спутников связи создает спутник с параметрами")
    void communicationFactoryCreateSatelliteWithParameters() {
        // Arrange
        String name = "Спутник связи 1";
        double batteryLevel = 0.8;
        double bandwidth = 125;

        // Act
        Satellite satellite = communicationFactory.createSatelliteWithParameter(name, batteryLevel, bandwidth);

        // Assert
        assertNotNull(satellite);
        assertInstanceOf(CommunicationSatellite.class, satellite);
        assertEquals(name, satellite.getName());
        assertEquals(batteryLevel, satellite.getEnergy().getBatteryLevel(), 0.001);

        CommunicationSatellite commSatellite = (CommunicationSatellite) satellite;
        assertEquals(bandwidth, commSatellite.getBandwidth(), 0.001);
    }

    @Test
    @DisplayName("Фабрика спутников ДЗЗ создает спутник с параметрами")
    void imagingFactoryCreateSatelliteWithParameters() {
        // Arrange
        String name = "Спутник ДЗЗ 1";
        double batteryLevel = 0.8;
        double resolution = 48;

        // Act
        Satellite satellite = imagingFactory.createSatelliteWithParameter(name, batteryLevel, resolution);

        // Assert
        assertNotNull(satellite);
        assertInstanceOf(ImagingSatellite.class, satellite);
        assertEquals(name, satellite.getName());
        assertEquals(batteryLevel, satellite.getEnergy().getBatteryLevel(), 0.001);

        ImagingSatellite imagingSatellite = (ImagingSatellite) satellite;
        assertEquals(resolution, imagingSatellite.getResolution(), 0.001);
    }

    @Test
    @DisplayName("Созданные фабриками спутники могут быть активированы")
    void factoryCreatedSatellitesCanBeActivated() {
        // Arrange
        Satellite commSatellite = communicationFactory.createSatellite("АктивныйКомСат", 0.9);
        Satellite imagingSatellite = imagingFactory.createSatellite("АктивныйДЗЗ", 0.9);

        // Act & Assert
        assertTrue(commSatellite.activate());
        assertTrue(commSatellite.getState().isActive());

        assertTrue(imagingSatellite.activate());
        assertTrue(imagingSatellite.getState().isActive());

    }

    @Test
    @DisplayName("Созданные фабриками спутники с низким зарядом не могут быть активированы")
    void factoryCreatedSatellitesWithLowBatteryNotBeActivated() {
        // Arrange
        Satellite commSatellite = communicationFactory.createSatellite("АктивныйКомСат", 0.1);
        Satellite imagingSatellite = imagingFactory.createSatellite("АктивныйДЗЗ", 0.001);

        // Act & Assert
        assertFalse(commSatellite.activate());
        assertFalse(commSatellite.getState().isActive());

        assertFalse(imagingSatellite.activate());
        assertFalse(imagingSatellite.getState().isActive());

    }

}
