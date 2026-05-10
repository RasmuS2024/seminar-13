package seminars.domains.constellations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seminars.domains.satellites.CommunicationSatellite;
import seminars.domains.satellites.ImagingSatellite;
import seminars.exceptions.SpaceOperationException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Unit-тесты SatelliteConstellation")
class SatelliteConstellationTest {

    private SatelliteConstellation constellation;

    @BeforeEach
    void setUp() {
        constellation = new SatelliteConstellation("ТестоваяГруппировка");
    }

    @Test
    @DisplayName("Создание группировки должно корректно присваивать имя")
    void shouldCreateConstellationWithName() {
        assertEquals("ТестоваяГруппировка", constellation.getName());
        assertNotNull(constellation.getSatellites());
        assertTrue(constellation.getSatellites().isEmpty());
    }

    @Test
    @DisplayName("Добавление спутника должно связывать его с группировкой")
    void shouldAddSatelliteSuccessfully() {
        ImagingSatellite sat = new ImagingSatellite("ДЗЗ-1", 0.8, 5.5);

        constellation.addSatellite(sat);

        assertEquals(1, constellation.getSatellites().size());
        assertSame(constellation, sat.getConstellation());
    }

    @Test
    @DisplayName("Повторное добавление того же спутника не должно вызывать исключения")
    void shouldIgnoreDuplicateSatellite() {
        ImagingSatellite sat = new ImagingSatellite("ДЗЗ-1", 0.8, 5.5);

        constellation.addSatellite(sat);
        constellation.addSatellite(sat);

        assertEquals(1, constellation.getSatellites().size());
    }

    @Test
    @DisplayName("Добавление спутника из другой группировки должно выбрасывать исключение")
    void shouldThrowWhenSatelliteAlreadyInOtherConstellation() {
        SatelliteConstellation other = new SatelliteConstellation("ДругаяГруппировка");
        ImagingSatellite sat = new ImagingSatellite("ДЗЗ-1", 0.8, 5.5);
        other.addSatellite(sat);

        SpaceOperationException ex = assertThrows(SpaceOperationException.class,
                () -> constellation.addSatellite(sat));
        assertTrue(ex.getMessage().contains("ДругаяГруппировка"));
    }

    @Test
    @DisplayName("Удаление спутника должно очищать связь")
    void shouldRemoveSatellite() {
        ImagingSatellite sat = new ImagingSatellite("ДЗЗ-1", 0.8, 5.5);
        constellation.addSatellite(sat);

        constellation.removeSatellite(sat);

        assertEquals(0, constellation.getSatellites().size());
        assertNull(sat.getConstellation());
    }

    @Test
    @DisplayName("Удаление null не должно вызывать исключения")
    void shouldIgnoreNullOnRemove() {
        constellation.removeSatellite(null);
        assertTrue(constellation.getSatellites().isEmpty());
    }

    @Test
    @DisplayName("Активация должна включать все спутники")
    void shouldActivateAllSatellites() {
        constellation.addSatellite(new ImagingSatellite("ДЗЗ-1", 0.8, 5.5));
        constellation.addSatellite(new CommunicationSatellite("Связь-1", 0.9, 3.0));

        constellation.activateAllSatellites();

        constellation.getSatellites().forEach(sat ->
                assertTrue(sat.getState().isActive()));
    }

    @Test
    @DisplayName("Выполнение миссий должно вызываться для всех спутников")
    void shouldExecuteAllMissions() {
        ImagingSatellite sat = new ImagingSatellite("ДЗЗ-1", 0.8, 5.5);
        constellation.addSatellite(sat);
        sat.activate();

        constellation.executeAllMissions();

        assertTrue(sat.getState().isActive());
    }

    @Test
    @DisplayName("getAllSatellitesStatuses должен возвращать корректный отчёт")
    void shouldReturnCorrectStatusReport() {
        constellation.addSatellite(new ImagingSatellite("ДЗЗ-1", 0.8, 5.5));

        String status = constellation.getAllSatellitesStatuses();

        assertTrue(status.contains("СТАТУС"));
        assertTrue(status.contains("Количество спутников: 1"));
    }
}
