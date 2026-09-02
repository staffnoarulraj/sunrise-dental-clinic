/**
 * Appointment.java
 * Data model representing a single patient appointment.
 * Supports pipe-delimited serialization for text-file persistence.
 * Sunrise Dental Clinic - Patient Management System
 */
public class Appointment {

    // ── Fields ───────────────────────────────────────────────────────────────
    private String appointmentNumber;
    private String patientName;
    private String address;
    private String contactNumber;
    private String dentistName;
    private String treatmentType;
    private String appointmentDate;   // stored as entered (e.g. "01/09/2026")
    private String appointmentTime;   // stored as entered (e.g. "10:30 AM")

    // ── Constructors ─────────────────────────────────────────────────────────
    public Appointment() {}

    public Appointment(String appointmentNumber, String patientName, String address,
                       String contactNumber, String dentistName, String treatmentType,
                       String appointmentDate, String appointmentTime) {
        this.appointmentNumber = appointmentNumber;
        this.patientName       = patientName;
        this.address           = address;
        this.contactNumber     = contactNumber;
        this.dentistName       = dentistName;
        this.treatmentType     = treatmentType;
        this.appointmentDate   = appointmentDate;
        this.appointmentTime   = appointmentTime;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getAppointmentNumber() { return appointmentNumber; }
    public String getPatientName()       { return patientName;       }
    public String getAddress()           { return address;           }
    public String getContactNumber()     { return contactNumber;     }
    public String getDentistName()       { return dentistName;       }
    public String getTreatmentType()     { return treatmentType;     }
    public String getAppointmentDate()   { return appointmentDate;   }
    public String getAppointmentTime()   { return appointmentTime;   }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }
    public void setPatientName(String patientName)             { this.patientName       = patientName;       }
    public void setAddress(String address)                     { this.address           = address;           }
    public void setContactNumber(String contactNumber)         { this.contactNumber     = contactNumber;     }
    public void setDentistName(String dentistName)             { this.dentistName       = dentistName;       }
    public void setTreatmentType(String treatmentType)         { this.treatmentType     = treatmentType;     }
    public void setAppointmentDate(String appointmentDate)     { this.appointmentDate   = appointmentDate;   }
    public void setAppointmentTime(String appointmentTime)     { this.appointmentTime   = appointmentTime;   }

    // ── Serialization ─────────────────────────────────────────────────────────

    /**
     * Serializes this appointment to a pipe-delimited CSV line.
     * Pipes within field values are escaped as "\|".
     */
    public String toCSV() {
        return String.join("|",
            escape(appointmentNumber),
            escape(patientName),
            escape(address),
            escape(contactNumber),
            escape(dentistName),
            escape(treatmentType),
            escape(appointmentDate),
            escape(appointmentTime)
        );
    }

    /**
     * Deserializes an Appointment from a pipe-delimited CSV line.
     * Returns null if the line is malformed.
     */
    public static Appointment fromCSV(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        // Split on unescaped pipes only
        String[] parts = line.split("(?<!\\\\)\\|", -1);
        if (parts.length != 8) return null;

        Appointment a = new Appointment();
        a.setAppointmentNumber(unescape(parts[0]));
        a.setPatientName      (unescape(parts[1]));
        a.setAddress          (unescape(parts[2]));
        a.setContactNumber    (unescape(parts[3]));
        a.setDentistName      (unescape(parts[4]));
        a.setTreatmentType    (unescape(parts[5]));
        a.setAppointmentDate  (unescape(parts[6]));
        a.setAppointmentTime  (unescape(parts[7]));
        return a;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static String escape(String s) {
        return (s == null) ? "" : s.replace("|", "\\|");
    }

    private static String unescape(String s) {
        return (s == null) ? "" : s.replace("\\|", "|");
    }

    @Override
    public String toString() {
        return String.format("Appointment[%s | %s | %s | %s]",
            appointmentNumber, patientName, treatmentType, appointmentDate);
    }
}
