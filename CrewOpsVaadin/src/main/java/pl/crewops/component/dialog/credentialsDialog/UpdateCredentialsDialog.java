package pl.crewops.component.dialog.credentialsDialog;

import com.vaadin.flow.component.dialog.Dialog;
import pl.crewops.model.ProfileFormModel;

public class UpdateCredentialsDialog extends Dialog {

    public UpdateCredentialsDialog(ProfileFormModel profileFormModel) {
        addClassName("profileDialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        var updateCredentialsForm = new UpdateCredentialsForm(profileFormModel);

        // todo implement update acion
        updateCredentialsForm.addUpdateListener(event -> {
            UpdateCredentialsForm.UpdateCredentialsData updateCredentialsData = event.getUpdateCredentialsData();
            close();
        });
        updateCredentialsForm.addCloseListener(e -> close());

        add(updateCredentialsForm);
    }
}
