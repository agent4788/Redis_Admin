package net.kleditzsch.Util;

import java.text.DecimalFormat;

/**
 * Hilfsfunktionen zur bearbeitung von Dateien
 */
public abstract class FileUtil {

    /**
     * Formatiert eine Dateigroeße in Bytes in einen Anzeigewert (Basis 1024)
     *
     * @param filesize Dateigroeße
     * @return
     */
    public static String formatFilezizeBinary(int filesize) {

        return FileUtil.formatFilezizeBinary(filesize, true);
    }

    /**
     * Formatiert eine Dateigroeße in Bytes in einen Anzeigewert (Basis 1024)
     *
     * @param filesize Dateigroeße
     * @param useShortNames Namen in Kurzversion benutzen
     * @return
     */
    public static String formatFilezizeBinary(int filesize, boolean useShortNames) {

        if(useShortNames) {

            String[] norm = {"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};

            int count = norm.length;
            int factor = 1024;
            int x = 0;
            double size = filesize;

            while(size >= factor && x < count) {

                size /= factor;
                x++;
            }

            DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );
            return format.format(size) + " " + norm[x];
        }

        String[] norm = {"Byte", "Kibibyte", "Mebibyte", "Gibibyte", "Tebibyte", "Pebibyte", "Exbibyte", "Zebibyte", "Yobibyte"};

        int count = norm.length;
        int factor = 1024;
        int x = 0;
        double size = filesize;

        while(size >= factor && x < count) {

            size /= factor;
            x++;
        }

        DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );
        return format.format(size) + " " + norm[x];
    }

    /**
     * Formatiert eine Dateigroeße in Bytes in einen Anzeigewert (Basis 1000)
     *
     * @param filesize Dateigroeße
     * @return
     */
    public static String formatFilesize(int filesize) {

        return FileUtil.formatFilesize(filesize, true);
    }

    /**
     * Formatiert eine Dateigroeße in Bytes in einen Anzeigewert (Basis 1000)
     *
     * @param filesize Dateigroeße
     * @param useShortNames Namen in Kurzversion benutzen
     * @return
     */
    public static String formatFilesize(int filesize, boolean useShortNames) {

        if(useShortNames) {

            String[] norm = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

            int count = norm.length;
            int factor = 1000;
            int x = 0;
            double size = filesize;

            while(size >= factor && x < count) {

                size /= factor;
                x++;
            }

            DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );
            return format.format(size) + " " + norm[x];
        }

        String[] norm = {"Byte", "Kilobyte", "Megabyte", "Gigabyte", "Terrabyte", "Petabyte", "Exabyte", "Zettabyte", "Yottabyte"};

        int count = norm.length;
        int factor = 1000;
        int x = 0;
        double size = filesize;

        while(size >= factor && x < count) {

            size /= factor;
            x++;
        }

        DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );
        return format.format(size) + " " + norm[x];
    }
}
