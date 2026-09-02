import java.io.*;
import java.util.*;

/**
 * FileUtil.java
 * Utility class for reading and writing text files in the "data/" directory.
 * Handles directory creation on first run.
 * Sunrise Dental Clinic - Patient Management System
 */
public class FileUtil {

    /** Relative path to the data directory (resolved from the working directory). */
    public static final String DATA_DIR = "data";

    // Prevent instantiation
    private FileUtil() {}

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * Creates the data directory if it does not already exist.
     * Should be called once at application startup.
     */
    public static void initDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("[FileUtil] WARNING: Could not create data directory: " + DATA_DIR);
            }
        }
    }

    // ── Path Helper ───────────────────────────────────────────────────────────

    /** Returns the full path for a given filename inside the data directory. */
    public static String path(String filename) {
        return DATA_DIR + File.separator + filename;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Reads all non-empty lines from the given file.
     *
     * @param filename  The filename inside the data directory.
     * @return          A list of trimmed, non-empty lines. Empty list if file absent.
     */
    public static List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(path(filename));
        if (!file.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    lines.add(trimmed);
                }
            }
        } catch (IOException e) {
            System.err.println("[FileUtil] Error reading '" + filename + "': " + e.getMessage());
        }
        return lines;
    }

    // ── Write (overwrite) ─────────────────────────────────────────────────────

    /**
     * Writes (overwrites) all lines to the given file.
     *
     * @param filename  The filename inside the data directory.
     * @param lines     Lines to write.
     */
    public static void writeLines(String filename, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path(filename), false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[FileUtil] Error writing '" + filename + "': " + e.getMessage());
        }
    }

    // ── Append ────────────────────────────────────────────────────────────────

    /**
     * Appends a single line to the given file (creates file if absent).
     *
     * @param filename  The filename inside the data directory.
     * @param line      The line to append.
     */
    public static void appendLine(String filename, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path(filename), true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[FileUtil] Error appending to '" + filename + "': " + e.getMessage());
        }
    }

    // ── Existence Check ───────────────────────────────────────────────────────

    /** Returns true if the given data file exists. */
    public static boolean fileExists(String filename) {
        return new File(path(filename)).exists();
    }
}
