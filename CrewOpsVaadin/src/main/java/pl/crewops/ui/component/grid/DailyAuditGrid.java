package pl.crewops.ui.component.grid;

import static pl.crewops.util.LocalDateTimeFormater.DATE_TIME_HUMAN_READABLE_FORMATTER;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import java.time.ZoneId;
import java.util.*;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.dailyEntry.DailyEntryAuditDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.util.SpringContextBridge;

public class DailyAuditGrid extends Grid<DailyEntryAuditDTO> {

    private final CoreAPI coreAPI;

    private static final String COLOR_INFO = "var(--lumo-primary-color)";
    private static final String COLOR_WARNING = "var(--lumo-warning-color)";
    private static final String COLOR_ERROR = "var(--lumo-error-color)";
    private static final String COLOR_PROCESS = "#00bcd4";
    private static final String COLOR_FINANCIAL = "#ff4081";
    private static final String COLOR_SUCCESS = "var(--lumo-success-color)";
    private static final String COLOR_DEFAULT = "#9E9E9E";

    public DailyAuditGrid() {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        setSizeFull();
        configureGrid();
    }

    public void updateGrid(Set<DailyEntryAuditDTO> dailyAudits) {

        List<DailyEntryAuditDTO> sorted = dailyAudits.stream()
                .sorted(Comparator.comparing(DailyEntryAuditDTO::createdAt))
                .toList();

        setItems(sorted);
    }

    private void configureGrid() {
        // --- 1. Kolumna: Czas Zmiany (createdAt) ---
        addColumn(auditDTO -> {
                    if (auditDTO.createdAt() == null) {
                        return "-";
                    }
                    // ZM: Formatowanie daty, jak wcześniej
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

        // --- 2. Kolumna: Typ Zdarzenia (eventType - Badge) ---
        addComponentColumn(auditDTO -> {
                    DailyEntryAuditType type = auditDTO.eventType();

                    // POBIERANIE PRZETŁUMACZONEJ NAZWY Z i18n
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

        // --- 4. Kolumna: Szczegóły Zmiany (oldValues -> newValues) ---
        addColumn(auditDTO -> formatChangeDetails(auditDTO.payload()))
                .setKey("changeDetails")
                .setHeader(getTranslation("dailyAuditGrid.changeDetails"))
                .setFlexGrow(3)
                .setResizable(true);

        // --- 5. Kolumna: Komentarz (comment) ---
        addColumn(DailyEntryAuditDTO::comment)
                .setKey("comment")
                .setHeader(getTranslation("dailyAuditGrid.comment"))
                .setFlexGrow(0)
                .setResizable(true);

        setAllRowsVisible(true);
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
     * Formatuje szczegóły zmian (oldValues -> newValues) w czytelnym formacie.
     */
    private String formatChangeDetails(JsonNode payload) {
        if (payload == null) {
            return getTranslation("dailyAuditGrid.noDetails");
        }

        JsonNode oldValues = payload.has("oldValues") ? payload.get("oldValues") : null;
        JsonNode newValues = payload.has("newValues") ? payload.get("newValues") : null;

        StringBuilder details = new StringBuilder();

        if (newValues != null && newValues.isObject()) {
            // Iterujemy tylko po nowych wartościach, zakładając, że jeśli coś się zmieniło,
            // to jest to reprezentowane w newValues/oldValues.
            Iterator<Map.Entry<String, JsonNode>> fields = newValues.fields();
            boolean first = true;

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                String newValue = field.getValue().asText();
                String oldValue = oldValues != null && oldValues.has(fieldName)
                        ? oldValues.get(fieldName).asText()
                        : "-";

                // Pomijamy 'operationType', bo to widać w eventType
                if (fieldName.equals("operationType")) {
                    continue;
                }

                if (!first) {
                    details.append("; ");
                }

                // Formatuje: pole (stara wartość -> nowa wartość)
                details.append(fieldName)
                        .append(" (")
                        .append(oldValue)
                        .append(" -> ")
                        .append(newValue)
                        .append(")");

                first = false;
            }
        }

        // Jeśli nie ma newValues, a jest tylko operationType, zwracamy typ operacji.
        if (details.isEmpty() && payload.has("operationType")) {
            return payload.get("operationType").asText();
        }

        return details.toString();
    }

    // --- Metody pomocnicze do kolorowania badge (bez zmian) ---

    private String getBackgroundColorForType(DailyEntryAuditType type) {
        // ... (Kod bez zmian)
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
        // ... (Kod bez zmian)
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
