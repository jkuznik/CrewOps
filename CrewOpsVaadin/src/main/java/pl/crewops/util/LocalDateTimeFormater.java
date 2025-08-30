package pl.crewops.util;

import java.time.format.DateTimeFormatter;

public class LocalDateTimeFormater {

    public static final DateTimeFormatter DATE_TIME_HUMAN_READABLE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
}
