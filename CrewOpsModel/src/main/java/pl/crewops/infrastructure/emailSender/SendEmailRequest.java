package pl.crewops.infrastructure.emailSender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendEmailRequest(
        @Email String toEmailAddress, @NotNull @NotBlank String subject, @NotNull @NotBlank String body) {}
