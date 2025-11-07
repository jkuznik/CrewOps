package pl.crewops.ui.component.dialog.credentialsDialog;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
class UpdateCredentialsData {
    private String username;
    private String repeatUsername;
    private String password;
    private String repeatPassword;
    private String currentPassword;
}
