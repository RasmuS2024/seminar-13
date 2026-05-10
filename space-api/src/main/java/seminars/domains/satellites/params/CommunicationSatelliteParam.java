package seminars.domains.satellites.params;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import seminars.domains.satellites.SatelliteType;

import static seminars.constants.CommunicationSatelliteConstants.DEFAULT_BANDWIDTH;

@Getter
public class CommunicationSatelliteParam extends SatelliteParam {
    private final double bandwidth;

    public CommunicationSatelliteParam(String name, double batteryLevel, double bandwidth) {
        super(SatelliteType.COMMUNICATION, name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    @JsonCreator
    public CommunicationSatelliteParam(
            @JsonProperty("name") String name,
            @JsonProperty("batteryLevel") double batteryLevel,
            @JsonProperty("bandwidth") Double bandwidth
    ) {
        super(SatelliteType.COMMUNICATION, name, batteryLevel);
        this.bandwidth = bandwidth != null ? bandwidth : DEFAULT_BANDWIDTH;
    }
}
