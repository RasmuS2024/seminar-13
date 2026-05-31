package seminars.dto;

import java.util.List;

public record SystemOverviewResponse(
        int totalConstellations,
        int totalSatellites,
        List<ConstellationStatusResponse> constellations
) {}
