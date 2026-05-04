package seminars.services;

import seminars.domains.constellations.SatelliteConstellation;
import java.util.List;

public interface ConstellationService {
    SatelliteConstellation createConstellation(String constellationName);
    SatelliteConstellation getConstellationByName(String constellationName);
    SatelliteConstellation getConstellationById(Long id);
    List<SatelliteConstellation> getAllConstellations();
    void deleteConstellation(Long id);
    void deleteConstellationByName(String name);
    void addSatelliteToConstellation(Long constellationId, Long satelliteId);
    void executeConstellationMission(String constellationName);
    void activateAllSatellites(String constellationName);
    void showConstellationStatus(String constellationName);
}
