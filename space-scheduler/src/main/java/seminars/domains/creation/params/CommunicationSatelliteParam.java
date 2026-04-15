package seminars.domains.creation.params;

import lombok.Getter;

@Getter
public class CommunicationSatelliteParam extends SatelliteParam {
    private final double bandwidth;

    public CommunicationSatelliteParam(String name, double batteryLevel, double bandwidth) {
        super(SatelliteType.COMMUNICATION, name, batteryLevel);
        this.bandwidth = bandwidth;
    }
}
