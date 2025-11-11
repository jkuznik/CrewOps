package pl.crewops.ui.component.grid;

import static pl.crewops.util.LocalDateTimeFormater.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import java.time.Instant;
import java.time.ZoneId;
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

    private static final Map<String, String> FIELD_NAME_TRANSLATIONS = Map.of(
            "startTime", "dailyAuditGrid.field.startTime",
            "endTime", "dailyAuditGrid.field.endTime",
            "overtime", "dailyAuditGrid.field.overtime",
            "attendance", "dailyAuditGrid.field.attendance",
            "status", "dailyAuditGrid.field.status",
            "jobPosition", "dailyAuditGrid.field.jobPosition");

    private static final List<String> PREFERRED_FIELD_ORDER =
            List.of("startTime", "endTime", "overtime", "jobPosition", "attendance", "status");

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
        if (!currentAudits.isEmpty()) {
            setDetailsVisible(currentAudits.getLast(), true);
            // 🔧 Workaround: delay refresh to avoid empty space after first details open
            getElement()
                    .executeJs("setTimeout(() => { this.requestContentUpdate && this.requestContentUpdate(); }, 50);");
        }
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
    /**
     * Tworzy komponent z szczeg\u00f3\u0142ami zrekonstruowanego stanu DailyEntry.
     */
    private Component createDetailsComponent(DailyEntryAuditDTO auditDTO) {
        DailyEntryDTO state = reconstructor.reconstructState(auditDTO, currentAudits);

        String jobPosition = state.jobPosition() != null ? state.jobPosition().name() : "-";
        String startTime = state.startTime() != null ? TIME_FORMATTER.format(state.startTime()) : "-";
        String endTime = state.endTime() != null ? TIME_FORMATTER.format(state.endTime()) : "-";
        String overTime = state.overTime() != null ? state.overTime().toPlainString() : "0";
        String attendance = state.attendance() != null
                ? getTranslation(
                        "dailyAudit.attendanceStatus." + state.attendance().name())
                : "-";
        String status = state.status() != null
                ? getTranslation("dailyAudit.entryStatus." + state.status().name())
                : "-";

        // 3. Budowa layoutu
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle().set("border-left", "4px solid var(--lumo-primary-color)");
        layout.getStyle().set("border-radius", "8px");
        layout.getStyle().set("margin", "0 0 0 20px");
        layout.getStyle().set("padding", "0.5em 1em");

        Span titleSpan = new Span(getTranslation("dailyAuditGrid.details.reconstructedStateTitle"));
        titleSpan.getStyle().set("font-weight", "bold");
        titleSpan.getStyle().set("font-size", "1.1em");

        // DODAWANIE STANOWISKA PRACY
        layout.add(
                titleSpan,
                new Span(getTranslation(FIELD_NAME_TRANSLATIONS.get("startTime")) + ": " + startTime),
                new Span(getTranslation(FIELD_NAME_TRANSLATIONS.get("endTime")) + ": " + endTime),
                new Span(getTranslation(FIELD_NAME_TRANSLATIONS.get("overtime")) + ": " + overTime),
                new Span(getTranslation(FIELD_NAME_TRANSLATIONS.get("jobPosition")) + ": "
                        + jobPosition), // NOWY ELEMENT
                new Span(getTranslation(FIELD_NAME_TRANSLATIONS.get("attendance")) + ": " + attendance),
                new Span(getTranslation(FIELD_NAME_TRANSLATIONS.get("status")) + ": " + status));

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

    private String formatChangeDetails(JsonNode payload) {
        if (payload == null) {
            return getTranslation("dailyAuditGrid.noDetails");
        }

        JsonNode oldValues = payload.has("oldValues") ? payload.get("oldValues") : null;
        JsonNode newValues = payload.has("newValues") ? payload.get("newValues") : null;

        if (newValues == null || !newValues.isObject() || newValues.isEmpty()) {
            if (payload.has("operationType")) {
                try {
                    DailyEntryAuditType type = DailyEntryAuditType.valueOf(
                            payload.get("operationType").asText());
                    return getTranslation("dailyAudit.eventType." + type.name());
                } catch (IllegalArgumentException e) {
                    return payload.get("operationType").asText();
                }
            }
            return getTranslation("dailyAuditGrid.noDetails");
        }

        List<String> detailsList = new ArrayList<>();

        // Zbudowanie zbioru zmienionych kluczy (PRAWID\u0141OWA LOGIKA DLA JsonNode)
        Set<String> changedFields = new HashSet<>();
        if (newValues.isObject()) {
            Iterator<String> fieldNames = newValues.fieldNames();
            while (fieldNames.hasNext()) {
                changedFields.add(fieldNames.next());
            }
        }

        // 1. ITERACJA PO PREDEFINIOWANEJ KOLEJNO\u015ACI
        for (String fieldNameKey : PREFERRED_FIELD_ORDER) {
            // Sprawd\u017A, czy to pole by\u0142o faktycznie zmienione w tym audycie
            if (changedFields.contains(fieldNameKey)) {

                String i18nKey = FIELD_NAME_TRANSLATIONS.getOrDefault(fieldNameKey, fieldNameKey);
                String fieldName = getTranslation(i18nKey);

                // 3. Pobranie i sformatowanie warto\u015Bci
                String newValueRaw = newValues.get(fieldNameKey).asText();
                String oldValueRaw = oldValues != null && oldValues.has(fieldNameKey)
                        ? oldValues.get(fieldNameKey).asText()
                        : null;

                // Sprawdzenie, czy surowe warto\u015Bci s\u0105 null/puste (co odpowiada '-')
                boolean isOldValueNullOrBlank =
                        oldValueRaw == null || oldValueRaw.isBlank() || oldValueRaw.equals("null");
                boolean isNewValueNullOrBlank =
                        newValueRaw == null || newValueRaw.isBlank() || newValueRaw.equals("null");

                String newValueFormatted = isNewValueNullOrBlank ? "-" : formatValue(fieldNameKey, newValueRaw);

                String oldValueFormatted = isOldValueNullOrBlank ? "-" : formatValue(fieldNameKey, oldValueRaw);

                // 4. BUDOWA CI\u0104GU ZNAK\u00d3W Z LOGIK\u0104 DLA NULL/PUSTYCH WARTOSCI
                String changeDetail;

                if (isOldValueNullOrBlank && isNewValueNullOrBlank) {
                    // Obie puste/null - pomijamy lub zwracamy pusty ci\u0105g (cho\u0107 to nie powinno si\u0119
                    // zdarzy\u0107, je\u015Bli jest w changedFields)
                    continue;
                } else if (isOldValueNullOrBlank) {
                    // Warto\u015B\u0107 ustawiona (np. Czas zako\u0144czenia: 08:15)
                    changeDetail = String.format("%s: %s", fieldName, newValueFormatted);
                } else if (isNewValueNullOrBlank) {
                    // Warto\u015B\u0107 usuni\u0119ta/wyczyszczona (np. Czas zako\u0144czenia: (08:15 \u2192 -))
                    // W tym przypadku zachowujemy pe\u0142ny format, ale mo\u017Cna upro\u015Bci\u0107
                    // Zostawmy pe\u0142ny format dla usuni\u0119cia (mo\u017Ce by\u0107 u\u017Cyteczne)
                    changeDetail = String.format("%s: (%s \u2192 %s)", fieldName, oldValueFormatted, newValueFormatted);
                } else {
                    // Pe\u0142na zmiana (np. Czas rozpocz\u0119cia: (01:30 \u2192 01:45))
                    changeDetail = String.format("%s: (%s \u2192 %s)", fieldName, oldValueFormatted, newValueFormatted);
                }

                detailsList.add(changeDetail);
            }
        }

        // Po\u0142\u0105czenie wszystkich zmian \u015Brednikiem i spacj\u0105
        return String.join("; ", detailsList);
    }

    private String formatValue(String fieldNameKey, String valueRaw) {
        if (valueRaw == null || valueRaw.isBlank()) {
            return "-";
        }

        return switch (fieldNameKey) {
            case "startTime", "endTime" -> {
                try {
                    yield TIME_FORMATTER.format(Instant.parse(valueRaw));
                } catch (Exception e) {
                    yield valueRaw;
                }
            }

            case "attendance" -> {
                try {
                    yield getTranslation("dailyAudit.attendanceStatus." + valueRaw.toUpperCase());
                } catch (Exception e) {
                    yield valueRaw;
                }
            }
            case "status" -> {
                try {
                    yield getTranslation("dailyAudit.entryStatus." + valueRaw.toUpperCase());
                } catch (Exception e) {
                    yield valueRaw;
                }
            }
            default -> valueRaw;
        };
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
