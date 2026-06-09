package seminars.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seminars.domains.constellations.SatelliteConstellation;
import seminars.dto.ConstellationStatusResponse;
import seminars.dto.CreateConstellationRequest;
import seminars.dto.RenameConstellationRequest;
import seminars.services.ConstellationService;

import java.util.List;

@RestController
@RequestMapping("/api/constellations")
@RequiredArgsConstructor
public class ConstellationController {
    private final ConstellationService constellationService;

    @PostMapping
    public ResponseEntity<SatelliteConstellation> createConstellation(@Valid @RequestBody CreateConstellationRequest request) {
        SatelliteConstellation constellation = constellationService.createConstellation(request.name());
        return ResponseEntity.ok(constellation);
    }

    @GetMapping
    public ResponseEntity<List<SatelliteConstellation>> getAllConstellations() {
        List<SatelliteConstellation> constellations = constellationService.getAllConstellationsWithSatellites();
        return ResponseEntity.ok(constellations);
    }

    @GetMapping("/{name}")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<SatelliteConstellation> getConstellationByName(@PathVariable String name) {
        SatelliteConstellation constellation = constellationService.getConstellationByName(name);
        return ResponseEntity.ok(constellation);
    }

    @DeleteMapping("/{name}")
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<Void> deleteConstellation(@PathVariable String name) {
        constellationService.deleteConstellation(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", description = "Неверное имя или группировка уже существует")
    @ApiResponse(responseCode = "404", description = "Группировка не найдена")
    public ResponseEntity<SatelliteConstellation> renameConstellation(
            @PathVariable String name,
            @Valid @RequestBody RenameConstellationRequest request) {
        constellationService.renameConstellation(name, request.newName());
        SatelliteConstellation constellation = constellationService.getConstellationByName(request.newName());
        return ResponseEntity.ok(constellation);
    }

    @PatchMapping("/{constellationName}/satellites/{satelliteName}")
    @ApiResponse(responseCode = "200", description = "Спутник привязан к группировке")
    @ApiResponse(responseCode = "404", description = "Группировка или спутник не найдены")
    public ResponseEntity<ConstellationStatusResponse> addSatelliteToConstellation(
            @PathVariable String constellationName,
            @PathVariable String satelliteName) {
        constellationService.addSatelliteToConstellation(constellationName, satelliteName);
        ConstellationStatusResponse status = constellationService.getConstellationStatus(constellationName);
        return ResponseEntity.ok(status);
    }

}
