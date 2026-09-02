import java.util.Scanner;

/**
 * MenuUI.java
 * Renders the ASCII banner, main menu, and shared UI utility methods (print helpers,
 * press-enter-to-continue, exit confirmation, section headers).
 * Sunrise Dental Clinic - Patient Management System
 */
public class MenuUI {

    // Prevent instantiation
    private MenuUI() {}

    // ── Banner ────────────────────────────────────────────────────────────────

    /**
     * Prints the full application banner to the console.
     * Should be called once after login succeeds.
     */
    public static void showBanner() {
        System.out.println(Colors.BRIGHT_CYAN);
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                              ║");
        System.out.println("  ║" + Colors.BRIGHT_YELLOW + Colors.BOLD
                + "     ██████  ███████ ███    ██ ████████  █████  ██"
                + Colors.RESET + Colors.BRIGHT_CYAN + "         ║");
        System.out.println("  ║" + Colors.BRIGHT_YELLOW + Colors.BOLD
                + "    ██      ██      ████   ██    ██    ██   ██ ██"
                + Colors.RESET + Colors.BRIGHT_CYAN + "         ║");
        System.out.println("  ║" + Colors.BRIGHT_YELLOW + Colors.BOLD
                + "    ██      █████   ██ ██  ██    ██    ███████ ██"
                + Colors.RESET + Colors.BRIGHT_CYAN + "         ║");
        System.out.println("  ║" + Colors.BRIGHT_YELLOW + Colors.BOLD
                + "    ██      ██      ██  ██ ██    ██    ██   ██ ██"
                + Colors.RESET + Colors.BRIGHT_CYAN + "         ║");
        System.out.println("  ║" + Colors.BRIGHT_YELLOW + Colors.BOLD
                + "     ██████  ███████ ██   ████    ██    ██   ██ ███████"
                + Colors.RESET + Colors.BRIGHT_CYAN + "    ║");
        System.out.println("  ║                                                              ║");
        System.out.println("  ║" + Colors.WHITE
                + "     ====================================================   "
                + Colors.BRIGHT_CYAN + "║");
        System.out.println("  ║" + Colors.BRIGHT_WHITE + Colors.BOLD
                + "       SUNRISE DENTAL CLINIC  —  Patient Management System "
                + Colors.RESET + Colors.BRIGHT_CYAN + " ║");
        System.out.println("  ║" + Colors.WHITE
                + "                  Colombo, Sri Lanka  |  v1.0           "
                + Colors.BRIGHT_CYAN + "   ║");
        System.out.println("  ║" + Colors.WHITE
                + "     ====================================================   "
                + Colors.BRIGHT_CYAN + "║");
        System.out.println("  ║                                                              ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝"
                + Colors.RESET);
        System.out.println();
    }

    // ── Main Menu ─────────────────────────────────────────────────────────────

    /**
     * Prints the main menu and prompts for user input.
     * The caller is responsible for reading the input.
     *
     * @param loggedInUser  Username of the currently logged-in staff member.
     */
    public static void showMainMenu(String loggedInUser) {
        System.out.println(Colors.CYAN
                + "\n  ┌─────────────────────────────────────────────────┐");
        System.out.printf("  │" + Colors.WHITE + "  Logged in as: " + Colors.BRIGHT_GREEN
                + "%-33s" + Colors.CYAN + "│%n", loggedInUser.toUpperCase());
        System.out.println("  ├─────────────────────────────────────────────────┤");
        System.out.println("  │" + Colors.BRIGHT_WHITE + Colors.BOLD
                + "                   MAIN  MENU                    "
                + Colors.RESET + Colors.CYAN + "│");
        System.out.println("  ╞═════════════════════════════════════════════════╡");
        option("1", "Register New Appointment");
        option("2", "Display Appointment Details");
        option("3", "View All Appointments");
        option("4", "Calculate & Print Bill");
        option("5", "Help Section");
        System.out.println("  │  " + Colors.BRIGHT_RED + "6" + Colors.WHITE
                + "   Exit System                                " + Colors.CYAN + "│");
        System.out.println("  └─────────────────────────────────────────────────┘"
                + Colors.RESET);
        System.out.print(Colors.BRIGHT_YELLOW + "\n  Enter your choice [1-6]: " + Colors.RESET);
    }

    private static void option(String num, String label) {
        System.out.printf("  │  " + Colors.BRIGHT_GREEN + "%s" + Colors.WHITE
                + "   %-44s" + Colors.CYAN + "│%n", num, label);
    }

    // ── Section Header ────────────────────────────────────────────────────────

    /**
     * Prints a styled section header box above a functional screen.
     *
     * @param title  The section title to display.
     */
    public static void printSectionHeader(String title) {
        String bar = "═".repeat(54);
        System.out.println(Colors.BRIGHT_CYAN + "\n  ╔" + bar + "╗");
        System.out.printf("  ║" + Colors.BRIGHT_YELLOW + Colors.BOLD
                + "  %-52s" + Colors.RESET + Colors.BRIGHT_CYAN + "║%n", title);
        System.out.println("  ╚" + bar + "╝" + Colors.RESET);
    }

    // ── Status Messages ───────────────────────────────────────────────────────

    public static void printSuccess(String message) {
        System.out.println(Colors.BRIGHT_GREEN + "\n  ✔  " + message + Colors.RESET);
    }

    public static void printError(String message) {
        System.out.println(Colors.BRIGHT_RED + "\n  ✘  " + message + Colors.RESET);
    }

    public static void printInfo(String message) {
        System.out.println(Colors.BRIGHT_CYAN + "\n  ℹ  " + message + Colors.RESET);
    }

    public static void printWarning(String message) {
        System.out.println(Colors.BRIGHT_YELLOW + "\n  ⚠  " + message + Colors.RESET);
    }

    // ── Input Helper ──────────────────────────────────────────────────────────

    /**
     * Prompts the user for a field value and returns the trimmed input.
     * Re-prompts if the input is empty (when required=true).
     *
     * @param scanner  Shared Scanner.
     * @param prompt   The prompt label (e.g. "Patient Name").
     * @param required Whether blank input is rejected.
     * @return         Trimmed, non-empty (if required) string.
     */
    public static String readField(Scanner scanner, String prompt, boolean required) {
        while (true) {
            System.out.print(Colors.BRIGHT_YELLOW + "    " + prompt + ": " + Colors.WHITE);
            String input = scanner.nextLine().trim();
            if (!required || !input.isEmpty()) {
                System.out.print(Colors.RESET);
                return input;
            }
            printError(prompt + " cannot be empty. Please try again.");
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /** Pauses execution until the user presses ENTER. */
    public static void pressEnterToContinue(Scanner scanner) {
        System.out.print(Colors.YELLOW + "\n  Press ENTER to return to main menu... " + Colors.RESET);
        scanner.nextLine();
    }

    /**
     * Asks the user to confirm exit.
     *
     * @param scanner  Shared Scanner.
     * @return         true if the user confirmed exit; false to stay.
     */
    public static boolean confirmExit(Scanner scanner) {
        System.out.print(Colors.BRIGHT_YELLOW + "\n  Are you sure you want to exit? (yes / no): "
                + Colors.WHITE);
        String answer = scanner.nextLine().trim().toLowerCase();
        System.out.print(Colors.RESET);

        if (answer.equals("yes") || answer.equals("y")) {
            System.out.println(Colors.BRIGHT_CYAN
                    + "\n  ╔══════════════════════════════════════════════════════╗"
                    + "\n  ║   Thank you for using Sunrise Dental Clinic System.  ║"
                    + "\n  ║   Session ended. Goodbye!                            ║"
                    + "\n  ╚══════════════════════════════════════════════════════╝"
                    + Colors.RESET);
            return true;
        }
        return false;
    }

    // ── Screen Clear ─────────────────────────────────────────────────────────

    /**
     * Attempts to clear the terminal screen.
     * Falls back silently if the environment doesn't support it.
     */
    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // ANSI escape: move cursor to top-left and clear screen
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception ignored) {
            // Non-fatal; just skip clearing
        }
    }
}
