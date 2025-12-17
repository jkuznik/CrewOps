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

    public NativeScheduleGrid() {
        setSizeFull();
        addListener(ShiftDroppedEvent.class, this::onShiftDropped);
        addListener(ShiftResizedEvent.class, this::onShiftResized);
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
        updateShiftState(event.getShiftId(), -1, event.getNewStartMinute(), event.getNewDurationMinutes());
    }

    private void updateShiftState(String shiftId, int dayNumber, int newStart, int newDuration) {
        // 1. Próba znalezienia zmiany, która już jest na grafiku
        ShiftResource shift = findShiftById(shiftId);

        // 2. Jeśli nie znaleziono na grafiku, sprawdź czy to nowa zmiana z palety
        if (shift == null) {
            ShiftDTO template = findDtoInPalette(shiftId);
            if (template != null) {
                // Tworzymy nowy obiekt zasobu
                shift = new ShiftResource(template);

                // Ustawiamy domyślny czas trwania (np. 8h = 480 min), jeśli nie podano innego
                int duration = (newDuration != -1) ? newDuration : 480;
                shift.setDurationMinutes(duration);

                // Ustawiamy start (jeśli drop nastąpił na 0, to będzie 0)
                shift.setStartMinute(Math.max(0, newStart));

                // Dodajemy nową zmianę do wybranego dnia
                getOrCreateDay(dayNumber).getShifts().add(shift);
            }
        } else {
            // 3. Jeśli zmiana już istniała, aktualizujemy jej parametry (przesunięcie/zmiana rozmiaru)

            // Aktualizacja godziny rozpoczęcia
            if (newStart != -1) {
                shift.setStartMinute(newStart);
            }

            // Aktualizacja czasu trwania (resize)
            if (newDuration != -1) {
                shift.setDurationMinutes(newDuration);
            }

            // Jeśli nastąpił drop na inny dzień (zmiana wiersza)
            if (dayNumber != -1) {
                moveShiftToDay(shift, dayNumber);
            }
        }

        // Jeśli po wszystkich próbach nadal nie mamy obiektu, wychodzimy
        if (shift == null) {
            return;
        }

        // 4. Obsługa przejścia przez północ (Shadow Shift)
        handleMidnightTransition(shift);

        // 5. Synchronizacja z frontendem
        updateClientSideData();
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
                .filter(s -> s.getShiftDTO().id().equals(original.getShiftDTO().id()) && s.isCrossMidnightSegment())
                .findFirst()
                .orElseGet(() -> {
                    ShiftResource shadow = new ShiftResource(original.getShiftDTO());
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
                .filter(s -> s.getShiftDTO().id().toString().equals(id))
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
                sObj.put("id", sr.getShiftDTO().id().toString());
                sObj.put("name", sr.getShiftDTO().name());
                sObj.put("color", sr.getShiftDTO().color());
                sObj.put("startMinute", sr.getStartMinute());
                sObj.put("duration", sr.getDurationMinutes());
                sObj.put("isCross", sr.isCrossMidnightSegment());
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

        public ShiftResizedEvent(
                NativeScheduleGrid source,
                boolean fromClient,
                @EventData("event.detail.shiftId") String shiftId,
                @EventData("event.detail.newStartMinute") int newStartMinute,
                @EventData("event.detail.newDurationMinutes") int newDurationMinutes) {
            super(source, fromClient);
            this.shiftId = shiftId;
            this.newStartMinute = newStartMinute;
            this.newDurationMinutes = newDurationMinutes;
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
    }
}
