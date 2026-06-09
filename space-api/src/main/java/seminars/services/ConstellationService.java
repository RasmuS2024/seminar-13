package seminars.services;

import seminars.domains.constellations.SatelliteConstellation;
import seminars.dto.ConstellationStatusResponse;

import java.util.List;

public interface ConstellationService {
    SatelliteConstellation createConstellation(String constellationName);
    SatelliteConstellation getConstellationByName(String constellationName);
    SatelliteConstellation getConstellationById(Long id);
    List<SatelliteConstellation> getAllConstellations();
    List<SatelliteConstellation> getAllConstellationsWithSatellites();
    void deleteConstellation(String name);
    void addSatelliteToConstellation(Long constellationId, Long satelliteId);
    void addSatelliteToConstellation(String constellationName, String satelliteName);
    void removeSatelliteFromConstellation(String constellationName, String satelliteName);
    ConstellationStatusResponse executeConstellationMission(String constellationName);
    ConstellationStatusResponse activateAllSatellites(String constellationName);
    void showConstellationStatus(String constellationName);
    ConstellationStatusResponse getConstellationStatus(String constellationName);
    void renameConstellation(String oldName, String newName);
}
