package seminars.dto;

public record SatelliteStatusResponse(
        Long id,
        String name,
        String type,
        boolean active,
        double batteryLevel
) { }
