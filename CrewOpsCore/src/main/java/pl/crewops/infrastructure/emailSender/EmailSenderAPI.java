package pl.crewops.infrastructure.emailSender;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface EmailSenderAPI {

    void sendEmail(@NotNull @Valid SendEmailRequest sendEmailRequest);
}
