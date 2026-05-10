package seminars.services;

import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;

import java.util.List;

public interface SatelliteService {
    Satellite createSatellite(SatelliteParam param);
    Satellite getSatelliteById(Long id);
    Satellite getSatelliteByName(String name);
    List<Satellite> getAllSatellites();
    List<Satellite> getSatellitesByConstellationId(Long constellationId);
    void deleteSatellite(Long id);
    void activateSatellite(Long satelliteId);
    void deActivateSatellite(Long satelliteId);
    void performSatelliteMission(Long satelliteId);
    String getSatelliteStatus(Long satelliteId);
    Satellite updateSatellite(Long id, SatelliteParam param);
}
