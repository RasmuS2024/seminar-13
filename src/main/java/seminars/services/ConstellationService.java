package seminars.services;

import org.springframework.stereotype.Service;
import seminars.domains.satellites.Satellite;
import seminars.SatelliteConstellation;
import seminars.repository.ConstellationRepository;

import java.util.Map;

@Service
public class ConstellationService {
    protected ConstellationRepository constellationRepository;

    public ConstellationService(ConstellationRepository constellationRepository) {
        this.constellationRepository = constellationRepository;
    }

    public void createAndSaveConstellation(String constellationName) {
        validateName(constellationName);

        this.constellationRepository.addConstellation(new SatelliteConstellation(constellationName));
    }

    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        validateName(constellationName);

        if (satellite == null) {
            throw new IllegalArgumentException("Спутник не может быть null");
        }

        SatelliteConstellation constellation = getConstellation(constellationName);
        constellation.addSatellite(satellite);
    }

    public void executeConstellationMission(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation constellation = getConstellation(constellationName);
        constellation.executeAllMissions();
    }

    public void activateAllSatellites(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation constellation = getConstellation(constellationName);
        constellation.activateAllSatellites();
    }

    public void showConstellationStatus(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation satelliteConstellation = getConstellation(constellationName);
        satelliteConstellation.getAllSatellitesStatuses();
    }

    public SatelliteConstellation getConstellation(String constellationName) {
        validateName(constellationName);

        return constellationRepository.getConstellation(constellationName);
    }

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return constellationRepository.getAllConstellations();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя группировки не может быть пустым");
        }
    }
}