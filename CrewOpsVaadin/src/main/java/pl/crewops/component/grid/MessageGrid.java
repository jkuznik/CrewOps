package pl.crewops.component.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.component.form.MessageForm;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.MessageFormModel;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;

@Slf4j
@Getter
@Setter
public class MessageGrid extends VerticalLayout {
    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final Grid<MessageFormModel> grid = new Grid<>();
    private final MessageForm messageForm = new MessageForm();
    // todo: i18n
    private final Button sendButton = new Button("send");

    public MessageGrid(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.authenticationResolver = authenticationResolver;

        localize();

        configureGrid();
        configureForm();

        updateGrid();

        add(getToolbar(), getContent());
    }

    private void localize() {
        sendButton.setText(getTranslation("messageGrid.sendButton"));
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(message -> {
                    var senderEmployeeId = message.getSenderEmployeeId();
                    Optional<EmployeeDTO> employeeById = null;
                    try {
                        employeeById = coreAPI.getEmployeeById(senderEmployeeId);
                    } catch (NotAuthenticatedException e) {
                        new FailNotification(e.getMessage());
                    }
                    if (employeeById.isPresent()) {
                        return employeeById.get().firstName() + " "
                                + employeeById.get().lastName();
                    } else {
                        return null;
                    }
                })
                .setKey("sender");
        grid.addColumn(MessageFormModel::getTitle).setKey("title");
        grid.addColumn(MessageFormModel::getCreatedAt).setKey("sendTime");
        grid.addColumn(MessageFormModel::getDescription).setKey("description");
        grid.addColumn(MessageFormModel::isRead).setKey("read");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> {});
    }

    private void configureForm() {
        if (BrowserResolver.isMobile()) {
            messageForm.setWidthFull();
        } else {
            messageForm.setWidth("25em");
        }

        messageForm.setVisible(false);
        messageForm.addSendListener(event -> {
            new Notification("send message clicek").open();
        });

        messageForm.addCloseListener(event -> messageForm.setVisible(false));
    }

    private Component getContent() {
        var content = new HorizontalLayout(grid, messageForm);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, grid);
        return content;
    }

    private void updateGrid() {
        // todo: implement logic to put unread messages on the top of the list and after
        //  read move to read collection but sorted by createdAt
        try {
            var employeeId = authenticationResolver.getPrincipal().getEmployeeId();
            // todo: implement loop to fetch messages partially
            var messagesByRecipientEmployeeId = coreAPI.getMessagesByRecipientEmployeeId(employeeId).stream()
                    .map(MessageFormModel::toMessageFormModel)
                    .toList();

            grid.setItems(messagesByRecipientEmployeeId);
        } catch (NotAuthenticatedException e) {
            log.error("Fail to get messages by recipient employee id", e);
            new FailNotification(e.getMessage());
        }
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        sendButton.addClickListener(event -> {
            messageForm.setVisible(true);
        });

        toolbar.add(sendButton);
        return toolbar;
    }
}
