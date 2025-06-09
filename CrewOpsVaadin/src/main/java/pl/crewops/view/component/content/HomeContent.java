package pl.crewops.view.component.content;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class HomeContent extends VerticalLayout {

    public HomeContent() {
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        FlexLayout mainLayout = new FlexLayout();
        mainLayout.setWidthFull();
        mainLayout
                .getStyle()
                .set("gap", "40px")
                .set("align-items", "start")
                .set("padding", "20px")
                .set("overflow-x", "hidden")
                .set("box-sizing", "border-box")
                .set("max-width", "100vw");

        Div imageContainer = new Div();
        imageContainer.setWidth("50%");
        imageContainer
                .getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("overflow", "hidden");

        Image headerImage = new Image("images/home-view-pic.png", "home view");
        headerImage
                .getStyle()
                .set("max-width", "100%")
                .set("height", "auto")
                .set("object-fit", "contain")
                .set("display", "block");

        imageContainer.add(headerImage);

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setWidth("50%");
        textLayout.setPadding(false);
        textLayout.setSpacing(true);

        H2 title = new H2("CrewOps – sprawne zarządzanie zespołami i sprzętem w terenie");

        Paragraph intro = new Paragraph(
                "Aplikacja została zaprojektowana z myślą o usprawnieniu codziennej pracy technicznych załóg pracowniczych oraz skutecznym monitorowaniu dostępności sprzętu ciężkiego.");

        UnorderedList features = new UnorderedList(
                new ListItem(
                        "Zarządzanie załogami pracowniczymi – możliwość przypisywania pracowników do konkretnych zmian, zespołów roboczych lub maszyn. System umożliwia śledzenie dostępności kadry w czasie rzeczywistym."),
                new ListItem(
                        "Monitoring i ewidencja maszyn – przegląd aktualnie dostępnych, przypisanych lub wyłączonych z użytku maszyn."),
                new ListItem(
                        "Zgłaszanie i rejestracja awarii – użytkownicy mogą zgłaszać usterki techniczne maszyn bezpośrednio z poziomu aplikacji. Każde zgłoszenie trafia do rejestru usterek, gdzie można śledzić jego status i historię napraw."),
                new ListItem(
                        "Panel administracyjny – pełna kontrola nad uprawnieniami użytkowników, widokiem systemu, logami działań."),
                new ListItem(
                        "Obecnie dostępna przez przeglądarkę internetową. Wersja mobilna na smartfony – w przygotowaniu."));

        Paragraph loginInfo = new Paragraph("Dane logowania – konta przykładowe:");
        loginInfo.getStyle().set("margin-top", "20px");

        Paragraph credentials = new Paragraph();
        credentials
                .getElement()
                .setProperty(
                        "innerHTML",
                        "admin / admin<br>user / user<br><br>Oba konta obecnie mają pełne uprawnienia, umożliwiające testowanie wszystkich funkcji systemu.");

        textLayout.add(title, intro, features, loginInfo, credentials);

        mainLayout.add(imageContainer, textLayout);
        add(mainLayout);
    }
}
