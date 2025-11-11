package pl.crewops.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeFormater {

    public static final DateTimeFormatter DATE_TIME_HUMAN_READABLE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    public static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    public static final DateTimeFormatter DATE_HUMAN_READABLE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
}
