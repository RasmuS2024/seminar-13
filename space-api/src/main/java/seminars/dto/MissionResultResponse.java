package seminars.dto;

public record MissionResultResponse(
        String constellationName,
        String satelliteName,
        boolean success,
        String message
) { }
