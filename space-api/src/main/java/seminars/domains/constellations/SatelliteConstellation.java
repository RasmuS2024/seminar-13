package seminars.domains.constellations;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seminars.domains.satellites.Satellite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "satellite_constellation")
@Getter
@Setter
@NoArgsConstructor
public class SatelliteConstellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String constellationName;

    @JsonManagedReference
    @OneToMany(mappedBy = "constellation", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Satellite> satellites = new ArrayList<>();

    public SatelliteConstellation(String constellationName) {
        this.constellationName = constellationName;
    }

    public void addSatellite(Satellite satellite) {
        if (satellite != null && !satellites.contains(satellite)) {
            satellites.add(satellite);
            satellite.setConstellation(this);
        }
    }

    public void removeSatellite(Satellite satellite) {
        if (satellite != null) {
            satellite.setConstellation(null);
            satellites.remove(satellite);
        }
    }

    public void activateAllSatellites() {
        for (Satellite satellite : satellites) {
            satellite.activate();
        }
    }

    public void executeAllMissions() {
        for (Satellite satellite : satellites) {
            satellite.performMission();
        }
    }

    public String getAllSatellitesStatuses() {
        StringBuilder sb = new StringBuilder();
        sb.append("СТАТУС ГРУППИРОВКИ: ").append(constellationName.toUpperCase()).append("\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append("Количество спутников: ").append(satellites.size()).append("\n");

        for (Satellite satellite : satellites) {
            sb.append(satellite.getState().toString()).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "SatelliteConstellation{constellationName='"
                + constellationName
                + "', satellites="
                + satellites.stream()
                        .map(Satellite::toString)
                        .collect(Collectors.joining(",\n", "[\n", "]"))
                + "}";
    }
}
