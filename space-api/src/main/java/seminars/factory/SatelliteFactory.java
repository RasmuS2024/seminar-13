package seminars.factory;

import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;
import seminars.domains.satellites.SatelliteType;

public interface SatelliteFactory {
    Satellite createSatelliteWithParameter(SatelliteParam param);

    boolean isSatelliteTypeSupported(SatelliteType type);
}
