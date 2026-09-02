import java.util.Scanner;

/**
 * BillingUI.java
 * Console screen for the "Calculate & Print Bill" functionality.
 * Looks up the appointment, displays the fee breakdown, then prints the receipt.
 * Sunrise Dental Clinic - Patient Management System
 */
public class BillingUI {

    // Prevent instantiation
    private BillingUI() {}

    /**
     * Interactive billing screen.
     * Prompts for an appointment number, shows a fee breakdown, and prints the receipt.
     *
     * @param scanner             Shared Scanner.
     * @param appointmentService  Service layer for appointment lookup.
     */
    public static void calculateAndPrintBill(Scanner scanner, AppointmentService appointmentService) {
        MenuUI.clearScreen();
        MenuUI.printSectionHeader("CALCULATE & PRINT BILL");

        // ── Lookup Appointment ────────────────────────────────────────────────
        String number = MenuUI.readField(scanner, "Enter Appointment Number", true);
        Appointment appointment = appointmentService.findByNumber(number);

        if (appointment == null) {
            MenuUI.printError("No appointment found with number: \"" + number + "\"");
            MenuUI.printInfo("Please register the appointment first or check the appointment number.");
            MenuUI.pressEnterToContinue(scanner);
            return;
        }

        // ── Fee Summary Preview ───────────────────────────────────────────────
        double treatmentFee = BillingService.getTreatmentFee(appointment.getTreatmentType());
        double total        = BillingService.calculateTotal(appointment);

        System.out.println(Colors.BRIGHT_CYAN
                + "\n  ┌──────────────── Fee Breakdown ─────────────────────┐");
        System.out.printf("  │  " + Colors.WHITE + "%-30s" + Colors.BRIGHT_CYAN
                + "  " + Colors.BRIGHT_YELLOW + "LKR %,10.2f" + Colors.BRIGHT_CYAN + "  │%n" + Colors.RESET,
                "Consultation Fee", BillingService.CONSULTATION_FEE);
        System.out.printf("  │  " + Colors.WHITE + "%-30s" + Colors.BRIGHT_CYAN
                + "  " + Colors.BRIGHT_YELLOW + "LKR %,10.2f" + Colors.BRIGHT_CYAN + "  │%n" + Colors.RESET,
                "Treatment: " + appointment.getTreatmentType(), treatmentFee);
        System.out.println(Colors.BRIGHT_CYAN
                + "  ├────────────────────────────────────────────────────┤");
        System.out.printf("  │  " + Colors.BRIGHT_WHITE + Colors.BOLD
                + "%-30s" + Colors.RESET + Colors.BRIGHT_CYAN
                + "  " + Colors.BRIGHT_GREEN + Colors.BOLD + "LKR %,10.2f" + Colors.BRIGHT_CYAN + "  │%n" + Colors.RESET,
                "TOTAL AMOUNT DUE", total);
        System.out.println(Colors.BRIGHT_CYAN
                + "  └────────────────────────────────────────────────────┘" + Colors.RESET);

        // ── Print Confirmation ────────────────────────────────────────────────
        System.out.print(Colors.BRIGHT_YELLOW + "\n  Print full receipt? (yes / no): " + Colors.WHITE);
        String confirm = scanner.nextLine().trim().toLowerCase();
        System.out.print(Colors.RESET);

        if (confirm.equals("yes") || confirm.equals("y")) {
            BillingService.printReceipt(appointment);
            MenuUI.printSuccess("Receipt printed successfully.");
        } else {
            MenuUI.printInfo("Receipt printing skipped.");
        }

        MenuUI.pressEnterToContinue(scanner);
    }
}
