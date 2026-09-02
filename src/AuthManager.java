import java.security.MessageDigest;
import java.util.*;

/**
 * AuthManager.java
 * Handles user authentication with SHA-256 password hashing.
 * Credentials are persisted in data/users.txt as  username:hashedPassword.
 * A default admin account is seeded on first run.
 * Sunrise Dental Clinic - Patient Management System
 */
public class AuthManager {

    private static final String USERS_FILE       = "users.txt";
    private static final String DEFAULT_USERNAME  = "admin";
    private static final String DEFAULT_PASSWORD  = "admin123";
    private static final int    MAX_LOGIN_ATTEMPTS = 3;

    private final Map<String, String> userStore = new LinkedHashMap<>();
    private String currentUser = null;

    // ── Constructor ───────────────────────────────────────────────────────────

    public AuthManager() {
        seedDefaultUser();
        loadUsers();
    }

    // ── Initialisation ────────────────────────────────────────────────────────

    /** Seeds the default admin account if no users file exists yet. */
    private void seedDefaultUser() {
        if (!FileUtil.fileExists(USERS_FILE)) {
            String hashed = hashPassword(DEFAULT_PASSWORD);
            FileUtil.appendLine(USERS_FILE, DEFAULT_USERNAME + ":" + hashed);
        }
    }

    /** Loads all user credentials from the data file into memory. */
    private void loadUsers() {
        userStore.clear();
        List<String> lines = FileUtil.readLines(USERS_FILE);
        for (String line : lines) {
            int idx = line.indexOf(':');
            if (idx > 0) {
                String username = line.substring(0, idx).trim();
                String hash     = line.substring(idx + 1).trim();
                userStore.put(username.toLowerCase(), hash);
            }
        }
    }

    // ── Authentication ────────────────────────────────────────────────────────

    /**
     * Interactively prompts for username and password with up to MAX_LOGIN_ATTEMPTS.
     *
     * @param scanner  The shared Scanner for console input.
     * @return         true if login succeeded, false if all attempts exhausted.
     */
    public boolean promptLogin(Scanner scanner) {
        MenuUI.clearScreen();
        MenuUI.showBanner();

        int attempts = 0;
        while (attempts < MAX_LOGIN_ATTEMPTS) {
            printLoginBox(attempts);

            System.out.print(Colors.BRIGHT_YELLOW + "    Username : " + Colors.WHITE);
            String username = scanner.nextLine().trim();

            System.out.print(Colors.BRIGHT_YELLOW + "    Password : " + Colors.WHITE);
            String password = scanner.nextLine().trim();

            System.out.println(Colors.RESET);

            if (authenticate(username, password)) {
                MenuUI.printSuccess("Login successful! Welcome, " + currentUser.toUpperCase() + ".");
                pause(900);
                return true;
            } else {
                attempts++;
                int remaining = MAX_LOGIN_ATTEMPTS - attempts;
                if (remaining > 0) {
                    MenuUI.printError("Invalid username or password. " + remaining + " attempt(s) remaining.");
                    pause(600);
                } else {
                    MenuUI.printError("Too many failed attempts. System access denied.");
                    pause(1000);
                }
            }
        }
        return false;
    }

    /**
     * Validates credentials against the in-memory user store.
     *
     * @param username  Plain-text username.
     * @param password  Plain-text password.
     * @return          true on match.
     */
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) return false;
        String stored = userStore.get(username.toLowerCase());
        if (stored == null) return false;
        boolean match = stored.equals(hashPassword(password));
        if (match) currentUser = username.toLowerCase();
        return match;
    }

    /** Returns the username of the currently logged-in user, or null. */
    public String getCurrentUser() { return currentUser; }

    // ── Password Hashing ──────────────────────────────────────────────────────

    /**
     * Hashes a plain-text password using SHA-256.
     *
     * @param password  Plain-text password.
     * @return          Lowercase hex-encoded SHA-256 hash.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed: " + e.getMessage(), e);
        }
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    private void printLoginBox(int attempt) {
        System.out.println(Colors.CYAN +
            "  ╔══════════════════════════════════════════╗");
        System.out.println(
            "  ║" + Colors.BRIGHT_WHITE + Colors.BOLD +
            "              STAFF LOGIN PORTAL            " +
            Colors.RESET + Colors.CYAN + "║");
        System.out.println(
            "  ╠══════════════════════════════════════════╣");
        if (attempt > 0) {
            System.out.printf(
            "  ║" + Colors.BRIGHT_RED +
            "   Attempt %d of %d. Please try again.      " +
            Colors.CYAN + "║%n", attempt + 1, MAX_LOGIN_ATTEMPTS);
            System.out.println(
            "  ╠══════════════════════════════════════════╣");
        }
        System.out.println(
            "  ║  " + Colors.BRIGHT_YELLOW + "Default credentials: admin / admin123" +
            Colors.CYAN + "    ║");
        System.out.println(
            "  ╠══════════════════════════════════════════╣" +
            Colors.RESET);
        System.out.println();
    }

    private static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
