package pl.crewops.component.custom;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class DailyTimeline extends Div {

    private static final int TIMELINE_WIDTH_PX = 2000;

    private static final int SCROLL_OFFSET_PX = 400;

    public DailyTimeline(List<Item> items) {
        setWidth("80%");
        setHeight("140px");
        getStyle().set("overflow-x", "auto");
        getStyle().set("border", "1px solid #ccc");
        getStyle().set("border-radius", "4px");
        getStyle().set("padding", "5px");

        add(getConfiguredTimeline(items));

        int scrollPosition = calculateScrollPosition();
        String jsCode = String.format("setTimeout(function() { $0.scrollLeft = %d; }, 100);", scrollPosition);
        UI.getCurrent().getPage().executeJs(jsCode, this.getElement());
    }

    private int calculateScrollPosition() {
        LocalTime now = LocalTime.now();

        // 1. Całkowita długość dnia w sekundach
        long totalDaySeconds = 24 * 60 * 60;

        // 2. Ilość sekund, która upłynęła od początku dnia
        long elapsedSeconds = now.toSecondOfDay();

        // 3. Pozycja aktualnej godziny na osi czasu (0 do 2000px)
        double currentPositionOnTimeline = ((double) elapsedSeconds / totalDaySeconds) * TIMELINE_WIDTH_PX;

        // Ostateczna pozycja scrolla: Przesuwamy scrollbar tak, aby bieżąca godzina
        // była oddalona o SCROLL_OFFSET_PX od lewej krawędzi widocznego kontenera.
        int scrollPosition = (int) Math.round(currentPositionOnTimeline - SCROLL_OFFSET_PX);

        // --- ZABEZPIECZENIA ---

        // 1. Minimum scrolla (nie scrollujemy przed początek)
        if (scrollPosition < 0) {
            scrollPosition = 0;
        }

        // 2. Maksimum scrolla
        // Musimy oszacować, jak szeroki jest widoczny kontener w pikselach
        // Ponieważ szerokość 80% jest dynamiczna, musimy ją oszacować.
        // Bez dokładnej wiedzy o szerokości klienta, możemy użyć stałej,
        // która zapewni, że koniec osi czasu (2000px) będzie widoczny.
        // Jeżeli widoczny obszar ma np. 800px, maksymalny scroll to 2000 - 800 = 1200px.
        // Użyjemy 1200px jako bezpiecznego maksimum.
        final int MAX_SCROLL = TIMELINE_WIDTH_PX - 800;

        if (scrollPosition > MAX_SCROLL) {
            scrollPosition = MAX_SCROLL;
        }

        return scrollPosition;
    }

    private Timeline getConfiguredTimeline(List<Item> items) {

        Timeline timeline = new Timeline(items);

        LocalDate today = LocalDate.now();
        timeline.setTimelineRange(LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX));

        timeline.setHeight("120px");
        timeline.setMoveable(true);
        timeline.setShowCurentTime(true);
        timeline.setZoomable(false);
        timeline.setWidth(TIMELINE_WIDTH_PX + "px");

        return timeline;
    }

    // template of items creation (Item.class that is supported by Timeline.class)
    private List<Item> createTimelineItems() {

        LocalDate staticDay = LocalDate.of(2021, 8, 12);

        Item item1 = new Item(
                LocalDateTime.of(2021, 8, 11, 2, 30, 00),
                LocalDateTime.of(2021, 8, 11, 8, 00, 00),
                "Item 1 - Praca na projekcie A");
        item1.setId("1");

        Item item2 = new Item(
                LocalDateTime.of(2021, 8, 11, 9, 00, 00),
                LocalDateTime.of(2021, 8, 11, 17, 00, 00),
                "Item 2 - Spotkanie z klientem");
        item2.setId("2");

        Item item3 = new Item(staticDay.atTime(0, 30, 00), staticDay.atTime(3, 0, 00), "Item 3 - Migracja serwera");
        item3.setId("3");

        Item item4 = new Item(staticDay.atTime(4, 30, 00), staticDay.atTime(20, 0, 00), "Item 4 - Dzień wolny");
        item4.setId("4");

        Item item5 =
                new Item(staticDay.atTime(21, 30, 00), staticDay.plusDays(1).atTime(1, 15, 00), "Item 5 - Wdrożenie");
        item5.setId("5");

        List<Item> items = Arrays.asList(item1, item2, item3, item4, item5);

        items.forEach(i -> {
            i.setEditable(true);
            i.setUpdateTime(true);
        });

        return items;
    }
}
