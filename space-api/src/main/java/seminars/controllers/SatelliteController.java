package seminars.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;
import seminars.services.SatelliteService;

import java.util.List;

@RestController
@RequestMapping("/api/satellites")
@RequiredArgsConstructor
public class SatelliteController {
    private final SatelliteService satelliteService;

    @PostMapping
    public ResponseEntity<Satellite> createSatellite(@Valid @RequestBody SatelliteParam param) {
        Satellite satellite = satelliteService.createSatellite(param);
        return ResponseEntity.status(HttpStatus.CREATED).body(satellite);
    }

    @GetMapping
    public ResponseEntity<List<Satellite>> getAllSatellites() {
        List<Satellite> satellites = satelliteService.getAllSatellites();
        return ResponseEntity.ok(satellites);
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Спутник не найден")
    public ResponseEntity<Satellite> getSatelliteById(@PathVariable Long id) {
        Satellite satellite = satelliteService.getSatelliteById(id);
        return ResponseEntity.ok(satellite);
    }

    @GetMapping("/name/{name}")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Спутник не найден")
    public ResponseEntity<Satellite> getSatelliteByName(@PathVariable String name) {
        Satellite satellite = satelliteService.getSatelliteByName(name);
        return ResponseEntity.ok(satellite);
    }

    @GetMapping("/constellation/{constellationId}")
    public ResponseEntity<List<Satellite>> getSatellitesByConstellationId(@PathVariable Long constellationId) {
        List<Satellite> satellites = satelliteService.getSatellitesByConstellationId(constellationId);
        return ResponseEntity.ok(satellites);
    }

    @PostMapping("/{id}/activate")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Спутник не найден")
    public ResponseEntity<Void> activateSatellite(@PathVariable Long id) {
        satelliteService.activateSatellite(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Спутник не найден")
    public ResponseEntity<Void> deactivateSatellite(@PathVariable Long id) {
        satelliteService.deActivateSatellite(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/mission")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Спутник не найден")
    public ResponseEntity<Void> performMission(@PathVariable Long id) {
        satelliteService.performSatelliteMission(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/status")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Спутник не найден")
    public ResponseEntity<String> getSatelliteStatus(@PathVariable Long id) {
        String status = satelliteService.getSatelliteStatus(id);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "404", description = "Спутник не найден")
    public ResponseEntity<Void> deleteSatellite(@PathVariable Long id) {
        satelliteService.deleteSatellite(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Спутник не найден")
    public ResponseEntity<Satellite> updateSatellite(
            @PathVariable Long id,
            @Valid @RequestBody SatelliteParam param) {
        Satellite satellite = satelliteService.updateSatellite(id, param);
        return ResponseEntity.ok(satellite);
    }

}
