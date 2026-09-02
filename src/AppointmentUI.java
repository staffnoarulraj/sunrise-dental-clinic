import java.util.List;
import java.util.Scanner;

/**
 * AppointmentUI.java
 * Provides the console screens for:
 *   1. Registering a new appointment (with field-by-field validation).
 *   2. Displaying a single appointment by appointment number.
 *   3. Listing all appointments in a summary table.
 * Sunrise Dental Clinic - Patient Management System
 */
public class AppointmentUI {

    // Prevent instantiation
    private AppointmentUI() {}

    // ══════════════════════════════════════════════════════════════════════════
    //  1. REGISTER NEW APPOINTMENT
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Interactive registration form.
     * Validates every field and delegates persistence to AppointmentService.
     *
     * @param scanner             Shared Scanner.
     * @param appointmentService  Service layer for persistence and validation.
     */
    public static void registerAppointment(Scanner scanner, AppointmentService appointmentService) {
        MenuUI.clearScreen();
        MenuUI.printSectionHeader("REGISTER NEW APPOINTMENT");

        System.out.println(Colors.WHITE
                + "\n  Please fill in all the fields below. Fields marked (*) are required.\n");

        // ── Appointment Number ────────────────────────────────────────────────
        String appointmentNumber;
        while (true) {
            appointmentNumber = MenuUI.readField(scanner, "(*) Appointment Number (e.g. APT001)", true);
            if (!AppointmentService.isValidAppointmentNumber(appointmentNumber)) {
                MenuUI.printError("Appointment number must be 3–15 alphanumeric characters and start with a letter.");
                continue;
            }
            if (appointmentService.numberExists(appointmentNumber)) {
                MenuUI.printError("Appointment number '" + appointmentNumber + "' is already registered. Use a unique number.");
                continue;
            }
            break;
        }

        // ── Patient Name ──────────────────────────────────────────────────────
        String patientName;
        while (true) {
            patientName = MenuUI.readField(scanner, "(*) Patient Full Name", true);
            if (patientName.matches("[A-Za-z .'-]+")) break;
            MenuUI.printError("Patient name must contain only letters, spaces, dots, hyphens, or apostrophes.");
        }

        // ── Address ───────────────────────────────────────────────────────────
        String address = MenuUI.readField(scanner, "(*) Address", true);

        // ── Contact Number ────────────────────────────────────────────────────
        String contactNumber;
        while (true) {
            contactNumber = MenuUI.readField(scanner, "(*) Contact Number (10 digits, e.g. 0771234567)", true);
            if (AppointmentService.isValidContactNumber(contactNumber)) break;
            MenuUI.printError("Contact number must be exactly 10 digits and start with 0 (e.g. 0771234567).");
        }

        // ── Dentist Name ──────────────────────────────────────────────────────
        String dentistName;
        while (true) {
            dentistName = MenuUI.readField(scanner, "(*) Dentist Name (e.g. Dr. Perera)", true);
            if (!dentistName.isEmpty()) break;
        }

        // ── Treatment Type ────────────────────────────────────────────────────
        String treatmentType = selectTreatment(scanner);

        // ── Appointment Date ──────────────────────────────────────────────────
        String appointmentDate;
        while (true) {
            appointmentDate = MenuUI.readField(scanner, "(*) Appointment Date (DD/MM/YYYY)", true);
            if (AppointmentService.isValidDate(appointmentDate)) break;
            MenuUI.printError("Invalid date format. Please use DD/MM/YYYY (e.g. 15/09/2026).");
        }

        // ── Appointment Time ──────────────────────────────────────────────────
        String appointmentTime;
        while (true) {
            appointmentTime = MenuUI.readField(scanner, "(*) Appointment Time (HH:MM, 24-hour, e.g. 09:30)", true);
            if (AppointmentService.isValidTime(appointmentTime)) break;
            MenuUI.printError("Invalid time format. Please use HH:MM in 24-hour format (e.g. 14:00).");
        }

        // ── Confirmation Preview ──────────────────────────────────────────────
        System.out.println(Colors.CYAN
                + "\n  ┌─────────────────── Appointment Summary ────────────────┐"
                + Colors.RESET);
        printPreviewRow("Appointment No", appointmentNumber);
        printPreviewRow("Patient Name  ", patientName);
        printPreviewRow("Address       ", address);
        printPreviewRow("Contact No    ", contactNumber);
        printPreviewRow("Dentist       ", dentistName);
        printPreviewRow("Treatment     ", treatmentType);
        printPreviewRow("Date          ", appointmentDate);
        printPreviewRow("Time          ", appointmentTime);
        System.out.println(Colors.CYAN
                + "  └────────────────────────────────────────────────────────┘"
                + Colors.RESET);

        System.out.print(Colors.BRIGHT_YELLOW + "\n  Confirm and save this appointment? (yes / no): "
                + Colors.WHITE);
        String confirm = scanner.nextLine().trim().toLowerCase();
        System.out.print(Colors.RESET);

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            MenuUI.printWarning("Registration cancelled. No data was saved.");
            MenuUI.pressEnterToContinue(scanner);
            return;
        }

        // ── Save ──────────────────────────────────────────────────────────────
        Appointment appointment = new Appointment(
                appointmentNumber, patientName, address, contactNumber,
                dentistName, treatmentType, appointmentDate, appointmentTime);

        boolean saved = appointmentService.registerAppointment(appointment);
        if (saved) {
            MenuUI.printSuccess("Appointment registered successfully! [ " + appointmentNumber + " ]");
        } else {
            MenuUI.printError("Registration failed. Appointment number already exists.");
        }

        MenuUI.pressEnterToContinue(scanner);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  2. DISPLAY APPOINTMENT DETAILS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Prompts for an appointment number and displays the full record.
     * Also offers a secondary search by patient name.
     *
     * @param scanner             Shared Scanner.
     * @param appointmentService  Service layer for lookup.
     */
    public static void displayAppointment(Scanner scanner, AppointmentService appointmentService) {
        MenuUI.clearScreen();
        MenuUI.printSectionHeader("DISPLAY APPOINTMENT DETAILS");

        System.out.println(Colors.WHITE + "\n  Search Options:"
                + "\n    [1]  Search by Appointment Number"
                + "\n    [2]  Search by Patient Name"
                + "\n");
        System.out.print(Colors.BRIGHT_YELLOW + "  Select search option [1/2]: " + Colors.WHITE);
        String searchChoice = scanner.nextLine().trim();
        System.out.print(Colors.RESET);

        if (searchChoice.equals("2")) {
            // ── Search by Name ────────────────────────────────────────────────
            String nameQuery = MenuUI.readField(scanner, "Enter patient name (partial allowed)", true);
            List<Appointment> results = appointmentService.findByPatientName(nameQuery);
            if (results.isEmpty()) {
                MenuUI.printError("No appointments found for patient name containing: \"" + nameQuery + "\"");
            } else if (results.size() == 1) {
                printAppointmentCard(results.get(0));
            } else {
                System.out.println(Colors.BRIGHT_CYAN + "\n  " + results.size()
                        + " appointments found. Showing all matches:\n" + Colors.RESET);
                for (int i = 0; i < results.size(); i++) {
                    System.out.println(Colors.YELLOW + "  ── Match " + (i + 1) + " ──" + Colors.RESET);
                    printAppointmentCard(results.get(i));
                }
            }
        } else {
            // ── Search by Appointment Number (default) ────────────────────────
            String number = MenuUI.readField(scanner, "Enter Appointment Number", true);
            Appointment found = appointmentService.findByNumber(number);
            if (found == null) {
                MenuUI.printError("No appointment found with number: \"" + number + "\"");
            } else {
                printAppointmentCard(found);
            }
        }

        MenuUI.pressEnterToContinue(scanner);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  3. VIEW ALL APPOINTMENTS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Prints a summary table of all registered appointments.
     *
     * @param scanner             Shared Scanner.
     * @param appointmentService  Service layer for data retrieval.
     */
    public static void viewAllAppointments(Scanner scanner, AppointmentService appointmentService) {
        MenuUI.clearScreen();
        MenuUI.printSectionHeader("ALL REGISTERED APPOINTMENTS");

        List<Appointment> all = appointmentService.getAllAppointments();

        if (all.isEmpty()) {
            MenuUI.printInfo("No appointments have been registered yet.");
            MenuUI.pressEnterToContinue(scanner);
            return;
        }

        System.out.printf("%n" + Colors.BRIGHT_CYAN
                + "  ┌──────────────┬──────────────────────────┬──────────────────────┬────────────┬──────────┐%n"
                + "  │" + Colors.BRIGHT_WHITE + Colors.BOLD
                + " %-12s " + Colors.BRIGHT_CYAN
                + "│" + Colors.BRIGHT_WHITE + Colors.BOLD
                + " %-24s " + Colors.BRIGHT_CYAN
                + "│" + Colors.BRIGHT_WHITE + Colors.BOLD
                + " %-20s " + Colors.BRIGHT_CYAN
                + "│" + Colors.BRIGHT_WHITE + Colors.BOLD
                + " %-10s " + Colors.BRIGHT_CYAN
                + "│" + Colors.BRIGHT_WHITE + Colors.BOLD
                + " %-8s " + Colors.BRIGHT_CYAN
                + "│%n" + Colors.RESET,
                "Appt. No", "Patient Name", "Treatment", "Date", "Time");

        System.out.print(Colors.BRIGHT_CYAN
                + "  ├──────────────┼──────────────────────────┼──────────────────────┼────────────┼──────────┤%n"
                + Colors.RESET);

        for (Appointment a : all) {
            System.out.printf(Colors.BRIGHT_CYAN + "  │" + Colors.WHITE
                    + " %-12s " + Colors.BRIGHT_CYAN
                    + "│" + Colors.WHITE
                    + " %-24s " + Colors.BRIGHT_CYAN
                    + "│" + Colors.WHITE
                    + " %-20s " + Colors.BRIGHT_CYAN
                    + "│" + Colors.WHITE
                    + " %-10s " + Colors.BRIGHT_CYAN
                    + "│" + Colors.WHITE
                    + " %-8s " + Colors.BRIGHT_CYAN
                    + "│%n" + Colors.RESET,
                    truncate(a.getAppointmentNumber(), 12),
                    truncate(a.getPatientName(), 24),
                    truncate(a.getTreatmentType(), 20),
                    truncate(a.getAppointmentDate(), 10),
                    truncate(a.getAppointmentTime(), 8));
        }

        System.out.println(Colors.BRIGHT_CYAN
                + "  └──────────────┴──────────────────────────┴──────────────────────┴────────────┴──────────┘"
                + Colors.RESET);

        System.out.println(Colors.BRIGHT_WHITE
                + "\n  Total appointments: " + Colors.BRIGHT_YELLOW + all.size() + Colors.RESET);

        MenuUI.pressEnterToContinue(scanner);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Displays a detailed appointment card (full-detail view for one appointment).
     */
    private static void printAppointmentCard(Appointment a) {
        String bar = "─".repeat(52);
        System.out.println(Colors.BRIGHT_CYAN + "\n  ╔═══════════════════════════════════════════════════════╗");
        System.out.println(           "  ║" + Colors.BRIGHT_WHITE + Colors.BOLD
                + "             APPOINTMENT DETAILS                    " + Colors.RESET + Colors.BRIGHT_CYAN + "   ║");
        System.out.println(           "  ╠" + bar + "═══╣");
        cardRow("Appointment No  ", a.getAppointmentNumber());
        cardRow("Patient Name    ", a.getPatientName());
        cardRow("Address         ", a.getAddress());
        cardRow("Contact Number  ", a.getContactNumber());
        cardRow("Dentist         ", a.getDentistName());
        cardRow("Treatment Type  ", a.getTreatmentType());
        cardRow("Appointment Date", a.getAppointmentDate());
        cardRow("Appointment Time", a.getAppointmentTime());
        System.out.println("  ╚═══════════════════════════════════════════════════════╝" + Colors.RESET);
    }

    private static void cardRow(String label, String value) {
        System.out.printf(Colors.BRIGHT_CYAN + "  ║  " + Colors.BRIGHT_YELLOW
                + "%-18s" + Colors.WHITE + ":  %-33s" + Colors.BRIGHT_CYAN + "║%n" + Colors.RESET,
                label, value == null ? "" : value);
    }

    /** Renders a single row in the registration preview. */
    private static void printPreviewRow(String label, String value) {
        System.out.printf("  │  " + Colors.BRIGHT_YELLOW + "%-14s" + Colors.WHITE
                + ": " + Colors.BRIGHT_WHITE + "%-40s" + Colors.CYAN + " │%n" + Colors.RESET,
                label, value);
    }

    /**
     * Presents a numbered treatment-type selection menu.
     *
     * @param scanner  Shared Scanner.
     * @return         The selected treatment name.
     */
    private static String selectTreatment(Scanner scanner) {
        List<String> treatments = BillingService.getTreatmentList();
        System.out.println(Colors.BRIGHT_YELLOW + "\n    Available Treatments:" + Colors.RESET);
        for (int i = 0; i < treatments.size(); i++) {
            double fee = BillingService.getTreatmentFee(treatments.get(i));
            System.out.printf("      " + Colors.BRIGHT_GREEN + "%2d" + Colors.WHITE
                    + "  %-25s" + Colors.BRIGHT_CYAN + "  LKR %,10.2f%n" + Colors.RESET,
                    i + 1, treatments.get(i), fee);
        }
        System.out.println();

        while (true) {
            System.out.print(Colors.BRIGHT_YELLOW
                    + "    (*) Select Treatment [1-" + treatments.size() + "]: " + Colors.WHITE);
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= treatments.size()) {
                    System.out.print(Colors.RESET);
                    return treatments.get(choice - 1);
                }
            } catch (NumberFormatException ignored) {}
            MenuUI.printError("Please enter a number between 1 and " + treatments.size() + ".");
        }
    }

    /** Truncates a string to maxLen characters, appending "…" if truncated. */
    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 1) + "…";
    }
}
