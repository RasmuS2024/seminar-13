package seminars;

import seminars.domains.satellites.Satellite;

public abstract class SatelliteFactory {
    public abstract Satellite createSatellite(String name, double batteryLevel);
}
