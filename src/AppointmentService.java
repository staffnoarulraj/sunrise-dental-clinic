import java.util.*;

/**
 * AppointmentService.java
 * Business-logic layer for appointment management.
 * Loads from and persists to data/appointments.txt using pipe-delimited CSV.
 * Provides register, search, list, and count operations.
 * Sunrise Dental Clinic - Patient Management System
 */
public class AppointmentService {

    private static final String APPOINTMENTS_FILE = "appointments.txt";

    /** In-memory list of all appointments (loaded from file at startup). */
    private final List<Appointment> appointments = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    public AppointmentService() {
        loadFromFile();
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    /** Reads all appointments from the data file into the in-memory list. */
    private void loadFromFile() {
        appointments.clear();
        List<String> lines = FileUtil.readLines(APPOINTMENTS_FILE);
        for (String line : lines) {
            Appointment a = Appointment.fromCSV(line);
            if (a != null) {
                appointments.add(a);
            }
        }
    }

    /**
     * Overwrites the data file with the current in-memory list.
     * Used after delete/update operations.
     */
    private void saveAllToFile() {
        List<String> lines = new ArrayList<>();
        for (Appointment a : appointments) {
            lines.add(a.toCSV());
        }
        FileUtil.writeLines(APPOINTMENTS_FILE, lines);
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Registers a new appointment.
     * Performs duplicate appointment-number check before persisting.
     *
     * @param appointment  The appointment to register.
     * @return             true on success; false if the appointment number already exists.
     */
    public boolean registerAppointment(Appointment appointment) {
        if (findByNumber(appointment.getAppointmentNumber()) != null) {
            return false; // duplicate
        }
        appointments.add(appointment);
        FileUtil.appendLine(APPOINTMENTS_FILE, appointment.toCSV()); // faster than full rewrite
        return true;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Searches for an appointment by its unique appointment number (case-insensitive).
     *
     * @param number  The appointment number to search.
     * @return        The matching Appointment, or null if not found.
     */
    public Appointment findByNumber(String number) {
        if (number == null || number.trim().isEmpty()) return null;
        for (Appointment a : appointments) {
            if (a.getAppointmentNumber().equalsIgnoreCase(number.trim())) {
                return a;
            }
        }
        return null;
    }

    /**
     * Searches for appointments where the patient name contains the given query
     * (case-insensitive partial match).
     *
     * @param nameQuery  Partial or full patient name.
     * @return           List of matching appointments (possibly empty).
     */
    public List<Appointment> findByPatientName(String nameQuery) {
        List<Appointment> results = new ArrayList<>();
        if (nameQuery == null || nameQuery.trim().isEmpty()) return results;
        String lower = nameQuery.trim().toLowerCase();
        for (Appointment a : appointments) {
            if (a.getPatientName().toLowerCase().contains(lower)) {
                results.add(a);
            }
        }
        return results;
    }

    /**
     * Returns an unmodifiable view of all appointments in memory.
     *
     * @return  Read-only list of all appointments.
     */
    public List<Appointment> getAllAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    /** Returns the total number of registered appointments. */
    public int getTotalCount() {
        return appointments.size();
    }

    /** Checks whether an appointment number is already in use. */
    public boolean numberExists(String number) {
        return findByNumber(number) != null;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Validates an appointment-number format.
     * Rules: 3–15 characters, letters and digits only, starts with a letter.
     *
     * @param number  The candidate appointment number.
     * @return        true if valid.
     */
    public static boolean isValidAppointmentNumber(String number) {
        if (number == null) return false;
        String trimmed = number.trim();
        return trimmed.matches("[A-Za-z][A-Za-z0-9]{2,14}");
    }

    /**
     * Validates a Sri Lankan mobile number.
     * Accepts formats: 07XXXXXXXX (10 digits starting with 07).
     *
     * @param number  The contact number string.
     * @return        true if valid.
     */
    public static boolean isValidContactNumber(String number) {
        if (number == null) return false;
        return number.trim().matches("0[0-9]{9}");
    }

    /**
     * Validates a date string in DD/MM/YYYY format.
     *
     * @param date  The date string.
     * @return      true if it matches DD/MM/YYYY.
     */
    public static boolean isValidDate(String date) {
        if (date == null) return false;
        return date.trim().matches("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/[0-9]{4}");
    }

    /**
     * Validates a time string in HH:MM (24-hour) format.
     *
     * @param time  The time string.
     * @return      true if it matches HH:MM.
     */
    public static boolean isValidTime(String time) {
        if (time == null) return false;
        return time.trim().matches("([01][0-9]|2[0-3]):[0-5][0-9]");
    }
}
