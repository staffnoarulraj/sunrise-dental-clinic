import java.util.Scanner;

/**
 * HelpUI.java
 * Displays a step-by-step user guide for new clinic staff.
 * Covers all major functions of the Sunrise Dental Clinic system.
 * Sunrise Dental Clinic - Patient Management System
 */
public class HelpUI {

    // Prevent instantiation
    private HelpUI() {}

    /**
     * Renders the full Help section to the console.
     *
     * @param scanner  Shared Scanner (used for "press enter" navigation).
     */
    public static void showHelp(Scanner scanner) {
        MenuUI.clearScreen();
        MenuUI.printSectionHeader("HELP SECTION  —  User Guide for New Staff");

        System.out.println(Colors.WHITE);

        // ── Introduction ──────────────────────────────────────────────────────
        helpHeading("WELCOME TO SUNRISE DENTAL CLINIC MANAGEMENT SYSTEM");
        System.out.println(Colors.WHITE
                + "  This system helps clinic staff manage patient appointments,\n"
                + "  view records, and generate bills efficiently.\n"
                + "  Below is a step-by-step guide for each function.\n");

        // ── Login ─────────────────────────────────────────────────────────────
        helpStep("1", "HOW TO LOG IN");
        helpBullet("When the system starts, a login screen will appear.");
        helpBullet("Enter your Username and press ENTER.");
        helpBullet("Enter your Password and press ENTER.");
        helpBullet("Default credentials:  Username = admin   Password = admin123");
        helpBullet("You have 3 attempts before the system locks access.");
        helpBullet("Contact your system administrator if you are locked out.");
        System.out.println();

        // ── Register Appointment ──────────────────────────────────────────────
        helpStep("2", "HOW TO REGISTER A NEW APPOINTMENT");
        helpBullet("Select option [1] from the Main Menu.");
        helpBullet("Fill in each field when prompted:");
        helpSubBullet("Appointment Number : Unique ID starting with a letter (e.g. APT001).");
        helpSubBullet("Patient Name       : Full name using letters only.");
        helpSubBullet("Address            : Patient's home address.");
        helpSubBullet("Contact Number     : 10-digit Sri Lankan mobile number (e.g. 0771234567).");
        helpSubBullet("Dentist Name       : The assigned dentist (e.g. Dr. Perera).");
        helpSubBullet("Treatment Type     : Select from the numbered list provided.");
        helpSubBullet("Appointment Date   : In DD/MM/YYYY format (e.g. 15/09/2026).");
        helpSubBullet("Appointment Time   : In HH:MM 24-hour format (e.g. 09:30 or 14:00).");
        helpBullet("A summary preview is shown before saving — confirm with 'yes' to save.");
        helpBullet("The system rejects duplicate appointment numbers automatically.");
        System.out.println();

        // ── Display Appointment ───────────────────────────────────────────────
        helpStep("3", "HOW TO DISPLAY APPOINTMENT DETAILS");
        helpBullet("Select option [2] from the Main Menu.");
        helpBullet("Choose to search by:");
        helpSubBullet("[1]  Appointment Number — enter the exact number (e.g. APT001).");
        helpSubBullet("[2]  Patient Name — enter a partial or full name.");
        helpBullet("The full appointment record will be displayed if found.");
        helpBullet("If multiple patients match a name search, all are shown.");
        System.out.println();

        // ── View All Appointments ─────────────────────────────────────────────
        helpStep("4", "HOW TO VIEW ALL APPOINTMENTS");
        helpBullet("Select option [3] from the Main Menu.");
        helpBullet("A summary table of all appointments is displayed.");
        helpBullet("Columns shown: Appointment No, Patient Name, Treatment, Date, Time.");
        helpBullet("Total number of appointments is shown at the bottom.");
        System.out.println();

        // ── Calculate Bill ────────────────────────────────────────────────────
        helpStep("5", "HOW TO CALCULATE AND PRINT A BILL");
        helpBullet("Select option [4] from the Main Menu.");
        helpBullet("Enter the Appointment Number of the patient to bill.");
        helpBullet("A fee breakdown is shown: Consultation Fee + Treatment Fee.");
        helpBullet("Confirm 'yes' to print the full formatted receipt.");
        helpBullet("Treatment fee schedule:");
        helpSubBullet("Cleaning             :  LKR  2,500");
        helpSubBullet("Filling              :  LKR  5,000");
        helpSubBullet("Root Canal           :  LKR 15,000");
        helpSubBullet("Extraction           :  LKR  3,500");
        helpSubBullet("Teeth Whitening      :  LKR  8,000");
        helpSubBullet("Braces Consultation  :  LKR 12,000");
        helpSubBullet("X-Ray                :  LKR  2,000");
        helpSubBullet("Crown Fitting        :  LKR 18,000");
        helpSubBullet("Dentures             :  LKR 25,000");
        helpSubBullet("Scaling              :  LKR  3,000");
        helpSubBullet("Consultation Fee (always added) : LKR 1,500");
        System.out.println();

        // ── Exit ──────────────────────────────────────────────────────────────
        helpStep("6", "HOW TO EXIT THE SYSTEM");
        helpBullet("Select option [6] from the Main Menu.");
        helpBullet("Confirm 'yes' when prompted to safely close the application.");
        helpBullet("All data is automatically saved to file — no manual save needed.");
        System.out.println();

        // ── Tips ──────────────────────────────────────────────────────────────
        helpHeading("GENERAL TIPS FOR STAFF");
        helpBullet("Always double-check the appointment number before registering.");
        helpBullet("Use a consistent naming format for dentists (e.g. Dr. Firstname).");
        helpBullet("Appointment data is stored in the 'data/appointments.txt' file.");
        helpBullet("Do not manually edit the data files to avoid corruption.");
        helpBullet("For technical support, contact your system administrator.");
        System.out.println();

        // ── Contact ───────────────────────────────────────────────────────────
        helpHeading("TECHNICAL SUPPORT");
        System.out.println(Colors.WHITE
                + "  Clinic:        Sunrise Dental Clinic\n"
                + "  Address:       No. 45, Galle Road, Colombo 03\n"
                + "  Telephone:     +94 11 234 5678\n"
                + "  Email:         info@sunrisedental.lk\n"
                + "  Support Hours: Monday – Friday, 8:00 AM – 5:00 PM\n");

        MenuUI.pressEnterToContinue(scanner);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private static void helpHeading(String text) {
        System.out.println(Colors.BRIGHT_CYAN + Colors.BOLD + "  ▌ " + text + Colors.RESET);
        System.out.println(Colors.BRIGHT_CYAN + "  " + "─".repeat(60) + Colors.RESET);
    }

    private static void helpStep(String number, String title) {
        System.out.println(Colors.BRIGHT_YELLOW + Colors.BOLD
                + "  Step " + number + ":  " + title + Colors.RESET);
    }

    private static void helpBullet(String text) {
        System.out.println(Colors.WHITE + "     •  " + text + Colors.RESET);
    }

    private static void helpSubBullet(String text) {
        System.out.println(Colors.WHITE + "           ›  " + text + Colors.RESET);
    }
}
