package pl.crewops.enums;

import java.time.LocalDate;

public enum DateState {
    PAST,
    TODAY,
    FUTURE;

    /**
     * Mapuje podaną datę (localDate) na odpowiedni stan (PAST, TODAY, FUTURE)
     * w stosunku do aktualnej daty systemowej.
     * * @param localDate Data do sprawdzenia.
     * @return Odpowiedni stan DateState. Zwraca TODAY, jeśli localDate jest null.
     */
    public static DateState fromLocalDate(LocalDate localDate) {
        if (localDate == null) {
            return TODAY;
        }

        LocalDate now = LocalDate.now();

        if (localDate.isBefore(now)) {
            return PAST;
        } else if (localDate.isEqual(now)) {
            return TODAY;
        } else { // localDate.isAfter(now)
            return FUTURE;
        }
    }
}
