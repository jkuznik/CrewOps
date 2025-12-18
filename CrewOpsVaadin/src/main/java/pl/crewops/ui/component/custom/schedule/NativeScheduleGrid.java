package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import pl.crewops.model.dto.shift.ShiftDTO;

@Tag("native-schedule-grid")
@JsModule("./ts/NativeScheduleGrid.ts")
@CssImport("./styles/component/dailyView/native-schedule.css")
public class NativeScheduleGrid extends Component implements HasSize {

    private final List<ScheduleDay> dayList = new ArrayList<>();
    private static final int MINUTES_PER_DAY = 1440;

    private final List<ShiftDTO> paletteTemplates = new ArrayList<>();
    private final String headerDayCell = getTranslation("nativeScheduleGrid.header.day");

    public NativeScheduleGrid() {
        setSizeFull();
        addListener(ShiftDroppedEvent.class, this::onShiftDropped);
        addListener(ShiftResizedEvent.class, this::onShiftResized);
        addListener(ShiftDeletedEvent.class, this::onShiftDeleted);

        getElement().setProperty("dayLabelText", headerDayCell);
    }

    public void registerPaletteTemplate(ShiftDTO dto) {
        this.paletteTemplates.add(dto);
    }

    private ShiftDTO findDtoInPalette(String shiftId) {
        return paletteTemplates.stream()
                .filter(dto -> dto.id().toString().equals(shiftId))
                .findFirst()
                .orElse(null);
    }

    // --- LOGIKA BIZNESOWA (HANDLERY) ---

    private void onShiftDropped(ShiftDroppedEvent event) {
        updateShiftState(event.getShiftId(), event.getDayNumber(), event.getNewStartMinute(), -1);
    }

    private void onShiftResized(ShiftResizedEvent event) {
        int duration = event.getNewDurationMinutes();

        if (event.isShadow()) {
            // Teraz szukamy oryginału po TYM SAMYM instanceId, ale z flagą !isCross
            ShiftResource original = dayList.stream()
                    .flatMap(d -> d.getShifts().stream())
                    .filter(s -> s.getInstanceId().equals(event.getShiftId()) && !s.isCrossMidnightSegment())
                    .findFirst()
                    .orElse(null);

            if (original != null) {
                int minutesBeforeMidnight = MINUTES_PER_DAY - original.getStartMinute();
                duration = minutesBeforeMidnight + event.getNewDurationMinutes();
            }
        }

        // Wywołujemy updateShiftState przekazując instanceId
        updateShiftState(event.getShiftId(), -1, -1, duration);
    }

    private void updateShiftState(String idFromClient, int dayNumber, int newStart, int newDuration) {
        // 1. Sprawdź, czy to istniejąca na grafiku instancja
        ShiftResource shift = findShiftByInstanceId(idFromClient);

        if (shift == null) {
            // SCENARIUSZ: To może być nowa zmiana przeciągnięta z palety
            ShiftDTO template = findDtoInPalette(idFromClient);
            if (template != null && dayNumber != -1) {

                // WALIDACJA: Czy w tym konkretnym dniu jest już zmiana z tego szablonu?
                boolean existsInDay = getOrCreateDay(dayNumber).getShifts().stream()
                        .anyMatch(s -> s.getShiftDTO().id().toString().equals(idFromClient));

                if (!existsInDay) {
                    shift = new ShiftResource(template);
                    shift.setStartMinute(Math.max(0, newStart));
                    getOrCreateDay(dayNumber).getShifts().add(shift);
                }
            }
        } else {
            // SCENARIUSZ: Przesuwamy/zmieniamy rozmiar istniejącej instancji
            if (newStart != -1) shift.setStartMinute(newStart);
            if (newDuration != -1) shift.setDurationMinutes(newDuration);
            if (dayNumber != -1) moveShiftToDay(shift, dayNumber);
        }

        if (shift != null) {
            handleMidnightTransition(shift);
            updateClientSideData();
        }
    }

    // Pomocnicza metoda szukająca po InstanceId
    private ShiftResource findShiftByInstanceId(String instanceId) {
        return dayList.stream()
                .flatMap(d -> d.getShifts().stream())
                .filter(s -> s.getInstanceId().equals(instanceId))
                .findFirst()
                .orElse(null);
    }

    private void handleMidnightTransition(ShiftResource shift) {
        int currentDayIdx = getDayIndex(shift);
        if (currentDayIdx == -1) return;

        ScheduleDay currentDay = dayList.get(currentDayIdx);
        int endMinute = shift.getStartMinute() + shift.getDurationMinutes();

        if (endMinute > MINUTES_PER_DAY) {
            // PRZEKRACZA PÓŁNOC
            shift.setHasCrossMidnightSegment(true);
            int nextDayEndMinute = endMinute - MINUTES_PER_DAY;

            // Znajdź lub stwórz następny dzień
            ScheduleDay nextDay = getOrCreateDay(currentDay.getDayNumber() + 1);

            // Znajdź lub stwórz segment-cień (isCrossMidnightSegment = true)
            ShiftResource shadow = findOrCreateShadowShift(nextDay, shift);
            shadow.setStartMinute(0);
            shadow.setDurationMinutes(nextDayEndMinute);
        } else {
            // NIE PRZEKRACZA PÓŁNOCY
            if (shift.isHasCrossMidnightSegment()) {
                shift.setHasCrossMidnightSegment(false);
                removeShadowFromNextDay(currentDay.getDayNumber() + 1, shift);
            }
        }
    }

    // --- METODY POMOCNICZE ---

    private ShiftResource findOrCreateShadowShift(ScheduleDay nextDay, ShiftResource original) {
        return nextDay.getShifts().stream()
                .filter(s -> s.getInstanceId().equals(original.getInstanceId()) && s.isCrossMidnightSegment())
                .findFirst()
                .orElseGet(() -> {
                    ShiftResource shadow = new ShiftResource(original.getShiftDTO());
                    shadow.setInstanceId(original.getInstanceId()); // KOPIUJEMY ID INSTANCJI
                    shadow.setCrossMidnightSegment(true);
                    nextDay.getShifts().add(shadow);
                    return shadow;
                });
    }

    private void removeShadowFromNextDay(int nextDayNumber, ShiftResource original) {
        dayList.stream()
                .filter(d -> d.getDayNumber() == nextDayNumber)
                .findFirst()
                .ifPresent(day -> day.getShifts()
                        .removeIf(s -> s.getShiftDTO()
                                        .id()
                                        .equals(original.getShiftDTO().id())
                                && s.isCrossMidnightSegment()));
    }

    private ScheduleDay getOrCreateDay(int dayNumber) {
        return dayList.stream()
                .filter(d -> d.getDayNumber() == dayNumber)
                .findFirst()
                .orElseGet(() -> {
                    ScheduleDay newDay = new ScheduleDay(dayNumber);
                    dayList.add(newDay);
                    dayList.sort((a, b) -> Integer.compare(a.getDayNumber(), b.getDayNumber()));
                    return newDay;
                });
    }

    private ShiftResource findShiftById(String id) {
        return dayList.stream()
                .flatMap(d -> d.getShifts().stream())
                // Filtrujemy po ID oraz upewniamy się, że to NIE JEST segment cienia
                .filter(s -> s.getShiftDTO().id().toString().equals(id) && !s.isCrossMidnightSegment())
                .findFirst()
                .orElse(null);
    }

    private int getDayIndex(ShiftResource shift) {
        for (int i = 0; i < dayList.size(); i++) {
            if (dayList.get(i).getShifts().contains(shift)) return i;
        }
        return -1;
    }

    private void moveShiftToDay(ShiftResource shift, int dayNumber) {
        dayList.forEach(d -> d.getShifts().remove(shift));
        getOrCreateDay(dayNumber).getShifts().add(shift);
    }

    public void updateClientSideData() {
        JsonArray daysArray = Json.createArray();
        for (int i = 0; i < dayList.size(); i++) {
            ScheduleDay day = dayList.get(i);
            JsonObject dayObj = Json.createObject();
            dayObj.put("dayNumber", day.getDayNumber());

            JsonArray shiftsArray = Json.createArray();
            List<ShiftResource> shifts = day.getShifts();
            for (int j = 0; j < shifts.size(); j++) {
                ShiftResource sr = shifts.get(j);
                JsonObject sObj = Json.createObject();

                // KLUCZOWA ZMIANA: wysyłamy instanceId zamiast shiftDTO.id()
                sObj.put("id", sr.getInstanceId());

                sObj.put("name", sr.getShiftDTO().name());
                sObj.put("color", sr.getShiftDTO().color());
                sObj.put("startMinute", sr.getStartMinute());
                sObj.put("duration", sr.getDurationMinutes());
                sObj.put("isCross", sr.isCrossMidnightSegment());

                // Dodatkowo przesyłamy ID szablonu, żeby przy usuwaniu/walidacji wiedzieć co to za typ
                sObj.put("templateId", sr.getShiftDTO().id().toString());

                shiftsArray.set(j, sObj);
            }
            dayObj.put("shifts", shiftsArray);
            daysArray.set(i, dayObj);
        }
        getElement().setPropertyJson("days", daysArray);
    }

    public void addDay(ScheduleDay day) {
        if (!dayList.contains(day)) {
            dayList.add(day);
            dayList.sort((a, b) -> Integer.compare(a.getDayNumber(), b.getDayNumber()));
        }
    }

    private void onShiftDeleted(ShiftDeletedEvent event) {
        String instanceId = event.getShiftId();

        // Usuwamy tylko te obiekty, które mają to samo instanceId (czyli oryginał i jego ewentualny cień)
        dayList.forEach(day -> day.getShifts().removeIf(s -> s.getInstanceId().equals(instanceId)));

        updateClientSideData();
    }

    // Pozwala pobrać listę dni do manipulacji
    public List<ScheduleDay> getDayList() {
        return dayList;
    }

    // Czyści cały grafik
    public void clear() {
        this.dayList.clear();
        updateClientSideData();
    }

    // --- EVENTS (Bez zmian) ---

    @DomEvent("shift-dropped")
    public static class ShiftDroppedEvent extends ComponentEvent<NativeScheduleGrid> {
        private final String shiftId;
        private final int dayNumber;
        private final int newStartMinute;

        public ShiftDroppedEvent(
                NativeScheduleGrid source,
                boolean fromClient,
                @EventData("event.detail.shiftId") String shiftId,
                @EventData("event.detail.dayNumber") int dayNumber,
                @EventData("event.detail.newStartMinute") int newStartMinute) {
            super(source, fromClient);
            this.shiftId = shiftId;
            this.dayNumber = dayNumber;
            this.newStartMinute = newStartMinute;
        }

        public String getShiftId() {
            return shiftId;
        }

        public int getDayNumber() {
            return dayNumber;
        }

        public int getNewStartMinute() {
            return newStartMinute;
        }
    }

    @DomEvent("shift-resized")
    public static class ShiftResizedEvent extends ComponentEvent<NativeScheduleGrid> {
        private final String shiftId;
        private final int newStartMinute;
        private final int newDurationMinutes;
        private final boolean isShadow; // Nowe pole

        public ShiftResizedEvent(
                NativeScheduleGrid source,
                boolean fromClient,
                @EventData("event.detail.shiftId") String shiftId,
                @EventData("event.detail.newStartMinute") int newStartMinute,
                @EventData("event.detail.newDurationMinutes") int newDurationMinutes,
                @EventData("event.detail.isShadow") boolean isShadow) { // Odbieramy z JS
            super(source, fromClient);
            this.shiftId = shiftId;
            this.newStartMinute = newStartMinute;
            this.newDurationMinutes = newDurationMinutes;
            this.isShadow = isShadow;
        }

        public String getShiftId() {
            return shiftId;
        }

        public int getNewStartMinute() {
            return newStartMinute;
        }

        public int getNewDurationMinutes() {
            return newDurationMinutes;
        }

        public boolean isShadow() {
            return isShadow;
        }
    }

    @DomEvent("shift-deleted")
    public static class ShiftDeletedEvent extends ComponentEvent<NativeScheduleGrid> {
        private final String shiftId;

        public ShiftDeletedEvent(
                NativeScheduleGrid source, boolean fromClient, @EventData("event.detail.shiftId") String shiftId) {
            super(source, fromClient);
            this.shiftId = shiftId;
        }

        public String getShiftId() {
            return shiftId;
        }
    }
}
