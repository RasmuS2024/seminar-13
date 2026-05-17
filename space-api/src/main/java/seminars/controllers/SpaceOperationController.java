package seminars.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seminars.domains.satellites.requests.AddSatelliteRequest;
import seminars.domains.satellites.requests.MissionRequest;
import seminars.services.SpaceOperationCenterService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SpaceOperationController {
    private final SpaceOperationCenterService spaceOperationCenterService;

    /**
     * Выполнить миссию для сптуника отдельно или для группировки
     */
    @PostMapping("/missions")
    public ResponseEntity<Void> executeMission(@Valid @RequestBody MissionRequest request) {
        spaceOperationCenterService.executeMission(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Добавление нового спутника(ов)
     */
    @PostMapping("/add-satellites")
    public ResponseEntity<Void> addSatellite(@Valid @RequestBody AddSatelliteRequest request) {
        spaceOperationCenterService.addSatellite(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Вывод сводной информации о группировках и сптуниках
     */
    @GetMapping("/overview")
    public ResponseEntity<String> getSystemOverview() {
        String overview = spaceOperationCenterService.getSystemOverview();
        return ResponseEntity.ok(overview);
    }

    /**
    * Вывод спутника из эксплуатации (деактивация и удаление из группировки)
    */
    @DeleteMapping("/constellations/{constellationName}/satellites/{satelliteName}")
    public ResponseEntity<Void> decommissionSatellite(
            @PathVariable String constellationName,
            @PathVariable String satelliteName) {
        spaceOperationCenterService.decommissionSatellite(constellationName, satelliteName);
        return ResponseEntity.noContent().build();
    }

}
