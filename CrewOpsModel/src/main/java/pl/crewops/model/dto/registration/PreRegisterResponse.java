package pl.crewops.model.dto.registration;

import java.util.UUID;

public record PreRegisterResponse(UUID registrationId, PreRegisterResponseCode code) {

    public enum PreRegisterResponseCode {
        TAX_ID_ALREADY_EXIST,
        EMAIL_VERIFICATION_REQUIRED
    }
}
