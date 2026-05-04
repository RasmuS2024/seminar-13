package seminars.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import seminars.domains.constellations.SatelliteConstellation;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.Satellite;
import seminars.exceptions.SpaceOperationException;
import seminars.repository.ConstellationRepository;
import seminars.repository.SatelliteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты ConstellationService")
class ConstellationServiceTest {

    private static final String CONSTELLATION_NAME = "TestConstellation";
    private static final String SATELLITE_NAME = "TestSatellite";
    private static final Long CONSTELLATION_ID = 1L;
    private static final Long SATELLITE_ID = 2L;

    @Mock
    private ConstellationRepository constellationRepository;

    @Mock
    private SatelliteRepository satelliteRepository;

    @InjectMocks
    private ConstellationServiceImpl constellationService;

    @Test
    @DisplayName("createConstellation создаёт группировку")
    void createConstellationCreatesConstellation() {
        when(constellationRepository.existsByName(CONSTELLATION_NAME)).thenReturn(false);
        when(constellationRepository.save(any(SatelliteConstellation.class))).thenAnswer(invocation -> {
            SatelliteConstellation saved = invocation.getArgument(0);
            return saved;
        });

        SatelliteConstellation result = constellationService.createConstellation(CONSTELLATION_NAME);

        assertNotNull(result);
        assertEquals(CONSTELLATION_NAME, result.getName());
        verify(constellationRepository).save(any(SatelliteConstellation.class));
    }

    @Test
    @DisplayName("createConstellation с null именем выбрасывает исключение")
    void createConstellationWithNullNameThrowsException() {
        assertThrows(SpaceOperationException.class,
                () -> constellationService.createConstellation(null));
    }

    @Test
    @DisplayName("createConstellation с пустым именем выбрасывает исключение")
    void createConstellationWithBlankNameThrowsException() {
        assertThrows(SpaceOperationException.class,
                () -> constellationService.createConstellation(""));
    }

    @Test
    @DisplayName("createConstellation с дубликатом имени выбрасывает исключение")
    void createConstellationWithDuplicateNameThrowsException() {
        when(constellationRepository.existsByName(CONSTELLATION_NAME)).thenReturn(true);

        assertThrows(SpaceOperationException.class,
                () -> constellationService.createConstellation(CONSTELLATION_NAME));
        verify(constellationRepository, never()).save(any());
    }

    @Test
    @DisplayName("getConstellationByName возвращает группировку")
    void getConstellationByNameReturnsConstellation() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_NAME);
        when(constellationRepository.findByName(CONSTELLATION_NAME))
                .thenReturn(Optional.of(constellation));

        SatelliteConstellation result = constellationService.getConstellationByName(CONSTELLATION_NAME);

        assertNotNull(result);
        assertEquals(CONSTELLATION_NAME, result.getName());
    }

    @Test
    @DisplayName("getConstellationByName для несуществующей выбрасывает исключение")
    void getConstellationByNameNotFoundThrowsException() {
        when(constellationRepository.findByName(CONSTELLATION_NAME))
                .thenReturn(Optional.empty());

        assertThrows(SpaceOperationException.class,
                () -> constellationService.getConstellationByName(CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("getConstellationById возвращает группировку")
    void getConstellationByIdReturnsConstellation() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_NAME);
        when(constellationRepository.findById(CONSTELLATION_ID)).thenReturn(Optional.of(constellation));

        SatelliteConstellation result = constellationService.getConstellationById(CONSTELLATION_ID);

        assertNotNull(result);
    }

    @Test
    @DisplayName("getConstellationById для несуществующей выбрасывает исключение")
    void getConstellationByIdNotFoundThrowsException() {
        when(constellationRepository.findById(CONSTELLATION_ID)).thenReturn(Optional.empty());

        assertThrows(SpaceOperationException.class,
                () -> constellationService.getConstellationById(CONSTELLATION_ID));
    }

    @Test
    @DisplayName("getAllConstellations возвращает все группировки")
    void getAllConstellationsReturnsAll() {
        SatelliteConstellation c1 = new SatelliteConstellation("C1");
        SatelliteConstellation c2 = new SatelliteConstellation("C2");
        when(constellationRepository.findAll()).thenReturn(List.of(c1, c2));

        List<SatelliteConstellation> result = constellationService.getAllConstellations();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("deleteConstellation удаляет группировку")
    void deleteConstellationDeletesConstellation() {
        when(constellationRepository.existsById(CONSTELLATION_ID)).thenReturn(true);
        doNothing().when(constellationRepository).deleteById(CONSTELLATION_ID);

        constellationService.deleteConstellation(CONSTELLATION_ID);

        verify(constellationRepository).deleteById(CONSTELLATION_ID);
    }

    @Test
    @DisplayName("deleteConstellation для несуществующей выбрасывает исключение")
    void deleteConstellationNotFoundThrowsException() {
        when(constellationRepository.existsById(CONSTELLATION_ID)).thenReturn(false);

        assertThrows(SpaceOperationException.class,
                () -> constellationService.deleteConstellation(CONSTELLATION_ID));
        verify(constellationRepository, never()).deleteById(CONSTELLATION_ID);
    }

    @Test
    @DisplayName("deleteConstellationByName удаляет группировку по имени")
    void deleteConstellationByNameDeletesConstellation() {
        when(constellationRepository.existsByName(CONSTELLATION_NAME)).thenReturn(true);
        doNothing().when(constellationRepository).deleteByName(CONSTELLATION_NAME);

        constellationService.deleteConstellationByName(CONSTELLATION_NAME);

        verify(constellationRepository).deleteByName(CONSTELLATION_NAME);
    }

    @Test
    @DisplayName("addSatelliteToConstellation добавляет спутник в группировку")
    void addSatelliteToConstellationAddsSatellite() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_NAME);
        Satellite satellite = new ImagingSatellite(SATELLITE_NAME, 0.8, 1.0);

        when(constellationRepository.findById(CONSTELLATION_ID)).thenReturn(Optional.of(constellation));
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.of(satellite));
        when(satelliteRepository.save(satellite)).thenReturn(satellite);

        constellationService.addSatelliteToConstellation(CONSTELLATION_ID, SATELLITE_ID);

        verify(satelliteRepository).save(satellite);
        assertTrue(constellation.getSatellites().contains(satellite));
    }

    @Test
    @DisplayName("addSatelliteToConstellation для несуществующей группировки выбрасывает исключение")
    void addSatelliteToConstellationWithNonExistentConstellationThrowsException() {
        when(constellationRepository.findById(CONSTELLATION_ID)).thenReturn(Optional.empty());

        assertThrows(SpaceOperationException.class,
                () -> constellationService.addSatelliteToConstellation(CONSTELLATION_ID, SATELLITE_ID));
    }

    @Test
    @DisplayName("addSatelliteToConstellation для несуществующего спутника выбрасывает исключение")
    void addSatelliteToConstellationWithNonExistentSatelliteThrowsException() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_NAME);
        when(constellationRepository.findById(CONSTELLATION_ID)).thenReturn(Optional.of(constellation));
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.empty());

        assertThrows(SpaceOperationException.class,
                () -> constellationService.addSatelliteToConstellation(CONSTELLATION_ID, SATELLITE_ID));
    }

    @Test
    @DisplayName("addSatelliteToConstellation для спутника в другой группировке выбрасывает исключение")
    void addSatelliteToConstellationWithSatelliteInOtherConstellationThrowsException() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_NAME);
        SatelliteConstellation otherConstellation = new SatelliteConstellation("OtherConstellation");
        Satellite satellite = new ImagingSatellite(SATELLITE_NAME, 0.8, 1.0);
        satellite.setConstellation(otherConstellation);

        when(constellationRepository.findById(CONSTELLATION_ID)).thenReturn(Optional.of(constellation));
        when(satelliteRepository.findById(SATELLITE_ID)).thenReturn(Optional.of(satellite));

        assertThrows(SpaceOperationException.class,
                () -> constellationService.addSatelliteToConstellation(CONSTELLATION_ID, SATELLITE_ID));
    }

    @Test
    @DisplayName("activateAllSatellites активирует все спутники")
    void activateAllSatellitesActivatesAll() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_NAME);
        Satellite satellite = new ImagingSatellite(SATELLITE_NAME, 0.8, 1.0);
        constellation.addSatellite(satellite);

        when(constellationRepository.findByName(CONSTELLATION_NAME))
                .thenReturn(Optional.of(constellation));

        constellationService.activateAllSatellites(CONSTELLATION_NAME);

        assertTrue(satellite.getState().isActive());
    }

    @Test
    @DisplayName("executeConstellationMission выполняет миссии всех спутников")
    void executeConstellationMissionExecutesAllMissions() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_NAME);
        Satellite satellite = new ImagingSatellite(SATELLITE_NAME, 0.8, 1.0);
        constellation.addSatellite(satellite);

        when(constellationRepository.findByName(CONSTELLATION_NAME))
                .thenReturn(Optional.of(constellation));

        constellationService.executeConstellationMission(CONSTELLATION_NAME);

        assertFalse(satellite.getState().isActive());
    }
}
