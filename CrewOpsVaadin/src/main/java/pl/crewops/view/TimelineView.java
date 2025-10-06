package pl.crewops.view;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// @Route("timeline")
public class TimelineView extends VerticalLayout {

    public TimelineView() {
        // Ustawienia dla layoutu Vaadin
        setSizeFull();
        setSpacing(true);

        // 1. Zestaw danych: Utworzenie elementów osi czasu
        List<Item> items = createTimelineItems();

        // 2. Utworzenie i konfiguracja komponentu Timeline
        Timeline timeline = new Timeline(items);

        // Ustawienie zakresu wyświetlania na osi czasu (cały dzień)
        timeline.setTimelineRange(LocalDateTime.of(2021, 8, 12, 0, 0, 0), LocalDateTime.of(2021, 8, 12, 23, 59, 59));

        // Konfiguracja
        timeline.setHeight("120px");
        timeline.setMoveable(true);
        timeline.setShowCurentTime(true);
        timeline.setZoomable(false);

        // ----------------------------------------------------
        // KLUCZOWA ZMIANA: KONTENER DLA WYMUSZONEGO SCROLLA
        // ----------------------------------------------------
        Div scrollContainer = new Div(timeline);

        // Ustawienie bardzo dużej stałej szerokości dla timeline.
        // Szerokość 3000px zapewnia wystarczająco miejsca, aby skala vis-timeline
        // automatycznie przeszła na gęstszą podziałkę (np. co 15-30 minut).
        timeline.setWidth("2000px"); // Wymuszamy dużą szerokość

        // Włączamy poziomy scroll w kontenerze, aby to zmieścić w widoku
        scrollContainer.setWidth("60%");
        scrollContainer.setHeight("140px");

        scrollContainer.getStyle().set("overflow-x", "auto");

        // Dodanie kontenera (zamiast samego timeline) do widoku
        add(scrollContainer);
    }

    /**
     * Tworzy i konfiguruje listę elementów (zdarzeń) dla osi czasu.
     * Używamy konfiguracji, którą podałeś jako "edytowalną" wersję.
     */
    private List<Item> createTimelineItems() {
        // UWAGA: Użycie Item z TimeRange (start/end) jest idealne dla tego typu wizualizacji.
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

        Item item3 = new Item(
                LocalDateTime.of(2021, 8, 12, 0, 30, 00),
                LocalDateTime.of(2021, 8, 12, 3, 00, 00),
                "Item 3 - Migracja serwera");
        item3.setId("3");

        Item item4 = new Item(
                LocalDateTime.of(2021, 8, 12, 4, 30, 00),
                LocalDateTime.of(2021, 8, 12, 20, 00, 00),
                "Item 4 - Dzień wolny");
        item4.setId("4");

        Item item5 = new Item(
                LocalDateTime.of(2021, 8, 12, 21, 30, 00),
                LocalDateTime.of(2021, 8, 13, 1, 15, 00),
                "Item 5 - Wdrożenie");
        item5.setId("5");

        List<Item> items = Arrays.asList(item1, item2, item3, item5);

        // Ustawienie edytowalności (użytkownik może przeciągać i zmieniać rozmiar)
        items.forEach(i -> {
            i.setEditable(true); // Umożliwia edycję (przeciąganie/zmianę rozmiaru)
            i.setUpdateTime(true); // Wyświetla czas podczas edycji
        });

        return items;
    }
}
