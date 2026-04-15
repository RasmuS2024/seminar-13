package seminars.services;

import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.SatelliteParam;

public interface SatelliteService {
    Satellite createSatellite(SatelliteParam param);
}
