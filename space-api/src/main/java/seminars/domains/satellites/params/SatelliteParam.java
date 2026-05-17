package seminars.domains.satellites.params;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import seminars.constants.EnergySystemConstants;
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

    @NotBlank(message = "Имя спутника не может быть пустым")
    private String name;

    @DecimalMin(EnergySystemConstants.MIN_BATTERY_STR)
    @DecimalMax(EnergySystemConstants.MAX_BATTERY_STR)
    private double batteryLevel;
}
