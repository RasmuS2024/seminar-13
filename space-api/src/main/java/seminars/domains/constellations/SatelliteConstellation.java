package seminars.domains.constellations;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seminars.domains.satellites.Satellite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SatelliteConstellation {
    private static final Logger log = LoggerFactory.getLogger(SatelliteConstellation.class);
    private final String constellationName;
    private final List<Satellite> satellites = new ArrayList<>();

    public SatelliteConstellation() {
        this.constellationName = "";
    }

    public SatelliteConstellation(String constellationName) {
        this.constellationName = constellationName;
        log.info("Создана спутниковая группировка: {}", constellationName);
    }

    public void addSatellite(Satellite satellite) {
        if (satellite != null && !satellites.contains(satellite)) {
            satellites.add(satellite);
            log.info("{} добавлен в группировку \"{}\"", satellite.getName(), constellationName);
        }
    }

    public void activateAllSatellites() {
        if (log.isInfoEnabled()) {
            log.info("АКТИВАЦИЯ СПУТНИКОВ ГРУППИРОВКИ {}", constellationName.toUpperCase());
            log.info("=".repeat(50));
        }

        for (Satellite satellite : satellites) {
            satellite.activate();
        }
    }

    public void executeAllMissions() {
        if (log.isInfoEnabled()) {
            log.info("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ {}", constellationName.toUpperCase());
            log.info("=".repeat(50));
        }

        for (Satellite satellite : satellites) {
            satellite.performMission();
        }
    }

    public void getAllSatellitesStatuses() {
        if (log.isInfoEnabled()) {
            log.info("СТАТУС ГРУППИРОВКИ: {}", constellationName.toUpperCase());
            log.info("=".repeat(50));
            log.info("Количество спутников: {}", satellites.size());
        }

        for (Satellite satellite : satellites)
            if (log.isInfoEnabled()) {
                log.info(satellite.getState().toString());
            }
    }

    @Override
    public String toString() {
        return "SatelliteConstellation{constellationName='" +
                constellationName +
                "', satellites=" +
                satellites.stream()
                        .map(Satellite::toString)
                        .collect(Collectors.joining(",\n", "[\n", "]")) +
                "}";
    }
}