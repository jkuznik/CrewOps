package pl.crewops.registration;

import lombok.Builder;

@Builder
public record PreRegisterResponse(boolean inProgress, String body) {
    public static final String TAX_ID_ALREADY_EXIST = "TaxIdAlreadyExist";
}
