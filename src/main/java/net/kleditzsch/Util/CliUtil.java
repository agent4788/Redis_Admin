package net.kleditzsch.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Optional;

/**
 * Hilfsfunktionen für die Kommandozeilenverarbeitung
 */
public class CliUtil {

    /**
     * Datenströme
     */
    private static PrintStream out = System.out;
    private static PrintStream err = System.err;
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Eingabeaufforderung
     *
     * @param message Meldung
     * @return Eingabe
     * @throws IOException
     */
    public static String input(String message) throws IOException {

        out.print(message);
        return in.readLine();
    }

    /**
     * fordert den Benutzer zu einer ja/nein Auswahl auf
     *
     * @param message Meldung
     * @param onText Text für "ja"
     * @param offText Texte für "nein"
     * @param value Standardwert
     * @param maxDuartions Maximale Versuche
     * @return Wert
     * @throws IOException
     */
    public static Optional<Boolean> inputOnOffOption(String message, String onText, String offText, boolean value, int maxDuartions) throws IOException {

        int i = 0;
        while(i < maxDuartions) {

            String input = CliUtil.input(message + " (" + (value == true ? onText : offText) + ") : ");

            input.trim();
            if(input.length() > 0) {

                if(input.equals(onText) || input.equals(onText.substring(0, 1))) {

                    //an
                    return Optional.of(true);
                } else if(input.equals(offText) || input.equals(offText.substring(0, 1))) {

                    //aus
                    return Optional.of(false);
                }
            } else {

                return Optional.empty();
            }

            i++;
            err.println("Fehlerhafte Eingabe");
        }
        throw new InputMismatchException();
    }

    /**
     * fordert den Benutzer auf eine Zeichenkette ein zu geben
     *
     * @param message Meldung
     * @param value Standardwert
     * @return Wert
     * @throws IOException
     */
    public static Optional<String> inputStringOption(String message, String value) throws IOException {

        String input = CliUtil.input(message + " (" + value + ") : ");

        input.trim();
        if(input.length() > 0) {

            return Optional.of(input);
        } else {

            return Optional.empty();
        }
    }

    /**
     * fordert den Benutzer auf eine Ganzzahl ein zu geben
     *
     * @param message Meldung
     * @param value Standartwert
     * @param min Minimalwert
     * @param max Maximalwert
     * @param maxDuartions  Maximale Versuche
     * @return Wert
     * @throws IOException
     */
    public static Optional<Integer> inputIntegerOption(String message, int value, int min, int max, int maxDuartions) throws IOException {

        int i = 0;
        while(i < maxDuartions) {

            String input = CliUtil.input(message + " (" + value + ") : ");

            input.trim();
            if(input.length() > 0) {

                try {

                    int newValue = Integer.parseInt(input);
                    if(newValue >= min && newValue <= max) {

                        return Optional.of(newValue);
                    }
                } catch (NumberFormatException ex) {}
            } else {

                return Optional.empty();
            }

            i++;
            err.println("Fehlerhafte Eingabe");
        }
        throw new InputMismatchException();
    }


    public static Optional<String> inputIpAddressOption(String message, String value, int maxDuartions) throws IOException {

        int i = 0;
        while(i < maxDuartions) {

            String input = CliUtil.input(message + " (" + value + ") : ");

            input.trim();
            if(input.length() > 0 && input.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {

                String[] parts = input.split("\\.");
                boolean errorFound = false;
                for(int j = 0; j < parts.length; j++) {

                    Integer part = Integer.parseInt(parts[j]);
                    if(part < 0 || part > 255) {

                        errorFound = true;
                    }
                }

                if(!errorFound) {

                    return Optional.of(input);
                }
            } else {

                return Optional.empty();
            }

            i++;
            err.println("Fehlerhafte Eingabe");
        }
        throw new InputMismatchException();
    }
}
