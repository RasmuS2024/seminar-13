package seminars.factory.impl;

import org.springframework.stereotype.Component;
import seminars.domains.satellites.CommunicationSatellite;
import seminars.domains.satellites.Satellite;
import seminars.factory.SatelliteFactory;

@Component
public class CommunicationSatelliteFactory implements SatelliteFactory {

    private static final double DEFAULT_BANDWIDTH = 100.0;

    @Override
    public Satellite createSatellite(String name, double batteryLevel) {
        return new CommunicationSatellite(name, batteryLevel, DEFAULT_BANDWIDTH);
    }

    @Override
    public Satellite createSatelliteWithParameter(String name, double batteryLevel, double parameter) {
        return new CommunicationSatellite(name, batteryLevel, parameter);
    }
}