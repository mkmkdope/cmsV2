/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import control.ConsultationManager;
import entity.Consultation;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Ng Mei Yen
 */
public class ConsultationMenu {

    private ConsultationManager consultationManager;
    Scanner sc = new Scanner(System.in);
//
//    //boundary - let user enter input
//    //control handle logic only

    public ConsultationMenu() {

    }

    public ConsultationMenu(ConsultationManager consultationManager) {
        this.consultationManager = consultationManager;
    }

    public int consultationMenu() {
        int choice = -1;
        boolean back;
        do {
            System.out.println("Consultation Management");
            System.out.println("========================");
            System.out.println("1. Walk-in consultation");
            System.out.println("2. View Today Consultation");
            System.out.println("3. Update Consultation Information");
            System.out.println("4. Search Consultation Record");
            System.out.println("5. Patient Visitation Report");
            System.out.println("6. Consultation Summary Report");
            System.out.println("7. complete consultation(used by doctor) to complete the consultation");
            System.out.println("8. Call next patient from waiting queue and create consultation");
            System.out.println("0. Exit");
            System.out.println("+------------------------");

            back = false;
            try {
                System.out.print("Enter your choice : ");
                choice = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException ex) {
                System.out.println(" ");
                System.out.println("Invalid input! Please enter a valid input.");
                back = true;
                sc.nextLine();
            }

        } while (back || (choice < 0 || choice > 8)); // allow 0..8
        return choice;
    }

    public String inputPatientIC() {
        System.out.print("Enter IC Number of patient: ");
        return sc.nextLine().trim();
    }

    public Boolean isExit() {
        System.out.print("Enter 0 to return to main menu: ");
        if (sc.nextLine().trim().equals("0")) {
            return true;
        }
        return false;
    }

    public String inputDoctorID() {
        System.out.print("\nEnter Doctor ID (enter 0 to cancel this process): ");
        return sc.nextLine().trim();
    }

    public LocalDate selectDate() {
        LocalDate selectedDate = null;
        boolean validDate = false;

        do {
            System.out.println("Date: Today or other day?");
            System.out.println("1. Today");
            System.out.println("2. Other day");
            System.out.print("Enter your selection (1 or 2): ");

            String opt = sc.nextLine().trim();
            if (opt.equals("1")) {
                selectedDate = LocalDate.now();
                validDate = true;

            } else if (opt.equals("2")) {
                System.out.println("Please enter the date in DD-MM-YYYY format:");
                String dateInput = sc.nextLine().trim();
                try {
                    selectedDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    validDate = true;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use DD-MM-YYYY. Please try again.");
                }
            } else {
                System.out.println("Invalid choice. Please try again.");
            }

        } while (!validDate);
        return selectedDate;
    }

    public String inputConsultationTime() {
        System.out.print("\nEnter consultation time (HH:mm) (enter 0 to return to choose another doctor): ");
        return sc.nextLine().trim();
    }

    public String inputOtherTime() {
        System.out.print("Would you like to choose another doctor? (yes/no): ");
        return sc.nextLine().trim();
    }

    public String inputConsultationReason() {
        String reason;
        while (true) {
            System.out.print("Enter reason for consultation (for queue list enter (-) is regardless unknown reason): ");
            reason = sc.nextLine().trim();
            if (reason.isEmpty()) {
                System.out.println("Reason cannot be empty. Please try again.");
            } else {
                break;
            }
        }
        return reason;
    }

    public LocalDate inputDate() {
        LocalDate selectedDate = null;
        boolean validDate = false;

        do {
            System.out.println("Date: Today or other day?");
            System.out.println("1. Today");
            System.out.println("2. Other day");
            System.out.print("Enter your selection (1 or 2): ");

            String opt = sc.nextLine().trim();
            if (opt.equals("1")) {
                selectedDate = LocalDate.now();
                validDate = true;
            } else if (opt.equals("2")) {
                System.out.println("Please enter the date in DD-MM-YYYY format:");
                String dateInput = sc.nextLine().trim();
                try {
                    selectedDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    validDate = true;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use DD-MM-YYYY. Please try again.");
                }
            } else {
                System.out.println("Invalid choice. Please try again.");
            }

        } while (!validDate);
        return selectedDate;
    }

    public boolean askChooseAnotherDoctor() {
        System.out.print("Would you like to choose another doctor? (yes/no): ");
        String response = sc.nextLine().trim();
        return response.equalsIgnoreCase("yes");
    }

    public int inputSearchOption() {
        int opt = -1;

        System.out.println("");
        System.out.println("=== Search Consultation ===");
        System.out.println("Search consultation based on?");
        System.out.println("1. Patient IC number");
        System.out.println("2. Date/Time");
        System.out.println("3. Status");
        System.out.println("4. View all consultation record");
        System.out.println("0. Exit");
        System.out.print("Please select 1 option (0-4): ");

        try {
            opt = sc.nextInt();
            sc.nextLine();
            if (!(opt >= 0 && opt <= 4)) {
                System.out.println("Invalid option. Please enter a number between 0 and 4.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number between 0 and 4.");
            sc.nextLine();
        }

        return opt;
    }

    public String inputUpdateConsultationId() {
        System.out.print("Enter Consultation ID to update: ");
        return sc.nextLine().trim();
    }

    public void printConsultationInfo(Consultation c) {
        System.out.println("\n=== Current Consultation Information ===");
        System.out.println("Consultation ID: " + c.getConsultationID());
        System.out.println("Doctor: " + c.getDoctor().getName());
        System.out.println("Patient: " + c.getPatient().getName());
        System.out.println("Date & Time: " + c.getDateTime());
        System.out.println("Status: " + c.getStatus());
        System.out.println("=====================================\n");
    }

    public String inputUpdateSelection() {
        System.out.println("What would you like to update?");
        System.out.println("1. Date & Time");
        System.out.println("2. Status");
        return sc.nextLine().trim();
    }

    public String continueUpdateSelection() {
        System.out.println("Would you like to update something else? (yes/no): ");
        return sc.nextLine().trim();
    }

    public String inputStatusSelection(Consultation c) {
        System.out.println("Current status: " + c.getStatus());
        System.out.println("Update status to?");
        System.out.println("1. Scheduled");
        System.out.println("2. Cancelled");
        System.out.println("3. No-show");
        System.out.print("Enter new status choice (1-3): ");

        return sc.nextLine().trim();
    }

    public String inputNewDate() {
        System.out.print("Enter new consultation date (dd-MM-yyyy) or '0' to cancel: ");
        return sc.nextLine().trim();
    }

    public String inputNewTime() {
        System.out.print("\nEnter new consultation time (HH:mm) or '0' to cancel: ");
        return sc.nextLine().trim();
    }

    public void printSearchHeader() {
        System.out.println("Search consultation based on?");
        System.out.println("1. Patient IC number");
        System.out.println("2. Date/Time");
        System.out.println("3. Status");
        System.out.println("4. View all consultation record");
        System.out.println("0. Exit");
        System.out.print("Please select 1 option (1-4): ");
    }

    public int inputSelection() {
        int selection = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                selection = sc.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a valid number: ");
                sc.nextLine();
            }
        }

        return selection;
    }
    
    public String inputYesNo(){
        return sc.nextLine();
    }

    public String inputStatusChoice() {
        System.out.println("Select a status to search:");
        System.out.println("1. Scheduled");
        System.out.println("2. Completed");
        System.out.println("3. Cancelled");
        System.out.println("4. No-show");
        System.out.print("Enter your choice (1-4): ");
        return sc.nextLine().trim();
    }

    public int inputMonth() {
        int month;
        do {
            try {
                System.out.print("Enter month (1-12): ");
                month = sc.nextInt();
                sc.nextLine();

                if (month >= 1 && month <= 12) {
                    return month;
                } else {
                    System.out.println("Invalid month. Please enter a number between 1 and 12.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number for month.");
                sc.nextLine();
            }
        } while (true);
    }

    public int inputYear() {
        int year;
        do {
            try {
                System.out.print("Enter year (e.g., 2025): ");
                year = sc.nextInt();
                sc.nextLine();

                if (year >= 2000 && year <= 2025) {
                    return year;
                } else {
                    System.out.println("Invalid year. Please enter a year between 2000 and 2025.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number for year.");
                sc.nextLine();
            }
        } while (true);
    }

    public void consultationStatusReportHeader(String formattedDate) {
        System.out.println("=".repeat(58));
        System.out.printf("%s %30s %s %13s\n", "=", "Daily Visitation Report", formattedDate, "=");
        System.out.println("=".repeat(58));
        System.out.printf("%-18s %-25s %-10s\n", "Status", "Number of Consultations", "Percentage");
        System.out.println("----------------------------------------------------------");
    }

    public void visitationReportHeader(String formattedDate) {
        System.out.println("=".repeat(108));
        System.out.printf("%s %52s %s %42s\n", "=", "Daily Visitation Report", formattedDate, "=");
        System.out.println("=".repeat(108));
        System.out.printf(" %-5s  %-18s  %-25s  %-16s  %-10s %-10s  %-3s  %-5s \n",
                "ID", "Patient", "Doctor", "Date/Time", "Reason", "Status", "F/U", "PrevID");
        System.out.println("-".repeat(108));
    }

    public void visitationReportFooter(String reportDate, ListInterface<Consultation> searchVisitation) {
        System.out.println("-".repeat(108));
        System.out.printf("Total Visitations for %s: %d\n", reportDate, searchVisitation.getNumberOfEntries());
        System.out.println("=".repeat(108));
    }

    public void printRecordHeader() {
        System.out.println("");
        System.out.println("=".repeat(108));
        System.out.printf("%s %58s %47s\n", "=", "Consultation Record List", "=");
        System.out.println("=".repeat(108));
        System.out.printf(" %-5s  %-18s  %-25s  %-16s  %-10s %-10s  %-3s  %-5s \n",
                "ID", "Patient", "Doctor", "Date & Time", "Reason", "Status", "F/U", "Prev");
        System.out.println("=".repeat(108));
    }

    public void printSuccessfulSchedule(String consultationId, String patientName, String doctorName, String dateTime) {
        System.out.println("\nWalk-in consultation scheduled successfully:");
        System.out.println("Consultation ID: " + consultationId);
        System.out.println("Patient: " + patientName);
        System.out.println("Doctor: " + doctorName);
        System.out.println("Date and Time: " + dateTime);
    }
    
    
}