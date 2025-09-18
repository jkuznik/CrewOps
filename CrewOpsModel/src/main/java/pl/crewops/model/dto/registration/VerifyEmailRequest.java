package pl.crewops.model.dto.registration;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VerifyEmailRequest(
        @NotNull UUID registrationId,
        @NotNull String verificationCode,
        @NotNull String subject,
        @NotNull String body) {}
