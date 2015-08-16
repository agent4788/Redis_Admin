package net.kleditzsch.Util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Hilfsfunktionen zur Datums und Zeitverarbeitung
 */
public abstract class TimeUtil {

    /**
     * gibt ein Lokalisiertes DateTime Objekt zum Zeitstempel zurueck (Zeitzone Europe/Berlin)
     *
     * @param seconds Zeitstempel
     * @return
     */
    public static LocalDateTime getLocalDateTimeOfEpochSeconds(long seconds) {

        return TimeUtil.getLocalDateTimeOfEpochSeconds(seconds, "Europe/Berlin");
    }

    /**
     * gibt ein Lokalisiertes DateTime Objekt zum Zeitstempel zurueck
     *
     * @param seconds Zeitstempel
     * @param timeZoneName Zeitzone
     * @return
     */
    public static LocalDateTime getLocalDateTimeOfEpochSeconds(long seconds, String timeZoneName) {

        return LocalDateTime.ofEpochSecond(seconds, 0, LocalDateTime.now().atZone(ZoneId.of(timeZoneName)).getOffset());
    }

    /**
     * gibt ein Instant zum Zeitstempel zurueck
     *
     * @param seconds Zeitstempel
     * @return
     */
    public static Instant getInstantOfEpoch(long seconds) {

        return Instant.ofEpochSecond(seconds, 0);
    }

    /**
     * formatiert eine Zeitdifferenz
     *
     * @param diff
     * @return
     */
    public static String formatDuration(Duration diff) {

        return TimeUtil.formatDuration(diff, true);
    }

    /**
     * formatiert eine Zeitdifferenz
     *
     * @param diff
     * @param useShortNames
     * @return
     */
    public static String formatDuration(Duration diff, boolean useShortNames) {

        long days = diff.toDays();
        long hours = diff.minusDays(days).toHours();
        long minutes = diff.minusHours(hours).toMinutes();

        String output = "";
        if(diff.isNegative()) {

            //vergangenheit
            output += "vor";
        } else {

            //Zukunft
            output += "in";
        }

        if(useShortNames) {

            if(days != 0) {

                output += " " + Math.abs(days) + "T";
            }

            if(hours != 0) {

                output += " " + Math.abs(hours) + "S";
            }

            if(minutes != 0) {

                output += " " + Math.abs(minutes) + "m";
            }
        } else {

            if(days != 0) {

                output += " " + Math.abs(days) + (Math.abs(days) == 1 ? " Tag" : " Tagen");
            }

            if(hours != 0) {

                output += " " + Math.abs(hours) + (Math.abs(hours) == 1 ? " Stunde" : " Stunden");
            }

            if(minutes != 0) {

                output += " " + Math.abs(minutes) + (Math.abs(minutes) == 1 ? " Minute" : " Minuten");
            }
        }

        if(days == 0 && hours == 0 && minutes == 0 && diff.isNegative()) {

            output = "gerade eben";
        } else if(days == 0 && hours == 0 && minutes == 0 && !diff.isNegative()) {

            output = "jetzt";
        }

        return output;
    }

    /**
     * formatiert eine Zeit Angabe in Sekunden
     *
     * @param seconds
     * @return
     */
    public static String formatSeconds(long seconds) {

        return TimeUtil.formatSeconds(seconds, true);
    }

    /**
     * formatiert eine Zeit Angabe in Sekunden
     *
     * @param seconds
     * @param useShortNames
     * @return
     */
    public static String formatSeconds(long seconds, boolean useShortNames) {

        Duration diff = Duration.between(Instant.now(), Instant.ofEpochSecond(Instant.now().getEpochSecond() + seconds));

        long days = diff.toDays();
        long hours = diff.minusDays(days).toHours();
        long minutes = diff.minusHours(hours).toMinutes();

        String output = "";
        if(useShortNames) {

            if(days != 0) {

                output += " " + Math.abs(days) + "T";
            }

            if(hours != 0) {

                output += " " + Math.abs(hours) + "S";
            }

            if(minutes != 0) {

                output += " " + Math.abs(minutes) + "m";
            }
        } else {

            if(days != 0) {

                output += " " + Math.abs(days) + (Math.abs(days) == 1 ? " Tag" : " Tagen");
            }

            if(hours != 0) {

                output += " " + Math.abs(hours) + (Math.abs(hours) == 1 ? " Stunde" : " Stunden");
            }

            if(minutes != 0) {

                output += " " + Math.abs(minutes) + (Math.abs(minutes) == 1 ? " Minute" : " Minuten");
            }
        }

        if(days == 0 && hours == 0 && minutes == 0 && diff.isNegative()) {

            output = "gerade eben";
        } else if(days == 0 && hours == 0 && minutes == 0 && !diff.isNegative()) {

            output = "jetzt";
        }

        return output;
    }
}
