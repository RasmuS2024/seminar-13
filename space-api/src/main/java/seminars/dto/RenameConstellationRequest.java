package seminars.dto;

import jakarta.validation.constraints.NotBlank;

public record RenameConstellationRequest(@NotBlank String newName) { }
