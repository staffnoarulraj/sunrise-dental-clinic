import java.io.File;
import java.util.List;

/**
 * TestRunner.java
 * Automated Test Suite and Test-Driven Development (TDD) Harness
 * for Sunrise Dental Clinic Patient Management System.
 *
 * Covers:
 *  - Authentication & Cryptographic Hashing Tests
 *  - Appointment Validation & Business Rule Tests
 *  - Duplicate Detection & Search Tests
 *  - Billing & Total Calculation Tests
 *  - File Serialization & CSV Escaping Tests
 */
public class TestRunner {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println(Colors.BRIGHT_CYAN + "╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║       SUNRISE DENTAL CLINIC — AUTOMATED TEST SUITE (TDD)           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝" + Colors.RESET);
        System.out.println();

        FileUtil.initDataDirectory();

        // ── 1. Authentication & Security Tests ────────────────────────────────
        runSection("1. Authentication & Security Unit Tests", () -> {
            test("TC-AUTH-01: SHA-256 Hashing Consistency", () -> {
                String hash1 = AuthManager.hashPassword("admin123");
                String hash2 = AuthManager.hashPassword("admin123");
                String diffHash = AuthManager.hashPassword("password123");
                
                assertEqual(hash1, hash2, "Identical passwords must produce identical SHA-256 hashes");
                assertNotEqual(hash1, diffHash, "Different passwords must produce distinct hashes");
                assertEqual(hash1, "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", "Known SHA-256 test vector match");
            });

            test("TC-AUTH-02: Authentication with Correct Credentials", () -> {
                AuthManager auth = new AuthManager();
                assertTrue(auth.authenticate("admin", "admin123"), "Valid username/password must authenticate");
            });

            test("TC-AUTH-03: Authentication Case-Insensitive Username", () -> {
                AuthManager auth = new AuthManager();
                assertTrue(auth.authenticate("ADMIN", "admin123"), "Username should be case-insensitive");
            });

            test("TC-AUTH-04: Rejection of Invalid Password", () -> {
                AuthManager auth = new AuthManager();
                assertFalse(auth.authenticate("admin", "wrongpassword"), "Invalid password must be rejected");
            });

            test("TC-AUTH-05: Rejection of Non-Existent User", () -> {
                AuthManager auth = new AuthManager();
                assertFalse(auth.authenticate("ghost_user", "admin123"), "Non-existent user must be rejected");
            });
        });

        // ── 2. Data Validation & Regex Unit Tests ──────────────────────────────
        runSection("2. Data Validation & Regex Unit Tests", () -> {
            test("TC-VAL-01: Valid Appointment Number Format", () -> {
                assertTrue(AppointmentService.isValidAppointmentNumber("APT001"), "Standard format APT001 should be valid");
                assertTrue(AppointmentService.isValidAppointmentNumber("A12345"), "A12345 should be valid");
                assertFalse(AppointmentService.isValidAppointmentNumber("12345"), "Must not start with a digit");
                assertFalse(AppointmentService.isValidAppointmentNumber("A"), "Too short (<3 chars)");
                assertFalse(AppointmentService.isValidAppointmentNumber("A1234567890123456"), "Too long (>15 chars)");
                assertFalse(AppointmentService.isValidAppointmentNumber("APT-001"), "Special characters not allowed");
            });

            test("TC-VAL-02: Sri Lankan Contact Number Format", () -> {
                assertTrue(AppointmentService.isValidContactNumber("0771234567"), "Valid 10-digit mobile number");
                assertTrue(AppointmentService.isValidContactNumber("0112345678"), "Valid 10-digit landline number");
                assertFalse(AppointmentService.isValidContactNumber("771234567"), "Must start with 0 (9 digits invalid)");
                assertFalse(AppointmentService.isValidContactNumber("07712345678"), "Too long (11 digits invalid)");
                assertFalse(AppointmentService.isValidContactNumber("077123456A"), "Non-numeric characters invalid");
            });

            test("TC-VAL-03: Appointment Date Validation (DD/MM/YYYY)", () -> {
                assertTrue(AppointmentService.isValidDate("15/09/2026"), "Valid date format");
                assertTrue(AppointmentService.isValidDate("01/01/2026"), "Boundary day 01");
                assertTrue(AppointmentService.isValidDate("31/12/2026"), "Boundary day 31");
                assertFalse(AppointmentService.isValidDate("32/01/2026"), "Day out of range (32)");
                assertFalse(AppointmentService.isValidDate("15/13/2026"), "Month out of range (13)");
                assertFalse(AppointmentService.isValidDate("2026-09-15"), "Wrong delimiter / ISO format");
                assertFalse(AppointmentService.isValidDate("15/9/2026"), "Single digit month without leading zero");
            });

            test("TC-VAL-04: Appointment Time Validation (HH:MM 24-hour)", () -> {
                assertTrue(AppointmentService.isValidTime("09:30"), "Valid morning time");
                assertTrue(AppointmentService.isValidTime("14:45"), "Valid afternoon time");
                assertTrue(AppointmentService.isValidTime("00:00"), "Midnight boundary");
                assertTrue(AppointmentService.isValidTime("23:59"), "End of day boundary");
                assertFalse(AppointmentService.isValidTime("24:00"), "Hour out of range (24)");
                assertFalse(AppointmentService.isValidTime("12:60"), "Minute out of range (60)");
                assertFalse(AppointmentService.isValidTime("9:30"), "Single digit hour without leading zero");
            });
        });

        // ── 3. Appointment Service & Business Logic Tests ──────────────────────
        runSection("3. Appointment Service & Business Logic Tests", () -> {
            AppointmentService service = new AppointmentService();

            test("TC-SRV-01: Duplicate Appointment Registration Prevention", () -> {
                String uniqueId = "TDD" + System.currentTimeMillis() % 10000;
                Appointment a1 = new Appointment(uniqueId, "Test Patient", "Colombo", "0770000000",
                        "Dr. Silva", "Cleaning", "10/10/2026", "10:00");
                
                boolean firstReg = service.registerAppointment(a1);
                assertTrue(firstReg, "First registration must succeed");

                Appointment a2Duplicate = new Appointment(uniqueId, "Another Patient", "Kandy", "0771111111",
                        "Dr. Perera", "Filling", "11/10/2026", "11:00");
                boolean secondReg = service.registerAppointment(a2Duplicate);
                assertFalse(secondReg, "Duplicate appointment number must be rejected");
            });

            test("TC-SRV-02: Search Appointment by ID (Case-Insensitive)", () -> {
                String uniqueId = "SRCH" + (System.currentTimeMillis() % 10000);
                Appointment a = new Appointment(uniqueId, "Search Subject", "Galle", "0772222222",
                        "Dr. Fernando", "Root Canal", "12/10/2026", "14:00");
                service.registerAppointment(a);

                Appointment foundUpper = service.findByNumber(uniqueId.toUpperCase());
                Appointment foundLower = service.findByNumber(uniqueId.toLowerCase());

                assertTrue(foundUpper != null, "Lookup with uppercase ID should succeed");
                assertTrue(foundLower != null, "Lookup with lowercase ID should succeed");
                assertEqual(foundUpper.getPatientName(), "Search Subject", "Retrieved patient name must match");
            });

            test("TC-SRV-03: Search Appointment by Partial Patient Name", () -> {
                String uniqueId = "NAME" + (System.currentTimeMillis() % 10000);
                Appointment a = new Appointment(uniqueId, "Kasun Wickramasinghe", "Matara", "0773333333",
                        "Dr. Silva", "Extraction", "15/10/2026", "16:00");
                service.registerAppointment(a);

                List<Appointment> results = service.findByPatientName("Wickrama");
                assertTrue(!results.isEmpty(), "Partial name query should return results");
                assertTrue(results.stream().anyMatch(item -> item.getAppointmentNumber().equals(uniqueId)),
                        "Matched list must contain registered appointment");
            });
        });

        // ── 4. Billing & Financial Calculation Tests ───────────────────────────
        runSection("4. Billing & Cost Calculation Tests", () -> {
            test("TC-BILL-01: Standard Consultation Fee Constant", () -> {
                assertEqual(BillingService.CONSULTATION_FEE, 1500.00, "Base consultation fee must be exactly 1,500.00 LKR");
            });

            test("TC-BILL-02: Individual Treatment Fee Lookups", () -> {
                assertEqual(BillingService.getTreatmentFee("Cleaning"), 2500.00, "Cleaning fee");
                assertEqual(BillingService.getTreatmentFee("Filling"), 5000.00, "Filling fee");
                assertEqual(BillingService.getTreatmentFee("Root Canal"), 15000.00, "Root Canal fee");
                assertEqual(BillingService.getTreatmentFee("Extraction"), 3500.00, "Extraction fee");
                assertEqual(BillingService.getTreatmentFee("Teeth Whitening"), 8000.00, "Teeth Whitening fee");
                assertEqual(BillingService.getTreatmentFee("Unknown Treatment"), 0.00, "Unknown treatment should return 0.00");
            });

            test("TC-BILL-03: Total Bill Calculation (Consultation + Treatment)", () -> {
                Appointment a1 = new Appointment("B001", "Billing Patient 1", "Colombo", "0771234567",
                        "Dr. Silva", "Root Canal", "01/10/2026", "09:00");
                double total1 = BillingService.calculateTotal(a1);
                assertEqual(total1, 16500.00, "Root Canal Total (1,500 + 15,000) must equal 16,500.00 LKR");

                Appointment a2 = new Appointment("B002", "Billing Patient 2", "Colombo", "0771234567",
                        "Dr. Silva", "Cleaning", "01/10/2026", "10:00");
                double total2 = BillingService.calculateTotal(a2);
                assertEqual(total2, 4000.00, "Cleaning Total (1,500 + 2,500) must equal 4,000.00 LKR");
            });
        });

        // ── 5. Serialization & Persistence Unit Tests ─────────────────────────
        runSection("5. Serialization & Persistence Unit Tests", () -> {
            test("TC-SER-01: CSV Serialization & Deserialization Round-Trip", () -> {
                Appointment original = new Appointment("APT999", "Kamal Perera", "123 Galle Rd, Colombo",
                        "0771122334", "Dr. Silva", "Teeth Whitening", "20/10/2026", "11:30");
                
                String csv = original.toCSV();
                Appointment restored = Appointment.fromCSV(csv);

                assertTrue(restored != null, "Deserialization should not return null");
                assertEqual(restored.getAppointmentNumber(), original.getAppointmentNumber(), "Appointment number matches");
                assertEqual(restored.getPatientName(), original.getPatientName(), "Patient name matches");
                assertEqual(restored.getAddress(), original.getAddress(), "Address matches");
                assertEqual(restored.getContactNumber(), original.getContactNumber(), "Contact number matches");
                assertEqual(restored.getDentistName(), original.getDentistName(), "Dentist name matches");
                assertEqual(restored.getTreatmentType(), original.getTreatmentType(), "Treatment type matches");
                assertEqual(restored.getAppointmentDate(), original.getAppointmentDate(), "Date matches");
                assertEqual(restored.getAppointmentTime(), original.getAppointmentTime(), "Time matches");
            });

            test("TC-SER-02: Pipe Delimiter Escaping in Fields", () -> {
                Appointment withPipes = new Appointment("APT888", "Dr. John | Special", "Floor 2 | Room 4",
                        "0779988776", "Dr. Silva", "X-Ray", "22/10/2026", "15:00");
                
                String csv = withPipes.toCSV();
                Appointment restored = Appointment.fromCSV(csv);

                assertTrue(restored != null, "Deserialization with escaped pipes should succeed");
                assertEqual(restored.getPatientName(), "Dr. John | Special", "Patient name with pipe intact");
                assertEqual(restored.getAddress(), "Floor 2 | Room 4", "Address with pipe intact");
            });

            test("TC-SER-03: Corrupted Line Handling", () -> {
                Appointment nullAppt = Appointment.fromCSV("Corrupted|Data|Too|Few|Columns");
                assertEqual(nullAppt, null, "Malformed CSV lines must gracefully return null");
            });
        });

        // ── Test Summary Report ───────────────────────────────────────────────
        printSummary();
    }

    // ── Test Framework Helpers ────────────────────────────────────────────────

    private static void runSection(String sectionName, Runnable runnable) {
        System.out.println(Colors.BRIGHT_YELLOW + Colors.BOLD + "▶ " + sectionName + Colors.RESET);
        runnable.run();
        System.out.println();
    }

    private static void test(String testName, TestCase testCase) {
        totalTests++;
        try {
            testCase.execute();
            passedTests++;
            System.out.println(Colors.BRIGHT_GREEN + "  ✔ PASS: " + Colors.WHITE + testName + Colors.RESET);
        } catch (AssertionError | Exception e) {
            failedTests++;
            System.out.println(Colors.BRIGHT_RED + "  ✘ FAIL: " + Colors.WHITE + testName + Colors.RESET);
            System.out.println(Colors.RED + "         Cause: " + e.getMessage() + Colors.RESET);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError("Assertion Failed: " + message);
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) throw new AssertionError("Assertion Failed: Expected FALSE but got TRUE -> " + message);
    }

    private static void assertEqual(Object actual, Object expected, String message) {
        if (actual == null && expected == null) return;
        if (actual == null || !actual.equals(expected)) {
            throw new AssertionError("Assertion Failed: " + message + " [Expected: " + expected + ", Actual: " + actual + "]");
        }
    }

    private static void assertNotEqual(Object actual, Object unexpected, String message) {
        if (actual != null && actual.equals(unexpected)) {
            throw new AssertionError("Assertion Failed: " + message + " [Received unexpected value: " + actual + "]");
        }
    }

    private static void printSummary() {
        System.out.println(Colors.BRIGHT_CYAN + "════════════════════════════════════════════════════════════════════" + Colors.RESET);
        System.out.println(Colors.BRIGHT_WHITE + Colors.BOLD + "                       TEST EXECUTION SUMMARY                       " + Colors.RESET);
        System.out.println(Colors.BRIGHT_CYAN + "════════════════════════════════════════════════════════════════════" + Colors.RESET);
        System.out.printf("  Total Tests Executed : %s%d%s%n", Colors.BRIGHT_WHITE, totalTests, Colors.RESET);
        System.out.printf("  Passed Tests         : %s%d%s%n", Colors.BRIGHT_GREEN, passedTests, Colors.RESET);
        System.out.printf("  Failed Tests         : %s%d%s%n", failedTests > 0 ? Colors.BRIGHT_RED : Colors.BRIGHT_GREEN, failedTests, Colors.RESET);
        
        double passRate = ((double) passedTests / totalTests) * 100.0;
        System.out.printf("  Pass Rate            : %s%.1f%%%s%n", passRate == 100.0 ? Colors.BRIGHT_GREEN : Colors.BRIGHT_YELLOW, passRate, Colors.RESET);
        System.out.println(Colors.BRIGHT_CYAN + "════════════════════════════════════════════════════════════════════" + Colors.RESET);
        
        if (failedTests == 0) {
            System.out.println(Colors.BRIGHT_GREEN + "  ✔ ALL AUTOMATED TEST CASES PASSED SUCCESSFULLY!" + Colors.RESET);
        } else {
            System.out.println(Colors.BRIGHT_RED + "  ✘ SOME TESTS FAILED. PLEASE REVIEW CAUSES ABOVE." + Colors.RESET);
        }
        System.out.println();
    }

    @FunctionalInterface
    interface TestCase {
        void execute() throws Exception;
    }
}
