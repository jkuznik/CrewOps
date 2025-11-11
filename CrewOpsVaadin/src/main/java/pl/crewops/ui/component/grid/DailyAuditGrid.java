package pl.crewops.ui.component.grid;

import static pl.crewops.util.LocalDateTimeFormater.DATE_TIME_HUMAN_READABLE_FORMATTER;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.dailyEntry.DailyEntryAuditDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.util.DailyEntryAuditReconstructor;
import pl.crewops.util.SpringContextBridge;

public class DailyAuditGrid extends Grid<DailyEntryAuditDTO> {

    private final CoreAPI coreAPI;
    private final DailyEntryAuditReconstructor reconstructor;
    private List<DailyEntryAuditDTO> currentAudits = Collections.emptyList();

    // NOWE POLE: \u015aledzenie ostatnio otwartego wiersza
    private DailyEntryAuditDTO lastOpenedItem = null;

    // Format dla daty i czasu w szczeg\u00f3\u0142ach
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    private static final String COLOR_INFO = "var(--lumo-primary-color)";
    private static final String COLOR_WARNING = "var(--lumo-warning-color)";
    private static final String COLOR_ERROR = "var(--lumo-error-color)";
    private static final String COLOR_PROCESS = "#00bcd4";
    private static final String COLOR_FINANCIAL = "#ff4081";
    private static final String COLOR_SUCCESS = "var(--lumo-success-color)";
    private static final String COLOR_DEFAULT = "#9E9E9E";

    public DailyAuditGrid() {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.reconstructor = new DailyEntryAuditReconstructor();

        setSizeFull();
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        configureGrid();
    }

    public void openLastEventDetails() {
        setDetailsVisible(currentAudits.getLast(), true);
        // 🔧 Workaround: delay refresh to avoid empty space after first details open
        getElement().executeJs("setTimeout(() => { this.requestContentUpdate && this.requestContentUpdate(); }, 50);");
    }

    public void updateGrid(Set<DailyEntryAuditDTO> dailyAudits) {
        currentAudits = dailyAudits.stream()
                .sorted(Comparator.comparing(DailyEntryAuditDTO::createdAt))
                .collect(Collectors.toList());

        setItems(currentAudits);
    }

    private void configureGrid() {
        // --- Definicje Kolumn (bez zmian) ---
        addColumn(auditDTO -> {
                    if (auditDTO.createdAt() == null) {
                        return "-";
                    }
                    return auditDTO.createdAt()
                            .atZone(ZoneId.systemDefault())
                            .format(DATE_TIME_HUMAN_READABLE_FORMATTER);
                })
                .setKey("createdAt")
                .setHeader(getTranslation("dailyAuditGrid.createdAt"))
                .setSortable(true)
                .setWidth("170px")
                .setFlexGrow(1)
                .setResizable(true);

        addColumn(auditDTO -> getPerformedByFromPayload(auditDTO.payload()))
                .setKey("performedBy")
                .setHeader(getTranslation("dailyAuditGrid.performedBy"))
                .setWidth("150px")
                .setFlexGrow(1)
                .setResizable(true);

        addComponentColumn(auditDTO -> {
                    DailyEntryAuditType type = auditDTO.eventType();
                    String typeName = Objects.nonNull(type)
                            ? getTranslation("dailyAudit.eventType." + type.name())
                            : getTranslation("dailyAuditGrid.eventType.UNKNOWN");

                    Span typeLabel = new Span(typeName);
                    String backgroundColor = getBackgroundColorForType(type);

                    typeLabel.getElement().getThemeList().add("badge small contrast");
                    typeLabel.getStyle().set("font-size", "0.8em");
                    typeLabel.getStyle().set("padding", "0.2em 0.4em");
                    typeLabel.getStyle().set("border-radius", "8px");
                    typeLabel.getStyle().set("border", "1px solid " + getBorderColorForType(type));
                    typeLabel.getStyle().set("border-style", "solid");
                    typeLabel.getStyle().set("background-color", backgroundColor);

                    if (COLOR_WARNING.equals(backgroundColor) || COLOR_DEFAULT.equals(backgroundColor)) {
                        typeLabel.getStyle().set("color", "#1f2937");
                    } else {
                        typeLabel.getStyle().set("color", "white");
                    }

                    typeLabel.getStyle().set("white-space", "nowrap");
                    typeLabel.getStyle().set("overflow", "hidden");
                    typeLabel.getStyle().set("text-overflow", "ellipsis");
                    typeLabel.getStyle().set("max-width", "200px");
                    return typeLabel;
                })
                .setKey("eventType")
                .setHeader(getTranslation("dailyAuditGrid.eventType"))
                .setFlexGrow(1)
                .setResizable(true);

        addColumn(auditDTO -> formatChangeDetails(auditDTO.payload()))
                .setKey("changeDetails")
                .setHeader(getTranslation("dailyAuditGrid.changeDetails"))
                .setFlexGrow(3)
                .setResizable(true);

        addColumn(DailyEntryAuditDTO::comment)
                .setKey("comment")
                .setHeader(getTranslation("dailyAuditGrid.comment"))
                .setFlexGrow(0)
                .setResizable(true);

        // Ustawienie renderera szczeg\u00f3\u0142\u00f3w
        setItemDetailsRenderer(new ComponentRenderer<>(this::createDetailsComponent));

        // ZMIENIONA LOGIKA KLIKNI\u0118CIA
        addItemClickListener(event -> {
            DailyEntryAuditDTO clickedItem = event.getItem();

            if (clickedItem == null) return;

            // 1. Sprawdzenie, czy klikni\u0119to w ten sam wiersz
            if (clickedItem.equals(lastOpenedItem)) {
                // Je\u015Bli klikni\u0119to w ten sam wiersz, zamykamy go
                setDetailsVisible(clickedItem, false);
                lastOpenedItem = null;
            } else {
                // 2. Je\u015Bli poprzedni wiersz by\u0142 otwarty, zamykamy go
                if (lastOpenedItem != null) {
                    setDetailsVisible(lastOpenedItem, false);
                }

                // 3. Otwieramy nowy wiersz
                setDetailsVisible(clickedItem, true);

                // 4. Ustawiamy nowy wiersz jako ostatnio otwarty
                lastOpenedItem = clickedItem;
            }
        });

        setAllRowsVisible(true);
    }

    /**
     * Tworzy komponent z szczeg\u00f3\u0142ami zrekonstruowanego stanu DailyEntry.
     * Logika ta zosta\u0142a zachowana bez zmian.
     */
    private Component createDetailsComponent(DailyEntryAuditDTO auditDTO) {
        // 1. Rekonstrukcja stanu na moment tego audytu
        DailyEntryDTO state = reconstructor.reconstructState(auditDTO, currentAudits);

        // 2. Formatowanie danych
        String startTime = state.startTime() != null ? DATE_TIME_FORMATTER.format(state.startTime()) : "-";
        String endTime = state.endTime() != null ? DATE_TIME_FORMATTER.format(state.endTime()) : "-";
        String overTime = state.overTime() != null ? state.overTime().toPlainString() : "0";
        String attendance = state.attendance() != null ? state.attendance().toString() : "-";
        String status = state.status() != null ? state.status().toString() : "-";

        // 3. Budowa layoutu
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle().set("border-left", "4px solid var(--lumo-primary-color)");
        layout.getStyle().set("border-radius", "8px");
        layout.getStyle().set("margin", "0 0 0 20px");
        layout.getStyle().set("padding", "0.5em 1em");

        // Zrekonstruowany Stan Wpisu
        Span titleSpan = new Span(getTranslation("dailyAuditGrid.details.reconstructedStateTitle"));
        titleSpan.getStyle().set("font-weight", "bold");
        titleSpan.getStyle().set("font-size", "1.1em");

        // Dodawanie sformatowanych danych do layoutu
        layout.add(
                titleSpan,
                new Span(getTranslation("dailyAuditGrid.details.startTime") + ": " + startTime),
                new Span(getTranslation("dailyAuditGrid.details.endTime") + ": " + endTime),
                new Span(getTranslation("dailyAuditGrid.details.overtime") + ": " + overTime),
                new Span(getTranslation("dailyAuditGrid.details.attendanceStatus") + ": " + attendance),
                new Span(getTranslation("dailyAuditGrid.details.entryStatus") + ": " + status));

        return layout;
    }

    /**
     * Parsuje UUID wykonawcy z pola 'payload'.
     */
    private String getPerformedByFromPayload(JsonNode payload) {
        if (payload != null
                && payload.has("metadata")
                && payload.get("metadata").has("performedBy")) {
            try {
                var id = UUID.fromString(
                        payload.get("metadata").get("performedBy").asText());
                Optional<EmployeeDTO> employeeById = coreAPI.getEmployeeById(id);
                return employeeById
                        .map(employeeDTO -> employeeDTO.firstName() + " " + employeeDTO.lastName())
                        .orElseGet(() -> getTranslation("dailyAuditGrid.system"));
            } catch (Exception e) {
                return getTranslation("dailyAuditGrid.system");
            }
        }
        return getTranslation("dailyAuditGrid.system");
    }

    /**
     * Formatuje szczeg\u00f3\u0142y zmian (oldValues -> newValues) w czytelnym formacie.
     */
    private String formatChangeDetails(JsonNode payload) {
        if (payload == null) {
            return getTranslation("dailyAuditGrid.noDetails");
        }

        JsonNode oldValues = payload.has("oldValues") ? payload.get("oldValues") : null;
        JsonNode newValues = payload.has("newValues") ? payload.get("newValues") : null;

        StringBuilder details = new StringBuilder();

        if (newValues != null && newValues.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = newValues.fields();
            boolean first = true;

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                String newValue = field.getValue().asText();
                String oldValue = oldValues != null && oldValues.has(fieldName)
                        ? oldValues.get(fieldName).asText()
                        : "-";

                if (fieldName.equals("operationType")) {
                    continue;
                }

                if (!first) {
                    details.append("; ");
                }

                details.append(fieldName)
                        .append(" (")
                        .append(oldValue)
                        .append(" -> ")
                        .append(newValue)
                        .append(")");

                first = false;
            }
        }

        if (details.isEmpty() && payload.has("operationType")) {
            return payload.get("operationType").asText();
        }

        return details.toString();
    }

    private String getBackgroundColorForType(DailyEntryAuditType type) {
        if (type == null) {
            return COLOR_DEFAULT;
        }
        return switch (type) {
            case INFORMATION_MODIFIED -> COLOR_INFO;
            case ATTENDANCE_STATUS_CHANGED -> COLOR_SUCCESS;
            case SAFETY_NOTE_ADDED -> COLOR_ERROR;
            case REPORT_STATUS_CHANGED -> COLOR_PROCESS;
            case OVERTIME_MODIFIED -> COLOR_FINANCIAL;
            case ENTRY_STATUS_CHANGED -> COLOR_WARNING;
        };
    }

    private String getBorderColorForType(DailyEntryAuditType type) {
        if (type == null) {
            return "#616161";
        }
        return switch (type) {
            case INFORMATION_MODIFIED -> "var(--lumo-primary-text-color)";
            case ATTENDANCE_STATUS_CHANGED -> "var(--lumo-success-text-color)";
            case SAFETY_NOTE_ADDED -> "var(--lumo-error-text-color)";
            case REPORT_STATUS_CHANGED -> "#006064";
            case OVERTIME_MODIFIED -> "#880e4f";
            case ENTRY_STATUS_CHANGED -> "#e65100";
        };
    }
}
