package seminars.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;
import seminars.dto.SatelliteStatusResponse;

import java.util.List;
import java.util.Optional;

public interface SatelliteService {
    Satellite createSatellite(SatelliteParam param);
    Satellite getSatelliteById(Long id);
    Satellite getSatelliteByName(String name);
    List<Satellite> getAllSatellites();
    List<Satellite> getSatellitesByConstellationId(Long constellationId);
    void deleteSatellite(Long id);
    SatelliteStatusResponse activateSatellite(Long satelliteId);
    SatelliteStatusResponse deActivateSatellite(Long satelliteId);
    SatelliteStatusResponse performSatelliteMission(Long satelliteId);
    SatelliteStatusResponse getSatelliteStatus(Long satelliteId);
    Satellite updateSatellite(Long id, SatelliteParam param);

    @Cacheable(value = "satellite", key = "#constellationName + '::' + #satelliteName")
    @Transactional(readOnly = true)
    Optional<Satellite> findByName(String constellationName, String satelliteName);
}
