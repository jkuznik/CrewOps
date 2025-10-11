package pl.crewops.enums;

public enum DailyEntryAuditType {

    /** Zdarzenie: Czas rozpoczęcia lub zakończenia pracy został zmodyfikowany. */
    WORK_TIME_MODIFIED,

    /** Zdarzenie: Status obecności został zmieniony. */
    ATTENDANCE_STATUS_CHANGED,

    /** Zdarzenie: Dodano nową notatkę (DailyNote) do wpisu (inną niż BHP). */
    DAILY_NOTE_ADDED,

    /** Zdarzenie: Dodano nową notatkę, która jest uwagą BHP/Safety. */
    SAFETY_NOTE_ADDED, // NOWY TYP

    /** Zdarzenie: Rozpoczęto wypełnianie powiązanego raportu. */
    REPORT_STARTED,

    /** Zdarzenie: Raport został zakończony i zatwierdzony. */
    REPORT_COMPLETED,

    /** Zdarzenie: Pole nadgodzin ('overtime') zostało ręcznie zmodyfikowane. */
    OVERTIME_MODIFIED,

    /** Zdarzenie: Status całego wpisu został zmieniony. */
    ENTRY_STATUS_CHANGED
}
