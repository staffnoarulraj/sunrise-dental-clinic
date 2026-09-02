import java.util.Scanner;

/**
 * Main.java
 * Application entry point for the Sunrise Dental Clinic Patient Management System.
 *
 * Bootstrap sequence:
 *   1. Initialise the data directory.
 *   2. Display the application banner.
 *   3. Authenticate the user (up to 3 attempts).
 *   4. Enter the main menu loop until the user chooses to exit.
 *
 * Sunrise Dental Clinic - Patient Management System
 * Colombo, Sri Lanka  |  v1.0
 *
 * Default login:  username = admin   password = admin123
 */
public class Main {

    public static void main(String[] args) {

        // ── Bootstrap ─────────────────────────────────────────────────────────
        FileUtil.initDataDirectory();   // create data/ if it doesn't exist

        Scanner scanner = new Scanner(System.in);

        // ── Authentication ────────────────────────────────────────────────────
        AuthManager authManager = new AuthManager();
        boolean loggedIn = authManager.promptLogin(scanner);

        if (!loggedIn) {
            System.out.println(Colors.BRIGHT_RED
                    + "\n  System access denied. Exiting application." + Colors.RESET);
            scanner.close();
            return;
        }

        // ── Services ──────────────────────────────────────────────────────────
        AppointmentService appointmentService = new AppointmentService();

        // ── Main Menu Loop ────────────────────────────────────────────────────
        boolean running = true;
        while (running) {
            MenuUI.clearScreen();
            MenuUI.showBanner();
            MenuUI.showMainMenu(authManager.getCurrentUser());

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    // Register New Appointment
                    AppointmentUI.registerAppointment(scanner, appointmentService);
                    break;

                case "2":
                    // Display Appointment Details
                    AppointmentUI.displayAppointment(scanner, appointmentService);
                    break;

                case "3":
                    // View All Appointments
                    AppointmentUI.viewAllAppointments(scanner, appointmentService);
                    break;

                case "4":
                    // Calculate & Print Bill
                    BillingUI.calculateAndPrintBill(scanner, appointmentService);
                    break;

                case "5":
                    // Help Section
                    HelpUI.showHelp(scanner);
                    break;

                case "6":
                    // Exit System
                    running = !MenuUI.confirmExit(scanner);
                    break;

                default:
                    MenuUI.printError("Invalid option '" + choice
                            + "'. Please enter a number between 1 and 6.");
                    pause(800);
                    break;
            }
        }

        scanner.close();
        System.exit(0);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
