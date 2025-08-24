package pl.crewops.component.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
    public static final String SYSTEM_SENDER = "SYSTEM";

    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final Grid<MessageFormModel> grid = new Grid<>();
    private final MessageForm messageForm = new MessageForm();
    // todo: i18n
    private final Button sendButton = new Button();

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
        List<EmployeeDTO> allEmployees = new ArrayList<>();
        try {
            allEmployees = coreAPI.getAllEmployees();
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
        }

        List<EmployeeDTO> finalAllEmployees = allEmployees;

        grid.addComponentColumn(message -> {
                    String senderName = resolveSenderName(message, finalAllEmployees);

                    Span senderLabel = new Span(senderName);
                    senderLabel.getElement().getThemeList().add("badge small contrast");
                    senderLabel.getStyle().set("font-size", "0.8em");
                    senderLabel.getStyle().set("padding", "0.2em 0.4em");
                    senderLabel.getStyle().set("border-radius", "8px");
                    senderLabel.getStyle().set("border", "1px solid #B0B0B0");
                    senderLabel.getStyle().set("border-style", "solid");

                    if (SYSTEM_SENDER.equals(senderName)) {
                        senderLabel.getStyle().set("background-color", "#00adb5");
                        senderLabel.getStyle().set("color", "#ffffff");
                    }

                    senderLabel.getStyle().set("white-space", "nowrap");
                    senderLabel.getStyle().set("overflow", "hidden");
                    senderLabel.getStyle().set("text-overflow", "ellipsis");
                    senderLabel.getStyle().set("max-width", "200px");

                    return senderLabel;
                })
                .setKey("sender")
                .setHeader(getTranslation("messageGrid.sender"))
                .setAutoWidth(true);

        grid.addColumn(MessageFormModel::getTitle)
                .setKey("title")
                .setHeader(getTranslation("messageGrid.title"))
                .setFlexGrow(1);

        grid.addColumn(MessageFormModel::getCreatedAt)
                .setKey("sendTime")
                .setHeader(getTranslation("messageGrid.sendTime"))
                .setAutoWidth(true);

        grid.addColumn(MessageFormModel::getDescription)
                .setKey("description")
                .setHeader(getTranslation("messageGrid.description"))
                .setWidth("300px")
                .setFlexGrow(2);

        grid.addComponentColumn(message -> {
                    if (!message.isRead()) {
                        Icon icon = VaadinIcon.ENVELOPE.create();
                        icon.setColor("blue");
                        return icon;
                    }
                    return null;
                })
                .setKey("read")
                //                .setHeader(getTranslation("messageGrid.status"))
                .setFlexGrow(0);

        grid.asSingleSelect().addValueChangeListener(event -> {
            var selectedMessage = event.getValue();
            if (selectedMessage != null) {
                String senderName = resolveSenderName(selectedMessage, finalAllEmployees);
                if (!event.getValue().isRead()) {
                    try {
                        coreAPI.setMessageReadStatus(event.getValue().getId(), true);
                    } catch (NotAuthenticatedException e) {
                        new FailNotification(e.getMessage());
                    }
                    updateGrid();
                }

                messageForm.setReadMessageMode();
                messageForm.setBinderValue(selectedMessage, senderName);
                messageForm.setVisible(true);
            }
        });
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        sendButton.addClickListener(event -> {
            messageForm.setSendMessageMode();
            messageForm.clearBinderValue();
            messageForm.setVisible(true);
        });

        toolbar.add(sendButton);
        return toolbar;
    }

    private String resolveSenderName(MessageFormModel message, List<EmployeeDTO> employees) {
        if (message.getSenderEmployeeId() != null) {
            return employees.stream()
                    .filter(employee -> employee.id().equals(message.getSenderEmployeeId()))
                    .findFirst()
                    .map(employee -> employee.firstName() + " " + employee.lastName())
                    .orElse(SYSTEM_SENDER);
        }
        return SYSTEM_SENDER;
    }

    private void configureForm() {
        if (BrowserResolver.isMobile()) {
            messageForm.setWidthFull();
        } else {
            messageForm.setWidth("25em");
        }

        messageForm.setVisible(false);
        messageForm.addSendListener(event -> {
            updateGrid();
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
                    .sorted(Comparator.comparing(MessageFormModel::isRead)
                            .thenComparing(MessageFormModel::getCreatedAt, Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            grid.setItems(messagesByRecipientEmployeeId);
        } catch (NotAuthenticatedException e) {
            log.error("Fail to get messages by recipient employee id", e);
            new FailNotification(e.getMessage());
        }
    }
}
