package seminars.services;

import org.springframework.stereotype.Service;
import seminars.domains.satellites.Satellite;
import seminars.SatelliteConstellation;
import seminars.repository.ConstellationRepository;

@Service
public class SpaceOperationCenterService {
    protected ConstellationRepository constellationRepository;

    public SpaceOperationCenterService(ConstellationRepository constellationRepository) {
        this.constellationRepository = constellationRepository;
    }

    public void createAndSaveConstellation(String name) {
        this.constellationRepository.addConstellation(new SatelliteConstellation(name));
    }

    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        SatelliteConstellation constellation = this.constellationRepository.getConstellation(constellationName);
        constellation.addSatellite(satellite);
    }

    public void executeConstellationMission(String constellationName) {
        SatelliteConstellation constellation = this.constellationRepository.getConstellation(constellationName);
        constellation.executeAllMissions();
    }

    public void activateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = this.constellationRepository.getConstellation(constellationName);
        constellation.activateAllSatellites();
    }

    public void showConstellationStatus(String constellationName) {
        SatelliteConstellation satelliteConstellation = constellationRepository.getConstellation(constellationName);
        satelliteConstellation.getAllSatellitesStatuses();
    }

}
