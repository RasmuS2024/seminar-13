package seminars.domains.satellites.requests;

import jakarta.validation.constraints.NotNull;

public record EnergySystemUpdateRequest(@NotNull Double batteryLevel) {}
