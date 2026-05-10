package seminars.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seminars.domains.satellites.CommunicationSatellite;
import seminars.domains.satellites.params.CommunicationSatelliteParam;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.params.ImagingSatelliteParam;
import seminars.domains.satellites.Satellite;
import seminars.factory.impl.CommunicationSatelliteFactory;
import seminars.factory.impl.ImagingSatelliteFactory;

@DisplayName("Тесты фабрик спутников")
class SatelliteFactoryTest {
    private static CommunicationSatelliteFactory communicationFactory;
    private static ImagingSatelliteFactory imagingFactory;

    @BeforeAll
    static void setUp() {
        communicationFactory = new CommunicationSatelliteFactory();
        imagingFactory = new ImagingSatelliteFactory();
    }

    @Test
    @DisplayName("Фабрика спутников связи создает спутник с параметрами")
    void communicationFactoryCreateSatelliteWithParameters() {
        // Arrange
        String name = "Спутник связи 1";
        double batteryLevel = 0.8;
        double bandwidth = 125;

        // Act
        Satellite satellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam(name, batteryLevel, bandwidth)
        );

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
        Satellite satellite = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam(name, batteryLevel, resolution)
        );

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
        Satellite commSatellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam("АктивныйКомСат", 0.9, 150.0)
        );
        Satellite imagingSatellite = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam("АктивныйДЗЗ", 0.9, 20.0)
        );
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
        Satellite commSatellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam("НизкийЗарядКомСат", 0.1, 150.0)
        );
        Satellite imagingSatellite = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam("НизкийЗарядДЗЗ", 0.001, 20.0)
        );
        // Act & Assert
        assertFalse(commSatellite.activate());
        assertFalse(commSatellite.getState().isActive());

        assertFalse(imagingSatellite.activate());
        assertFalse(imagingSatellite.getState().isActive());

    }

    @Test
    @DisplayName("Фабрики реализуют общий интерфейс SatelliteFactory")
    void factoriesImplementCommonInterface() {
        // Arrange
        String imagingName = "ДЗЗ-1";
        String commName = "Связь-1";
        double batteryLevel = 0.9;
        double resolution = 2.5;
        double bandwidth = 100.0;

        // Act
        Satellite imagingSatellite = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam(imagingName, batteryLevel, resolution)
        );

        Satellite commSatellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam(commName, batteryLevel, bandwidth)
        );

        // Assert
        assertNotNull(imagingSatellite, "Спутник связи должен быть создан");
        assertNotNull(commSatellite, "Спутник ДЗЗ должен быть создан");
        assertInstanceOf(Satellite.class, imagingSatellite);
        assertInstanceOf(Satellite.class, commSatellite);
    }

}
