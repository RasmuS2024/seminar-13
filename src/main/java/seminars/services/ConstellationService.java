package seminars.services;

import org.springframework.stereotype.Service;
import seminars.domains.satellites.Satellite;
import seminars.SatelliteConstellation;
import seminars.exceptions.SpaceOperationException;
import seminars.repository.ConstellationRepository;

@Service
public class ConstellationService {
    protected ConstellationRepository constellationRepository;

    public ConstellationService(ConstellationRepository constellationRepository) {
        this.constellationRepository = constellationRepository;
    }

    public void createAndSaveConstellation(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new SpaceOperationException("Имя группировки не может быть пустым");
        }
        this.constellationRepository.addConstellation(new SatelliteConstellation(name));
    }

    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        if (constellationName == null || constellationName.trim().isEmpty()) {
            throw new SpaceOperationException("Имя группировки не может быть пустым");
        }
        if (satellite == null) {
            throw new SpaceOperationException("Спутник не может быть null");
        }

        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        constellation.addSatellite(satellite);
    }

    public void executeConstellationMission(String constellationName) {
        if (constellationName == null || constellationName.trim().isEmpty()) {
            throw new SpaceOperationException("Имя группировки не может быть пустым");
        }

        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        constellation.executeAllMissions();
    }

    public void activateAllSatellites(String constellationName) {
        if (constellationName == null || constellationName.trim().isEmpty()) {
            throw new SpaceOperationException("Имя группировки не может быть пустым");
        }

        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        constellation.activateAllSatellites();
    }

    public void showConstellationStatus(String constellationName) {
        if (constellationName == null || constellationName.trim().isEmpty()) {
            throw new SpaceOperationException("Имя группировки не может быть пустым");
        }

        SatelliteConstellation satelliteConstellation = getConstellationOrThrow(constellationName);
        satelliteConstellation.getAllSatellitesStatuses();
    }

    private SatelliteConstellation getConstellationOrThrow(String constellationName) {
        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        if (constellation == null) {
            throw new SpaceOperationException("Группировка не найдена: " + constellationName);
        }
        return constellation;
    }
}