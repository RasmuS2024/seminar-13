package seminars.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seminars.domains.satellites.requests.AddSatelliteRequest;
import seminars.domains.satellites.requests.MissionRequest;
import seminars.services.SpaceOperationCenterService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SpaceOperationController {
    private final SpaceOperationCenterService spaceOperationCenterService;

    @PostMapping("/missions")
    public ResponseEntity<Void> executeMission(@RequestBody MissionRequest request) {
        spaceOperationCenterService.executeMission(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-satellites")
    public ResponseEntity<Void> addSatellite(@RequestBody AddSatelliteRequest request) {
        spaceOperationCenterService.addSatellite(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
    @GetMapping("/missions")
    public ResponseEntity<List<MissionDto>> getAllMissions() {
        List<MissionDto> missions = spaceOperationCenterService.getAllMissions();
        return ResponseEntity.ok(missions);
    }
    */
}
