package pl.crewops.component.dialog.credentialsDialog;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import java.util.Optional;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.component.notification.SuccessNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.ProfileFormModel;
import pl.crewops.model.dto.auth.AuthUserDTO;
import pl.crewops.model.dto.auth.UpdateAuthUserDTO;
import pl.crewops.view.HomeView;
import pl.crewops.view.ProfileView;

public class UpdateUsernameDialog extends Dialog {

    public UpdateUsernameDialog(ProfileFormModel profileFormModel, CoreAPI coreAPI) {
        addClassName("profileDialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        var updateCredentialsForm = new UpdateUsernameForm(profileFormModel);

        updateCredentialsForm.addUpdateListener(event -> {
            UpdateCredentialsData updateCredentialsData = event.getUpdateCredentialsData();
            var updateAuthUserDTO = UpdateAuthUserDTO.builder()
                    .employeeId(profileFormModel.getEmployeeId())
                    .username(updateCredentialsData.getUsername())
                    .currentPassword(updateCredentialsData.getCurrentPassword())
                    .build();
            try {
                // todo implement security action against brute force username update (enumeration usernames)

                Optional<AuthUserDTO> authUserDTO = coreAPI.updateAuthUserCredentials(updateAuthUserDTO);
                if (authUserDTO.isPresent()) {
                    new SuccessNotification(getTranslation("updateCredentialsDialog.successNotification"));
                    close();
                    UI.getCurrent().navigate(ProfileView.class);
                } else {
                    new FailNotification(getTranslation("updateCredentialsDialog.failNotification"));
                }
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
                UI.getCurrent().navigate(HomeView.class);
            }
            close();
        });
        updateCredentialsForm.addCloseListener(e -> close());

        add(updateCredentialsForm);
    }
}
