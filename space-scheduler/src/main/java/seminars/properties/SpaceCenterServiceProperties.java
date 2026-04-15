package seminars.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import seminars.domains.mission.MissionTargetType;

import java.util.List;

@ConfigurationProperties("app.space-center-service")
public record SpaceCenterServiceProperties(String url, List<ConfiguredMission> missions) {
    @Data
    public static class ConfiguredMission {
        private MissionTargetType targetType;
        private String constellationName;
        private String satelliteName;
        private String cron;
    }
}
