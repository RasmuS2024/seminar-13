package seminars.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import seminars.domains.constellations.SatelliteConstellation;
import seminars.services.ConstellationService;

import java.util.List;

@RestController
@RequestMapping("/api/constellations")
@RequiredArgsConstructor
public class ConstellationController {
    private final ConstellationService constellationService;

    @PostMapping
    public ResponseEntity<SatelliteConstellation> createConstellation(@RequestBody String constellationName) {
        SatelliteConstellation constellation = constellationService.createConstellation(constellationName);
        return ResponseEntity.status(HttpStatus.CREATED).body(constellation);
    }

    @GetMapping
    public ResponseEntity<List<SatelliteConstellation>> getAllConstellations() {
        List<SatelliteConstellation> constellations = constellationService.getAllConstellationsWithSatellites();
        return ResponseEntity.ok(constellations);
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<SatelliteConstellation> getConstellationById(@PathVariable Long id) {
        SatelliteConstellation constellation = constellationService.getConstellationById(id);
        return ResponseEntity.ok(constellation);
    }

    @GetMapping("/{id}/status")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<String> getConstellationStatus(@PathVariable Long id) {
        SatelliteConstellation constellation = constellationService.getConstellationById(id);
        String status = constellation.getAllSatellitesStatuses();
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{name}/activate")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<Void> activateAllSatellites(@PathVariable String name) {
        constellationService.activateAllSatellites(name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{name}/mission")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<Void> executeConstellationMission(@PathVariable String name) {
        constellationService.executeConstellationMission(name);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/name/{name}")
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<Void> deleteConstellationByName(@PathVariable String name) {
        constellationService.deleteConstellationByName(name);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<Void> deleteConstellationById(@PathVariable Long id) {
        constellationService.deleteConstellation(id);
        return ResponseEntity.noContent().build();
    }
}
