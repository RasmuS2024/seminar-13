package seminars.dto;

import java.util.List;

public record AddSatellitesResponse(
        String constellationName,
        List<SatelliteStatusResponse> satellites
) {}
