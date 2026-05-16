package seminars.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seminars.domains.satellites.EnergySystem;
import seminars.domains.satellites.requests.EnergySystemUpdateRequest;
import seminars.services.EnergySystemService;
import java.util.List;

@RestController
@RequestMapping("/api/energy-systems")
@RequiredArgsConstructor
public class EnergySystemController {
    private final EnergySystemService energySystemService;

    @GetMapping
    public ResponseEntity<List<EnergySystem>> getAllEnergySystems() {
        List<EnergySystem> systems = energySystemService.getAllEnergySystems();
        return ResponseEntity.ok(systems);
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Энергосистема не найдена")
    public ResponseEntity<EnergySystem> getEnergySystemById(@PathVariable Long id) {
        EnergySystem energySystem = energySystemService.getEnergySystemById(id);
        return ResponseEntity.ok(energySystem);
    }

    @PatchMapping("/{id}")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Энергосистема не найдена")
    public ResponseEntity<EnergySystem> updateEnergySystem(
            @PathVariable Long id,
            @Valid @RequestBody EnergySystemUpdateRequest request) {
        EnergySystem updated = energySystemService.updateEnergySystem(id, request.batteryLevel());
        return ResponseEntity.ok(updated);
    }

}
