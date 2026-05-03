package seminars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import seminars.domains.constellations.SatelliteConstellation;
import seminars.repository.ConstellationRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mock-тесты для ConstellationRepository")
class ConstellationRepositoryMockTest {

    private static final String CONSTELLATION_NAME_1 = "testConstellation1";
    private static final String CONSTELLATION_NAME_2 = "testConstellation2";

    @Mock
    private ConstellationRepository repository;

    private SatelliteConstellation constellation1;
    private SatelliteConstellation constellation2;

    @BeforeEach
    void setUp() {
        constellation1 = new SatelliteConstellation(CONSTELLATION_NAME_1);
        constellation2 = new SatelliteConstellation(CONSTELLATION_NAME_2);
    }

    @Test
    @DisplayName("Добавление нескольких группировок должно сохранять их все")
    void testRepository() {
        Map<String, SatelliteConstellation> constellations = Map.of(
                CONSTELLATION_NAME_1, constellation1,
                CONSTELLATION_NAME_2, constellation2
        );

        when(repository.containsConstellation(CONSTELLATION_NAME_1)).thenReturn(true);
        when(repository.containsConstellation(CONSTELLATION_NAME_2)).thenReturn(true);
        when(repository.getAllConstellations()).thenReturn(constellations);

        assertTrue(repository.containsConstellation(CONSTELLATION_NAME_1));
        assertTrue(repository.containsConstellation(CONSTELLATION_NAME_2));
        assertEquals(2, repository.getAllConstellations().size());
    }

    @Test
    @DisplayName("addConstellation должен вызываться для сохранения группировки")
    void testAddConstellation() {
        // Arrange
        doNothing().when(repository).addConstellation(constellation1);

        // Act
        repository.addConstellation(constellation1);

        // Assert
        verify(repository, times(1)).addConstellation(constellation1);
    }

    @Test
    @DisplayName("getConstellationByName должен возвращать группировку по имени")
    void testGetConstellation() {
        // arrange
        when(repository.getConstellation(CONSTELLATION_NAME_1)).thenReturn(constellation1);

        // act
        SatelliteConstellation result = repository.getConstellation(CONSTELLATION_NAME_1);

        // assert
        assertNotNull(result);
        assertEquals(CONSTELLATION_NAME_1, result.getConstellationName());
        verify(repository, times(1)).getConstellation(CONSTELLATION_NAME_1);
    }

    @Test
    @DisplayName("updateConstellation должен вызываться с правильными параметрами")
    void testUpdateConstellation() {
        // arrange
        SatelliteConstellation updatedConstellation =
                new SatelliteConstellation(CONSTELLATION_NAME_1 + "_UPDATED");
        doNothing().when(repository).updateConstellation(CONSTELLATION_NAME_1, updatedConstellation);

        // act
        repository.updateConstellation(CONSTELLATION_NAME_1, updatedConstellation);

        // assert
        verify(repository, times(1))
                .updateConstellation(CONSTELLATION_NAME_1, updatedConstellation);
    }

    @Test
    @DisplayName("deleteConstellation должен вызываться для удаления группировки")
    void testDeleteConstellation() {
        // arrange
        doNothing().when(repository).deleteConstellation(CONSTELLATION_NAME_1);

        // act
        repository.deleteConstellation(CONSTELLATION_NAME_1);

        // assert
        verify(repository, times(1)).deleteConstellation(CONSTELLATION_NAME_1);
    }
}
