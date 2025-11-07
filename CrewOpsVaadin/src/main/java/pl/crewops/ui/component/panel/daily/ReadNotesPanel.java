package pl.crewops.ui.component.panel.daily;

import static pl.crewops.util.LocalDateTimeFormater.DATE_TIME_HUMAN_READABLE_FORMATTER;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.time.ZoneId;
import java.util.List;
import pl.crewops.model.NoteFormModel;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.ui.component.form.ReadNoteForm;

public class ReadNotesPanel extends PanelCustom {

    private final Grid<NoteFormModel> grid = new Grid<>();
    private final ReadNoteForm form = new ReadNoteForm();

    private final Button close = new Button(getTranslation("readNotesPanel.close"));

    public ReadNotesPanel() {

        setSizeFull();

        configureGrid();
        form.setVisible(false);

        var mainContainer = new HorizontalLayout();
        mainContainer.setSpacing(false);
        mainContainer.setPadding(false);

        mainContainer.add(grid, form);
        mainContainer.setFlexGrow(2, grid);
        mainContainer.setFlexGrow(1, form);

        close.setWidthFull();
        close.addClickListener(event -> {
            this.setVisible(false);
        });

        addContent(mainContainer, close);
    }

    public void updateGrid(List<NoteFormModel> items) {
        grid.setItems(items);
    }

    public void configurePanel(IconFactory iconFactory, String summaryText) {
        setSummary(iconFactory, summaryText);
    }

    private void configureGrid() {
        grid.setAllRowsVisible(true);

        // todo i18n
        grid.addColumn(NoteFormModel::getContent)
                .setKey("content")
                .setHeader(getTranslation("readNotesPanel.content"))
                .setFlexGrow(1);

        grid.addColumn(note -> {
                    if (note.getCreatedAt() == null) {
                        return "";
                    }
                    return note.getCreatedAt()
                            .atZone(ZoneId.systemDefault())
                            // 2. Formatowanie
                            .format(DATE_TIME_HUMAN_READABLE_FORMATTER);
                })
                .setKey("createdAt")
                .setHeader(getTranslation("readNotesPanel.createdAt"))
                .setSortable(true)
                .setFlexGrow(1);

        grid.addComponentColumn(note -> {
                    if (note.isPrivate()) {
                        Span privateLabel = new Span(getTranslation("readNotesPanel.private")); // todo i18n

                        privateLabel.getElement().getThemeList().add("badge small contrast");
                        privateLabel.getStyle().set("font-size", "0.8em");
                        privateLabel.getStyle().set("padding", "0.2em 0.4em");
                        privateLabel.getStyle().set("border-radius", "8px");
                        privateLabel.getStyle().set("border", "1px solid #00adb5"); // Używamy akcentu
                        privateLabel.getStyle().set("background-color", "transparent"); // Tło może być przezroczyste
                        privateLabel.getStyle().set("color", "#00adb5"); // Kolor tekstu jako akcent
                        privateLabel.getStyle().set("font-weight", "600");
                        privateLabel.getStyle().set("white-space", "nowrap");

                        return privateLabel;
                    }

                    return new Span();
                })
                .setKey("private")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.asSingleSelect().addValueChangeListener(event -> {
            form.setNoteFormModel(event.getValue());
            form.setVisible(true);
        });
    }
}
