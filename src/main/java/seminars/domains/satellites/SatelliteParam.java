package seminars.domains.satellites;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SatelliteParam {
    private  SatelliteType type;
    private String name;
    private double batteryLevel;
}
