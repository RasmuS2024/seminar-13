package seminars.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import seminars.domains.satellites.EnergySystem;
import seminars.exceptions.SpaceOperationException;
import seminars.services.EnergySystemService;

import java.util.Map;

@RestController
@RequestMapping("/api/energy-systems")
@RequiredArgsConstructor
public class EnergySystemController {
    private final EnergySystemService energySystemService;

    @PostMapping
    public ResponseEntity<EnergySystem> createEnergySystem(@RequestBody Map<String, Double> request) {
        double batteryLevel = request.get("batteryLevel");
        EnergySystem energySystem = energySystemService.createEnergySystem(batteryLevel);
        return ResponseEntity.status(HttpStatus.CREATED).body(energySystem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnergySystem> getEnergySystemById(@PathVariable Long id) {
        EnergySystem energySystem = energySystemService.getEnergySystemById(id);
        return ResponseEntity.ok(energySystem);
    }

    @PostMapping("/{id}/consume")
    public ResponseEntity<Map<String, Object>> consumeEnergy(
            @PathVariable Long id,
            @RequestBody Map<String, Double> request) {
        double amount = request.get("amount");
        boolean result = energySystemService.consumeEnergy(id, amount);
        return ResponseEntity.ok(Map.of("success", result));
    }

    @GetMapping("/{id}/power-status")
    public ResponseEntity<Map<String, Boolean>> getPowerStatus(@PathVariable Long id) {
        boolean hasSufficientPower = energySystemService.hasSufficientPower(id);
        return ResponseEntity.ok(Map.of("hasSufficientPower", hasSufficientPower));
    }

    @ExceptionHandler(SpaceOperationException.class)
    public ResponseEntity<Map<String, String>> handleSpaceOperationException(SpaceOperationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }
}
