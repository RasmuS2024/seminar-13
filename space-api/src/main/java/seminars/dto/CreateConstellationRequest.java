package seminars.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateConstellationRequest(@NotBlank String name) {}
