package boundary;

import control.DoctorManager;
import entity.Doctor;
import java.util.Scanner;
import utility.ConfirmationMsg;

/**
 *
 * @author Yap Ming Kang
 */

public class DoctorMenu {

    private DoctorManager doctorManager;
    private Scanner scanner;

    public DoctorMenu() {
        doctorManager = new DoctorManager();
        scanner = new Scanner(System.in);
        initializeSampleData();
    }

    private void initializeSampleData() {
        doctorManager.addDoctor("S001", "Dr. Yap", "Dermatology");
        doctorManager.addDoctor("S002", "Dr. Ming", "Neurosurgery");
        doctorManager.addDoctor("S003", "Dr. Kang", "Neurosurgery");
        doctorManager.addDoctor("S004", "Dr. Tan", "Orthopedic Surgery");
        doctorManager.addDoctor("S005", "Dr. Yu", "Orthopedic Surgery");
        doctorManager.addDoctor("S006", "Dr. Hang", "Orthopedic Surgery");
        doctorManager.addDoctor("S007", "Dr. Hew", "ENT");
        doctorManager.addDoctor("S008", "Dr. Min", "ENT");
        doctorManager.addDoctor("S009", "Dr. Fei", "ENT");
        doctorManager.addDoctor("S010", "Dr. Angelo", "ENT", "Monday-Thursday 08AM-06PM");
        doctorManager.addDoctor("S011", "Dr. Wan", "General Surgery", "Tuesday-Friday 08AM-06PM");
        doctorManager.addDoctor("S012", "Dr. Kai Zhe", "General Surgery", "Wednesday-Saturday 08AM-06PM");
        doctorManager.addDoctor("S013", "Dr. Ng", "General Surgery", "Thursday-Saturday 08AM-07PM");
        doctorManager.addDoctor("S014", "Dr. Mei", "General Surgery", "Friday-Saturday 08AM-08PM");
        doctorManager.addDoctor("S015", "Dr. Yen", "General Surgery", "Monday-Friday 08AM-05PM");
    }

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n=================================");
            System.out.println("|   Doctor Management System   |");
            System.out.println("=================================");
            System.out.println("1. Add Doctor");
            System.out.println("2. List all Doctor");
            System.out.println("3. Search Doctor by ID");
            System.out.println("4. Edit Doctor");
            System.out.println("5. Update Availability");
            System.out.println("6. Duty Schedule Report");
            System.out.println("7. Add Doctor Duty");
            System.out.println("8. Remove Doctor Duty");
            System.out.println("9. Reset Schedule");
            System.out.println("10. Duty Schedule Report [New | Next Cycle]");
            System.out.println("11. Rotate Doctor List [New | Next Cycle]");
            System.out.println("12. Swap Doctor Positions [New | Next Cycle]");
            System.out.println("13. Workload Analysis Report");
            System.out.println("14. Specialty Availability Report");
            //System.out.println(". Remove Doctor"); //should not delete doctor profile just set as active or inactive
            //System.out.println(". Display Duty Schedule"); // not using                   
            System.out.println("0. Back to Main Menu");
            System.out.println("------------------------------");
            System.out.print("Enter choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addDoctor();
                    break;
                case 2:
                    listAllDoctors();
                    break;
                case 3:
                    searchDoctor();
                    break;
                case 4:
                    editDoctor();
                    break;
                case 5:
                    updateAvailability();
                    break;
                case 6:
                    dutyReport();
                    break;
                case 7:
                    addDoctorToDutyDay();
                    break;
                case 8:
                    removeDoctorFromDutyDay();
                    break;
                case 9:
                    regenerateSchedule();
                    break;
                case 10:
                    nextCycleReport();
                    break;
                case 11:
                    rotateDoctorList();
                    break;
                case 12:
                    swapDoctorPositions();
                    break;
                case 13:
                    workloadReport();
                    break;
                case 14:
                    specialtyReport();
                    break;
                //case : removeDoctor(); break;
                //case : displayDutySchedule(); break;
                case 0:
                    System.out.println("Loading...");
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        } while (choice != 0);
    }

    private void dutyReport() {
        doctorManager.generateDutyReport();
    }

    private void workloadReport() {
        doctorManager.generateWorkloadReport();
    }

    private void specialtyReport() {
        doctorManager.generateSpecialtyReport();
    }

    private void addDoctor() {
        String id;
        boolean validId = false;
        boolean duplicateId = false;
        System.out.println("\nAdding New Profile...");

        do {
            System.out.print("Enter Doctor ID (format D001): ");
            id = scanner.nextLine().trim().toUpperCase();

            // Check format
            if (id.matches("D\\d{3}")) {
                validId = true;

                // Check for duplicates
                Doctor existingDoctor = doctorManager.searchDoctor(id);
                if (existingDoctor != null) {
                    System.out.println("Error: Doctor ID " + id + " already exists!");
                    duplicateId = true;
                } else {
                    duplicateId = false;
                }
            } else {
                System.out.println("Error: ID must be in format D followed by 3 digits (e.g., D001)");
                validId = false;
            }
        } while (!validId || duplicateId);

        String name;
        do {
            System.out.print("Enter Name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error: Name cannot be empty!");
            }
        } while (name.isEmpty());

        String specialization;
        do {
            System.out.print("Enter Specialization: ");
            specialization = scanner.nextLine().trim();
            if (specialization.isEmpty()) {
                System.out.println("Error: Specialization cannot be empty!");
            }
        } while (specialization.isEmpty());

        /* Let Duty Schedule to be default */
        //System.out.print("Enter Duty Schedule (e.g., Monday-Friday 09AM-05PM): ");
        //String dutySchedule = scanner.nextLine();
        //boolean success = doctorManager.addDoctor(id, name, specialization, dutySchedule);
        
        boolean success = doctorManager.addDoctor(id, name, specialization);
        if (success) {
            System.out.println("Doctor added successfully.");
        } else {
            System.out.println("Failed to add doctor!");
        }
    }

    private void listAllDoctors() {
        System.out.println("\nGetting Doctors List...");
        System.out.println("----- All Doctors -----");
        Doctor[] doctors = doctorManager.getAllDoctors();
        if (doctors.length == 0) {
            System.out.println("No doctors found.");
        } else {
            for (Doctor doctor : doctors) {
                System.out.println(doctor);
            }
        }
        System.out.println("----- End of List -----");
    }

    private void searchDoctor() {
        System.out.println("\nSearching Doctor...");
        System.out.print("Enter Doctor ID to search: ");
        String id = scanner.nextLine().trim().toUpperCase();
        Doctor doctor = doctorManager.searchDoctor(id);

        if (doctor != null) {
            System.out.println("Doctor found: " + doctor);
        } else {
            System.out.println("Doctor not found!");
        }
    }

    private void editDoctor() {
        System.out.println("\nEditing profile...");
        System.out.print("Enter Doctor ID to edit: ");
        String id = scanner.nextLine().trim().toUpperCase();

        Doctor doctor = doctorManager.searchDoctor(id);
        if (doctor != null) {
            System.out.println("Current details: " + doctor);

            System.out.print("Enter new name (current: " + doctor.getName() + "): ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                name = doctor.getName(); // Keep current if empty
            }

            System.out.print("Enter new specialization (current: " + doctor.getSpecialization() + "): ");
            String specialization = scanner.nextLine();
            if (specialization.trim().isEmpty()) {
                specialization = doctor.getSpecialization(); // Keep current if empty
            }

            String dutySchedule = doctor.getDutySchedule();

            /* no edit keep default */
            // System.out.print("Enter new Schedule (current: " + doctor.getDutySchedule() + "): ");
            // String dutySchedule = scanner.nextLine();
            // if (dutySchedule.trim().isEmpty()) {
            //     dutySchedule = doctor.getDutySchedule(); // Keep current if empty
            // }
            
            if (doctorManager.updateDoctor(id, name, specialization, dutySchedule)) {
                System.out.println("Doctor updated successfully!");
                System.out.println("Updated details: " + doctorManager.searchDoctor(id));
            } else {
                System.out.println("Failed to update doctor.");
            }
        } else {
            System.out.println("Doctor not found!");
        }
    }

    //should not delete doctor profile just set as active or inactive 
    private void removeDoctor() {
        System.out.print("Enter Doctor ID to remove: ");
        String id = scanner.nextLine().trim().toUpperCase();

        if (doctorManager.removeDoctor(id)) {
            System.out.println("Doctor removed successfully!");
        } else {
            System.out.println("Doctor not found!");
        }
    }

    //not using
    private void displayDutySchedule() {
        System.out.println("\n=== Weekly Duty Schedule ===");
        doctorManager.displayDutySchedule();
    }

    private void updateAvailability() {
        System.out.println("\nUpdating Availability...");
        System.out.print("Enter Doctor ID: ");
        String id = scanner.nextLine().trim().toUpperCase();

        Doctor doctor = doctorManager.searchDoctor(id);
        if (doctor != null) {
            System.out.println("Current Doctor: " + doctor.getName());
            System.out.println("Current Availability: " + (doctor.isAvailable() ? "Available" : "On Leave"));

            boolean validInput = false;
            boolean available = false;

            do {
                System.out.print("Set availability (1 for Available, 0 for On Leave): ");
                String input = scanner.nextLine().trim();

                if (input.equals("1")) {
                    available = true;
                    validInput = true;
                } else if (input.equals("0")) {
                    available = false;
                    validInput = true;
                } else {
                    System.out.println("Error: Please enter 1 for Available or 0 for On Leave!");
                }
            } while (!validInput);

            ConfirmationMsg.displayConfirmationMessage();
            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (confirmation.equals("yes")) {
                if (doctorManager.updateAvailability(id, available)) {
                    String status = available ? "Available" : "On Leave";
                    System.out.println("Availability updated successfully! Doctor is now: " + status);
                } else {
                    System.out.println("Failed to update availability!");
                }
            } else {
                System.out.println("Availability update cancelled.");
            }
        } else {
            System.out.println("Doctor not found!");
        }
    }

    private void addDoctorToDutyDay() {
        try {
            System.out.println("\n=== Add Doctor to Duty Day ===");
            System.out.println("Days: 0=Monday, 1=Tuesday, 2=Wednesday, 3=Thursday, 4=Friday");
            System.out.print("Enter day index (0-4): ");
            int dayIndex = scanner.nextInt();
            scanner.nextLine();

            // Show current doctors on this day
            System.out.println("\nCurrent doctors on " + getDayName(dayIndex) + ":");
            Doctor[] currentDoctors = doctorManager.getDoctorsForDutyDay(dayIndex);
            if (currentDoctors.length == 0) {
                System.out.println("  No doctors scheduled");
            } else {
                for (Doctor doctor : currentDoctors) {
                    System.out.println("  - " + doctor.getName() + " (" + doctor.getDoctorId() + ")");
                }
            }

            System.out.print("\nEnter Doctor ID to add: ");
            String doctorId = scanner.nextLine().trim().toUpperCase();

            if (doctorManager.addDoctorToDutyDay(dayIndex, doctorId)) {
                System.out.println("Doctor successfully added to duty!");
            } else {
                System.out.println("Failed to add doctor to duty.");
            }
        } catch (Exception e) {
            System.out.println("Error: Invalid input. Please enter numbers for day index.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    private void removeDoctorFromDutyDay() {
        try {
            System.out.println("\n=== Remove Doctor from Duty Day ===");
            System.out.println("Days: 0=Monday, 1=Tuesday, 2=Wednesday, 3=Thursday, 4=Friday");
            System.out.print("Enter day index (0-4): ");
            int dayIndex = scanner.nextInt();
            scanner.nextLine();

            // Show current doctors on this day
            System.out.println("\nCurrent doctors on " + getDayName(dayIndex) + ":");
            Doctor[] currentDoctors = doctorManager.getDoctorsForDutyDay(dayIndex);
            if (currentDoctors.length == 0) {
                System.out.println("  No doctors scheduled");
                return;
            }

            for (Doctor doctor : currentDoctors) {
                System.out.println("  - " + doctor.getName() + " (" + doctor.getDoctorId() + ")");
            }

            System.out.print("\nEnter Doctor ID to remove: ");
            String doctorId = scanner.nextLine().trim().toUpperCase();

            ConfirmationMsg.displayConfirmationMessage();
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                if (doctorManager.removeDoctorFromDutyDay(dayIndex, doctorId)) {
                    System.out.println("Doctor successfully removed from duty!");
                } else {
                    System.out.println("Failed to remove doctor from duty.");
                }
            } else {
                System.out.println("Doctor removal from duty cancelled.");
            }
        } catch (Exception e) {
            System.out.println("Error: Invalid input. Please enter numbers for day index.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    private String getDayName(int dayIndex) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        if (dayIndex >= 0 && dayIndex < days.length) {
            return days[dayIndex];
        }
        return "Invalid Day";
    }

    private void regenerateSchedule() {
        System.out.println("\nRegenerating Schedule...");
        ConfirmationMsg.displayConfirmationMessage();
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            doctorManager.regenerateSchedule();
            System.out.println("Doctor Duty Schedule is Ready!");
        } else {
            System.out.println("Schedule regeneration cancelled.");
        }
    }

    private void rotateDoctorList() {
        try {
            System.out.println("\n=== Rotate Doctor List ===");

            // Show current order
            Doctor[] doctors = doctorManager.getAllDoctors();
            System.out.println("Current doctor order:");
            for (int i = 0; i < doctors.length; i++) {
                System.out.println((i + 1) + ". " + doctors[i].getName()
                        + " (" + doctors[i].getDoctorId() + ")");
            }

            System.out.print("\nEnter number of positions to rotate (+, -): ");
            int steps = scanner.nextInt();
            scanner.nextLine();

            doctorManager.rotateDoctors(steps);

        } catch (Exception e) {
            System.out.println("Error: Please enter a valid number.");
            scanner.nextLine();
        }
    }

    private void swapDoctorPositions() {
        System.out.println("\n=== Swap Doctor Positions ===");

        // Show current order
        Doctor[] doctors = doctorManager.getAllDoctors();
        System.out.println("Current doctor order:");
        for (int i = 0; i < doctors.length; i++) {
            System.out.println((i + 1) + ". " + doctors[i].getName()
                    + " (" + doctors[i].getDoctorId() + ")");
        }

        System.out.print("\nEnter first Doctor ID: ");
        String id1 = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter second Doctor ID: ");
        String id2 = scanner.nextLine().trim().toUpperCase();

        doctorManager.swapDoctors(id1, id2);
    }

    private void nextCycleReport() {
        System.out.println("\n=== Next Cycle Duty Schedule Report ===");
        System.out.println("Displaying projection based on current doctor order...");
        doctorManager.generateNextCycleReport();
    }

    //for testing
//    public static void main(String[] args) {
//        DoctorMenu menu = new DoctorMenu();
//        menu.displayMenu();
//    }
}
