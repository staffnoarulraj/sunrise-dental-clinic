# 🦷 Sunrise Dental Clinic — Patient Management System

A menu-driven Java console application for managing patient appointments, treatment records, and billing at Sunrise Dental Clinic, Colombo.

## Features

| # | Function | Description |
|---|----------|-------------|
| 1 | **Login** | SHA-256 hashed passwords, 3-attempt lockout |
| 2 | **Register Appointment** | 8-field validated form, duplicate detection |
| 3 | **Display Appointment** | Search by appointment number or patient name |
| 3b | **View All Appointments** | Tabular summary of all records |
| 4 | **Calculate & Print Bill** | Treatment fee table + formatted ASCII receipt |
| 5 | **Help Section** | Step-by-step guide for new staff |
| 6 | **Exit System** | Safe shutdown with confirmation |

## Requirements

- Java 11 or higher (tested with Java 21)
- No external libraries — standard library only

## How to Run

```bash
# Clone the repository
git clone git@github.com:YourUsername/sunrise-dental-clinic.git
cd sunrise-dental-clinic

# Compile & run (one command)
bash compile_run.sh
```

Or manually:

```bash
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

**Default login:** `admin` / `admin123`

## Project Structure

```
SunriseDentalClinic/
├── compile_run.sh          # Build & run script
├── src/
│   ├── Main.java           # Entry point
│   ├── AuthManager.java    # SHA-256 authentication
│   ├── Appointment.java    # Data model
│   ├── AppointmentService.java  # CRUD + validation
│   ├── BillingService.java      # Fee table + receipt printer
│   ├── AppointmentUI.java       # Registration / display screens
│   ├── BillingUI.java           # Billing screen
│   ├── HelpUI.java              # Help guide
│   ├── MenuUI.java              # Banner + menu + UI helpers
│   ├── FileUtil.java            # File I/O utility
│   └── Colors.java             # ANSI color constants
└── data/                   # Auto-created at runtime
    ├── users.txt           # Hashed credentials
    └── appointments.txt    # Patient appointment records
```

## Treatment Fee Schedule

| Treatment | Fee (LKR) |
|-----------|-----------|
| Cleaning | 2,500 |
| Filling | 5,000 |
| Root Canal | 15,000 |
| Extraction | 3,500 |
| Teeth Whitening | 8,000 |
| Braces Consultation | 12,000 |
| X-Ray | 2,000 |
| Crown Fitting | 18,000 |
| Dentures | 25,000 |
| Scaling | 3,000 |
| **Consultation Fee** *(added to every bill)* | **1,500** |

## Data Storage

Appointments are stored in `data/appointments.txt` as pipe-delimited records:

```
APT001|Nimal Perera|No 12 Galle Road Colombo|0771234567|Dr. Silva|Root Canal|15/09/2026|09:30
```

---

*Built with Java — Sunrise Dental Clinic, Colombo, Sri Lanka*
