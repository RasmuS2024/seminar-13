package seminars.domains.satellites.params;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import seminars.domains.satellites.SatelliteType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CommunicationSatelliteParam.class, name = "COMMUNICATION"),
    @JsonSubTypes.Type(value = ImagingSatelliteParam.class, name = "IMAGE")
})

@AllArgsConstructor
@Getter
public class SatelliteParam {
    private SatelliteType type;
    private String name;
    private double batteryLevel;
}
