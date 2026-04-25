package seminars.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import seminars.domains.creation.AddSatelliteRequest;
import seminars.domains.mission.MissionRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceOperationClient {
    private final RestClient spaceOperationRestClient;

    public void addSatellite(AddSatelliteRequest request) {
        spaceOperationRestClient.post()
                .uri("/add-satellites")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
        log.info("Спутники добавлены в группировку {}", request.constellationName());
    }

    public void executeMission(MissionRequest request) {
        spaceOperationRestClient.post()
                .uri("/missions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
        log.info("Миссия выполнена: {}", request);
    }

    public String getSystemOverview() {
        return spaceOperationRestClient.get()
                .uri("/overview")
                .retrieve()
                .body(String.class);
    }

    public void decommissionSatellite(String constellationName, String satelliteName) {
        spaceOperationRestClient.delete()
                .uri("/constellations/{constellationName}/satellites/{satelliteName}", constellationName, satelliteName)
                .retrieve()
                .toBodilessEntity();
        log.info("Спутник {} выведен из эксплуатации в группировке {}", satelliteName, constellationName);
    }
}
