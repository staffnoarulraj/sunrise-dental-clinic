import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * BillingService.java
 * Handles treatment-fee lookup, total bill calculation, and formatted receipt printing.
 * The consultation fee is always added on top of the treatment fee.
 * Sunrise Dental Clinic - Patient Management System
 */
public class BillingService {

    // ── Constants ─────────────────────────────────────────────────────────────

    /** Fixed consultation fee applied to every appointment (LKR). */
    public static final double CONSULTATION_FEE = 1_500.00;

    /** Width of the content area inside the receipt box (excluding border chars). */
    private static final int RECEIPT_WIDTH = 58;

    /** Clinic contact info printed on the receipt. */
    private static final String CLINIC_PHONE   = "+94 11 234 5678";
    private static final String CLINIC_EMAIL   = "info@sunrisedental.lk";
    private static final String CLINIC_ADDRESS = "No. 45, Galle Road, Colombo 03";

    // ── Treatment Fee Table ───────────────────────────────────────────────────

    /**
     * Ordered map of treatment names to their fees (LKR).
     * Use {@link #getTreatmentList()} to get the ordered list for display.
     */
    private static final LinkedHashMap<String, Double> TREATMENT_FEES = new LinkedHashMap<>();

    static {
        TREATMENT_FEES.put("Cleaning",            2_500.00);
        TREATMENT_FEES.put("Filling",             5_000.00);
        TREATMENT_FEES.put("Root Canal",         15_000.00);
        TREATMENT_FEES.put("Extraction",          3_500.00);
        TREATMENT_FEES.put("Teeth Whitening",     8_000.00);
        TREATMENT_FEES.put("Braces Consultation",12_000.00);
        TREATMENT_FEES.put("X-Ray",               2_000.00);
        TREATMENT_FEES.put("Crown Fitting",      18_000.00);
        TREATMENT_FEES.put("Dentures",           25_000.00);
        TREATMENT_FEES.put("Scaling",             3_000.00);
    }

    // Prevent instantiation
    private BillingService() {}

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns an unmodifiable, ordered map of all treatments and their fees.
     */
    public static Map<String, Double> getTreatmentFees() {
        return Collections.unmodifiableMap(TREATMENT_FEES);
    }

    /**
     * Returns the ordered list of treatment names (for numbered menu display).
     */
    public static List<String> getTreatmentList() {
        return new ArrayList<>(TREATMENT_FEES.keySet());
    }

    /**
     * Looks up the fee for a given treatment type.
     *
     * @param treatmentType  Exact treatment name (as returned by getTreatmentList).
     * @return               Treatment fee in LKR, or 0.00 if not found.
     */
    public static double getTreatmentFee(String treatmentType) {
        return TREATMENT_FEES.getOrDefault(treatmentType, 0.00);
    }

    /**
     * Calculates the total bill for an appointment.
     * Total = Consultation Fee + Treatment Fee.
     *
     * @param appointment  The appointment whose bill is to be calculated.
     * @return             Total amount due in LKR.
     */
    public static double calculateTotal(Appointment appointment) {
        return CONSULTATION_FEE + getTreatmentFee(appointment.getTreatmentType());
    }

    // ── Receipt Printer ───────────────────────────────────────────────────────

    /**
     * Prints a fully formatted, ANSI-coloured ASCII receipt to the console.
     *
     * @param appointment  The appointment whose receipt is to be printed.
     */
    public static void printReceipt(Appointment appointment) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String receiptDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss"));

        double treatmentFee = getTreatmentFee(appointment.getTreatmentType());
        double total        = CONSULTATION_FEE + treatmentFee;

        String topBot = "═".repeat(RECEIPT_WIDTH);
        String divider = "─".repeat(RECEIPT_WIDTH);

        System.out.println();

        // ── Header ────────────────────────────────────────────────────────────
        borderTop(topBot);
        blankRow();
        centreRow(" SUNRISE DENTAL CLINIC ", Colors.BRIGHT_YELLOW + Colors.BOLD);
        centreRow(CLINIC_ADDRESS, Colors.WHITE);
        centreRow("Tel: " + CLINIC_PHONE + "   |   " + CLINIC_EMAIL, Colors.WHITE);
        blankRow();
        dividerRow(divider);
        centreRow("** PATIENT RECEIPT **", Colors.BRIGHT_WHITE + Colors.BOLD);
        dividerRow(divider);

        // ── Appointment Details ───────────────────────────────────────────────
        blankRow();
        labelValueRow("Appointment No ", appointment.getAppointmentNumber());
        labelValueRow("Patient Name   ", appointment.getPatientName());
        labelValueRow("Address        ", appointment.getAddress());
        labelValueRow("Contact No     ", appointment.getContactNumber());
        labelValueRow("Dentist        ", appointment.getDentistName());
        labelValueRow("Treatment      ", appointment.getTreatmentType());
        labelValueRow("Appt. Date     ", appointment.getAppointmentDate());
        labelValueRow("Appt. Time     ", appointment.getAppointmentTime());
        labelValueRow("Receipt Date   ", receiptDate);
        blankRow();

        // ── Fee Breakdown ─────────────────────────────────────────────────────
        dividerRow(divider);
        feeRow("Consultation Fee", df.format(CONSULTATION_FEE), Colors.WHITE);
        feeRow("Treatment Fee  (" + appointment.getTreatmentType() + ")",
                df.format(treatmentFee), Colors.WHITE);
        dividerRow(divider);
        feeRow("TOTAL AMOUNT DUE",
                "LKR  " + df.format(total),
                Colors.BRIGHT_YELLOW + Colors.BOLD);
        dividerRow(divider);

        // ── Footer ────────────────────────────────────────────────────────────
        blankRow();
        centreRow("Thank you for choosing Sunrise Dental Clinic!", Colors.BRIGHT_CYAN);
        centreRow("Your smile is our priority.  |  Get well soon!", Colors.WHITE);
        blankRow();
        borderBottom(topBot);
        System.out.println(Colors.RESET);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    /** Pads a string to exactly RECEIPT_WIDTH using spaces (truncates if too long). */
    private static String pad(String s) {
        if (s == null) s = "";
        if (s.length() >= RECEIPT_WIDTH) return s.substring(0, RECEIPT_WIDTH);
        return String.format("%-" + RECEIPT_WIDTH + "s", s);
    }

    /** Centers a string within RECEIPT_WIDTH. */
    private static String centre(String s) {
        if (s == null) s = "";
        int totalPad = RECEIPT_WIDTH - s.length();
        if (totalPad <= 0) return pad(s);
        int leftPad  = totalPad / 2;
        int rightPad = totalPad - leftPad;
        return " ".repeat(leftPad) + s + " ".repeat(rightPad);
    }

    private static void borderTop(String rule) {
        System.out.println(Colors.BRIGHT_CYAN + "  ╔" + rule + "╗" + Colors.RESET);
    }

    private static void borderBottom(String rule) {
        System.out.println(Colors.BRIGHT_CYAN + "  ╚" + rule + "╝" + Colors.RESET);
    }

    private static void dividerRow(String div) {
        System.out.println(Colors.BRIGHT_CYAN + "  ╠" + div + "╣" + Colors.RESET);
    }

    private static void blankRow() {
        System.out.println(Colors.BRIGHT_CYAN + "  ║" + Colors.RESET
                + pad("") + Colors.BRIGHT_CYAN + "║" + Colors.RESET);
    }

    private static void centreRow(String text, String color) {
        System.out.println(Colors.BRIGHT_CYAN + "  ║" + color
                + centre(text) + Colors.RESET + Colors.BRIGHT_CYAN + "║" + Colors.RESET);
    }

    private static void labelValueRow(String label, String value) {
        // Format: "  label : value"
        String content = "  " + label + ": " + (value == null ? "" : value);
        System.out.println(Colors.BRIGHT_CYAN + "  ║" + Colors.WHITE
                + pad(content) + Colors.BRIGHT_CYAN + "║" + Colors.RESET);
    }

    private static void feeRow(String label, String amount, String color) {
        // Right-align the amount field at column RECEIPT_WIDTH - 2
        int amountWidth = 18;
        int labelWidth  = RECEIPT_WIDTH - amountWidth - 4; // 2 leading spaces + 2 trailing
        String formatted = String.format("  %-" + labelWidth + "s  %"
                + amountWidth + "s", label, amount);
        System.out.println(Colors.BRIGHT_CYAN + "  ║" + color
                + pad(formatted) + Colors.RESET + Colors.BRIGHT_CYAN + "║" + Colors.RESET);
    }
}
