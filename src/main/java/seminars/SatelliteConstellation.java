package seminars;

import lombok.Getter;
import org.springframework.stereotype.Service;
import seminars.domains.satellites.Satellite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Service
public class SatelliteConstellation {
    private final String constellationName;
    private final List<Satellite> satellites = new ArrayList<>();

    public SatelliteConstellation() {
        this.constellationName = "";
    }

    public SatelliteConstellation(String constellationName) {
        this.constellationName = constellationName;
        System.out.println("Создана спутниковая группировка: " + constellationName);
    }

    public void addSatellite(Satellite satellite) {
        if (satellite != null && !satellites.contains(satellite)) {
            satellites.add(satellite);
            System.out.println(satellite.getName() + " добавлен в группировку \"" + constellationName + "\"");
        }
    }

    public void activateAllSatellites() {
        System.out.println("АКТИВАЦИЯ СПУТНИКОВ ГРУППИРОВКИ " + constellationName.toUpperCase());
        System.out.println("=".repeat(50));

        for (Satellite satellite : satellites) {
            satellite.activate();
        }
    }

    public void executeAllMissions() {
        System.out.println("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ " + constellationName.toUpperCase());
        System.out.println("=".repeat(50));

        for (Satellite satellite : satellites) {
            satellite.performMission();
        }
    }

    public void getAllSatellitesStatuses() {
        System.out.println("СТАТУС ГРУППИРОВКИ: " + constellationName.toUpperCase());
        System.out.println("=".repeat(50));

        for (Satellite satellite : satellites) {
            System.out.println(satellite.getState());
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
