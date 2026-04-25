package seminars.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import seminars.clients.SpaceOperationClient;
import seminars.domains.mission.MissionRequest;
import seminars.properties.SpaceCenterServiceProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguredMissionScheduler {

    private final SpaceOperationClient spaceClient;
    private final SpaceCenterServiceProperties properties;
    private final TaskScheduler taskScheduler;

    @PostConstruct
    public void init() {
        for (SpaceCenterServiceProperties.ConfiguredMission config : properties.missions()) {
            MissionRequest request = new MissionRequest(
                    config.getTargetType(),
                    config.getConstellationName(),
                    config.getSatelliteName()
            );
            taskScheduler.schedule(
                    () -> {
                        try {
                            spaceClient.executeMission(request);
                            log.info("Выполнена миссия по расписанию: {}", request);
                        } catch (Exception e) {
                            log.error("Ошибка в запланированной миссии: {}", e.getMessage());
                        }
                    },
                    new CronTrigger(config.getCron())
            );
            log.info("Запланирована миссия {} с cron {}", request, config.getCron());
        }
    }
}
