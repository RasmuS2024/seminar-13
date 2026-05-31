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
import seminars.dto.AddSatellitesResponse;
import seminars.dto.ConstellationStatusResponse;
import seminars.dto.MissionResultResponse;
import seminars.dto.SatelliteStatusResponse;
import seminars.dto.SystemOverviewResponse;
import seminars.services.SpaceOperationCenterService;
import seminars.services.SatelliteService;
import seminars.services.ConstellationService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SpaceOperationController {
    private final SpaceOperationCenterService spaceOperationCenterService;
    private final SatelliteService satelliteService;
    private final ConstellationService constellationService;

    @PostMapping("/missions")
    public ResponseEntity<MissionResultResponse> executeMission(@Valid @RequestBody MissionRequest request) {
        MissionResultResponse result = spaceOperationCenterService.executeMission(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add-satellites")
    public ResponseEntity<AddSatellitesResponse> addSatellite(@Valid @RequestBody AddSatelliteRequest request) {
        AddSatellitesResponse result = spaceOperationCenterService.addSatellite(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/overview")
    public ResponseEntity<SystemOverviewResponse> getSystemOverview() {
        SystemOverviewResponse overview = spaceOperationCenterService.getSystemOverview();
        return ResponseEntity.ok(overview);
    }

    @DeleteMapping("/constellations/{constellationName}/satellites/{satelliteName}")
    public ResponseEntity<Void> decommissionSatellite(
            @PathVariable String constellationName,
            @PathVariable String satelliteName) {
        spaceOperationCenterService.decommissionSatellite(constellationName, satelliteName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/satellites/{id}/activate")
    public ResponseEntity<SatelliteStatusResponse> activateSatellite(@PathVariable Long id) {
        SatelliteStatusResponse status = satelliteService.activateSatellite(id);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/satellites/{id}/deactivate")
    public ResponseEntity<SatelliteStatusResponse> deactivateSatellite(@PathVariable Long id) {
        SatelliteStatusResponse status = satelliteService.deActivateSatellite(id);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/satellites/{id}/mission")
    public ResponseEntity<SatelliteStatusResponse> performSatelliteMission(@PathVariable Long id) {
        SatelliteStatusResponse status = satelliteService.performSatelliteMission(id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/satellites/{id}/status")
    public ResponseEntity<SatelliteStatusResponse> getSatelliteStatus(@PathVariable Long id) {
        SatelliteStatusResponse status = satelliteService.getSatelliteStatus(id);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/constellations/{name}/activate")
    public ResponseEntity<ConstellationStatusResponse> activateConstellation(@PathVariable String name) {
        ConstellationStatusResponse status = constellationService.activateAllSatellites(name);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/constellations/{name}/mission")
    public ResponseEntity<ConstellationStatusResponse> executeConstellationMission(@PathVariable String name) {
        ConstellationStatusResponse status = constellationService.executeConstellationMission(name);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/constellations/{name}/status")
    public ResponseEntity<ConstellationStatusResponse> getConstellationStatus(@PathVariable String name) {
        ConstellationStatusResponse status = constellationService.getConstellationStatus(name);
        return ResponseEntity.ok(status);
    }
}
