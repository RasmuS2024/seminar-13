package seminars.dto;

import java.util.List;

public record ConstellationStatusResponse(
        Long id,
        String name,
        int satelliteCount,
        int activeCount,
        List<SatelliteStatusResponse> satellites
) { }
