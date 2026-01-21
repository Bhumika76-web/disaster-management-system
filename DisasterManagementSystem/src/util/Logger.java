package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Logger {
    private static final String LOG_FILE = "app_logs.txt";
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static void info(String className, String message) {
        log("INFO", className, message);
    }


    public static void warn(String className, String message) {
        log("WARN", className, message);
    }


    public static void error(String className, String message, Exception e) {
        String fullMessage = message;
        if (e != null) {
            fullMessage = message + " | Exception: " + e.getMessage();
        }
        log("ERROR", className, fullMessage);
    }


    public static void debug(String className, String message) {
        log("DEBUG", className, message);
    }


    private static void log(String level, String className, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] %s - %s: %s",
                timestamp, level, className, message);

        // Print to console
        System.out.println(logMessage);

        // Write to file
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(logMessage + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
