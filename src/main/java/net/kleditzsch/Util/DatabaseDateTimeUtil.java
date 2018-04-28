package net.kleditzsch.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Hilfsfunktionen zum Serialisieren und Parsen von Zeiten für die Datenbank
 *
 * @author Oliver Kleditzsch
 * @copyright Copyright (c) 2016, Oliver Kleditzsch
 * @license http://opensource.org/licenses/gpl-license.php GNU Public License
 */
public abstract class DatabaseDateTimeUtil {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * erstellt aus einem Datums und Zeit String ein LocalDateTime Objekt
     *
     * @param dateTimeStr Datums String
     * @return Datumsobjekt
     */
    public static LocalDateTime parseDateTimeFromDatabase(String dateTimeStr) {

        return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
    }

    /**
     * erstellt einen Datums und Zeit String aus einem LocalDateTime Objekt
     *
     * @param ldt Datums und Zeit Objekt
     * @return
     */
    public static String getDatabaseDateTimeStr(LocalDateTime ldt) {

        return ldt.format(dateTimeFormatter);
    }

    /**
     * erstellt aus einem Datums und Zeit String ein LocalDate Objekt
     *
     * @param dateStr Datums String
     * @return Datums und Zeit Objekt
     */
    public static LocalDate parseDateFromDatabase(String dateStr) {

        return LocalDate.parse(dateStr, dateFormatter);
    }

    /**
     * erstellt einen Datums und Zeit String aus einem LocalDate Objekt
     *
     * @param dt Datums Objekt
     * @return
     */
    public static String getDatabaseDateStr(LocalDate dt) {

        return dt.format(dateFormatter);
    }

    /**
     * erstellt aus einem Datums und Zeit String ein LocalDateTime Objekt
     *
     * @param timeStr Zeit String
     * @return Datumsobjekt
     */
    public static LocalTime parseTimeFromDatabase(String timeStr) {

        return LocalTime.parse(timeStr, dateTimeFormatter);
    }

    /**
     * erstellt einen Datums und Zeit String aus einem LocalDateTime Objekt
     *
     * @param lt Zeit Objekt
     * @return
     */
    public static String getDatabaseTimeStr(LocalTime lt) {

        return lt.format(dateTimeFormatter);
    }
}
