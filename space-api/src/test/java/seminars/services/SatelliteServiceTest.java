package seminars.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.params.CommunicationSatelliteParam;
import seminars.domains.satellites.params.ImagingSatelliteParam;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;
import seminars.domains.satellites.SatelliteType;
import seminars.exceptions.ResourceNotFoundException;
import seminars.exceptions.SpaceOperationException;
import seminars.factory.SatelliteFactory;
import seminars.repository.SatelliteRepository;
import seminars.repository.EnergySystemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты SatelliteService")
class SatelliteServiceTest {

    private static final String NAME = "TestSatellite";
    private static final double BATTERY_LEVEL = 0.6;
    private static final double RESOLUTION = 0.1;
    private static final Long SATELLITE_ID = 1L;

    @Mock
    private SatelliteRepository satelliteRepository;

    @Mock
    private EnergySystemRepository energySystemRepository;

    @Mock
    private SatelliteFactory imagingFactory;

    @Mock
    private TelemetryService telemetryService;

    private SatelliteServiceImpl satelliteService;

    @BeforeEach
    void setUp() {
        List<SatelliteFactory> factories = new ArrayList<>();
        factories.add(imagingFactory);
        satelliteService = new SatelliteServiceImpl(factories, satelliteRepository, telemetryService);
    }

    @Test
    @DisplayName("Спутник ДЗЗ создается с нужными параметрами")
    void createSatelliteTest() {
        SatelliteParam param = new ImagingSatelliteParam(NAME, BATTERY_LEVEL, RESOLUTION);
        Satellite satellite = new ImagingSatellite(NAME, BATTERY_LEVEL, RESOLUTION);

        when(imagingFactory.isSatelliteTypeSupported(SatelliteType.IMAGE)).thenReturn(true);
        when(imagingFactory.createSatelliteWithParameter(param)).thenReturn(satellite);
        when(satelliteRepository.save(satellite)).thenReturn(satellite);

        Satellite result = satelliteService.createSatellite(param);

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        verify(satelliteRepository).save(satellite);
    }

    @Test
    @DisplayName("createSatellite с null параметрами выбрасывает исключение")
    void createSatelliteWithNullParamThrowsException() {
        SpaceOperationException exception = assertThrows(SpaceOperationException.class,
                () -> satelliteService.createSatellite(null));
        assertEquals("Параметры спутника не могут быть null", exception.getMessage());
    }

    @Test
    @DisplayName("createSatellite с неподдерживаемым типом выбрасывает исключение")
    void createSatelliteWithUnsupportedTypeThrowsException() {
        SatelliteParam param = new ImagingSatelliteParam(NAME, BATTERY_LEVEL, RESOLUTION);

        when(imagingFactory.isSatelliteTypeSupported(SatelliteType.IMAGE)).thenReturn(false);

        assertThrows(SpaceOperationException.class,
                () -> satelliteService.createSatellite(param));
    }

    @Test
    @DisplayName("getSatelliteById возвращает спутник")
    void getSatelliteByIdReturnsSatellite() {
        Satellite satellite = new ImagingSatellite(NAME, BATTERY_LEVEL, RESOLUTION);
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.of(satellite));

        Satellite result = satelliteService.getSatelliteById(SATELLITE_ID);

        assertNotNull(result);
        assertEquals(NAME, result.getName());
    }

    @Test
    @DisplayName("getSatelliteById для несуществующего выбрасывает исключение")
    void getSatelliteByIdNotFoundThrowsException() {
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> satelliteService.getSatelliteById(SATELLITE_ID));
    }

    @Test
    @DisplayName("activateSatellite активирует спутник")
    void activateSatelliteActivatesSatellite() {
        Satellite satellite = new ImagingSatellite(NAME, BATTERY_LEVEL, RESOLUTION);
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.of(satellite));

        satelliteService.activateSatellite(SATELLITE_ID);

        assertTrue(satellite.getState().isActive());
    }

    @Test
    @DisplayName("deActivateSatellite деактивирует спутник")
    void deActivateSatelliteDeActivatesSatellite() {
        Satellite satellite = new ImagingSatellite(NAME, BATTERY_LEVEL, RESOLUTION);
        satellite.activate();
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.of(satellite));

        satelliteService.deActivateSatellite(SATELLITE_ID);

        assertFalse(satellite.getState().isActive());
    }

    @Test
    @DisplayName("performSatelliteMission выполняет миссию спутника")
    void performSatelliteMissionPerformsMission() {
        Satellite satellite = new ImagingSatellite(NAME, BATTERY_LEVEL, RESOLUTION);
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.of(satellite));

        satelliteService.performSatelliteMission(SATELLITE_ID);

        assertFalse(satellite.getState().isActive());
    }

    @Test
    @DisplayName("getSatelliteStatus возвращает статус")
    void getSatelliteStatusReturnsStatus() {
        Satellite satellite = new ImagingSatellite(NAME, BATTERY_LEVEL, RESOLUTION);
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.of(satellite));

        String status = satelliteService.getSatelliteStatus(SATELLITE_ID);

        assertNotNull(status);
    }

    @Test
    @DisplayName("getAllSatellites возвращает список всех спутников")
    void getAllSatellitesReturnsAllSatellites() {
        Satellite satellite1 = new ImagingSatellite("Sat1", 0.8, 1.0);
        Satellite satellite2 = new ImagingSatellite("Sat2", 0.9, 2.0);
        when(satelliteRepository.findAll()).thenReturn(List.of(satellite1, satellite2));

        List<Satellite> result = satelliteService.getAllSatellites();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getSatellitesByConstellationId возвращает спутники группировки")
    void getSatellitesByConstellationIdReturnsSatellites() {
        Long constellationId = 10L;
        Satellite satellite = new ImagingSatellite(NAME, BATTERY_LEVEL, RESOLUTION);
        when(satelliteRepository.findByConstellationId(constellationId)).thenReturn(List.of(satellite));

        List<Satellite> result = satelliteService.getSatellitesByConstellationId(constellationId);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("deleteSatellite удаляет спутник")
    void deleteSatelliteDeletesSatellite() {
        when(satelliteRepository.existsById(SATELLITE_ID)).thenReturn(true);
        doNothing().when(satelliteRepository).deleteById(SATELLITE_ID);

        satelliteService.deleteSatellite(SATELLITE_ID);

        verify(satelliteRepository).deleteById(SATELLITE_ID);
    }

    @Test
    @DisplayName("deleteSatellite для несуществующего выбрасывает исключение")
    void deleteSatelliteNotFoundThrowsException() {
        when(satelliteRepository.existsById(SATELLITE_ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> satelliteService.deleteSatellite(SATELLITE_ID));
        verify(satelliteRepository, never()).deleteById(SATELLITE_ID);
    }
}
