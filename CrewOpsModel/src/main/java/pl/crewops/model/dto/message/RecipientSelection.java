package pl.crewops.model.dto.message;

import jakarta.validation.constraints.NotNull;

public record RecipientSelection(RecipientOptionType type, @NotNull String value) {

    public enum RecipientOptionType {
        ALL,
        DEPARTMENT,
        MACHINE,
        EMPLOYEE
    }
}
