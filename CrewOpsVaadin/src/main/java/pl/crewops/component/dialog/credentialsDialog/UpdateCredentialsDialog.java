package pl.crewops.component.dialog.credentialsDialog;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.ProfileFormModel;
import pl.crewops.model.dto.auth.UpdateAuthUserDTO;
import pl.crewops.view.HomeView;

public class UpdateCredentialsDialog extends Dialog {

    public UpdateCredentialsDialog(ProfileFormModel profileFormModel, CoreAPI coreAPI) {
        addClassName("profileDialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        var updateCredentialsForm = new UpdateCredentialsForm(profileFormModel);

        updateCredentialsForm.addUpdateListener(event -> {
            UpdateCredentialsForm.UpdateCredentialsData updateCredentialsData = event.getUpdateCredentialsData();
            var updateAuthUserDTO = UpdateAuthUserDTO.builder()
                    .employeeId(profileFormModel.getEmployeeId())
                    .username(updateCredentialsData.getUsername())
                    .password(updateCredentialsData.getPassword())
                    .build();
            try {
                // todo implement additional authentication using currentpassword field, implement notification and
                // close dialog
                coreAPI.updateAuthUserCredentials(updateAuthUserDTO);
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
