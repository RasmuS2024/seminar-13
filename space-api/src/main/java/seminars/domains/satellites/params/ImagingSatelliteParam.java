package seminars.domains.satellites.params;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import seminars.domains.satellites.SatelliteType;

import static seminars.constants.ImagingSatelliteConstants.DEFAULT_RESOLUTION;

@Getter
public class ImagingSatelliteParam extends SatelliteParam {
    private final double resolution;

    @JsonCreator
    public ImagingSatelliteParam(
            @JsonProperty("name") String name,
            @JsonProperty("batteryLevel") double batteryLevel,
            @JsonProperty("resolution") Double resolution
    ) {
        super(SatelliteType.IMAGE, name, batteryLevel);
        this.resolution = resolution != null ? resolution : DEFAULT_RESOLUTION;
    }
}
