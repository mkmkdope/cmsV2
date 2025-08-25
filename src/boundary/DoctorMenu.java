package boundary;

import control.DoctorManager;
import entity.Doctor;
import java.util.Scanner;

public class DoctorMenu {

    private DoctorManager doctorManager;
    private Scanner scanner;

    public DoctorMenu() {
        doctorManager = new DoctorManager();
        scanner = new Scanner(System.in);
        initializeSampleData();
    }



    private void initializeSampleData() {
        doctorManager.addDoctor("S001", "Dr. Smith", "Cardiology");
        doctorManager.addDoctor("S002", "Dr. Johnson", "Pediatrics");
        doctorManager.addDoctor("S003", "Dr. Williams", "Surgery");
        doctorManager.addDoctor("S004", "Dr. Brown", "Neurology");
        doctorManager.addDoctor("S005", "Dr. Davis", "Orthopedics");
        doctorManager.addDoctor("S006", "Dr. Miller", "Dermatology");
        doctorManager.addDoctor("S007", "Dr. Wilson", "Ophthalmology");
        doctorManager.addDoctor("S008", "Dr. Moore", "Surgery");
        doctorManager.addDoctor("S009", "Dr. Taylor", "Radiology");
        doctorManager.addDoctor("S010", "Dr. Anderson", "Neurology");
        doctorManager.addDoctor("S011", "Dr. Thomas", "Neurology");
        doctorManager.addDoctor("S012", "Dr. Jackson", "Surgery");
        doctorManager.addDoctor("S013", "Dr. White", "Surgery", "Tuesday-Saturday 10AM-06PM");
        doctorManager.addDoctor("S014", "Dr. Harris", "Nephrology", "Monday-Saturday 08AM-04PM");
        doctorManager.addDoctor("S015", "Dr. Martin", "Neurology", "Monday-Friday 09AM-09PM");
    }

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n=== Doctor Management System ===");
            System.out.println("1. Add Doctor");
            System.out.println("2. List all Doctor");
            System.out.println("3. Search Doctor by ID");
            System.out.println("4. Edit Doctor");
            System.out.println("5. Update Availability");
            System.out.println("6. Duty Report");
            System.out.println("7. Add Doctor to Duty Day");
            System.out.println("8. Remove Doctor from Duty Day");
            System.out.println("9. Reset Schedule"); //Regenerate
            System.out.println("10. Workload Report");
            System.out.println("11. Specialty Report");
            //System.out.println(". Remove Doctor"); //should not delete doctor profile just set as active or inactive
            //System.out.println(". Display Duty Schedule"); // use duty report instead                   
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 ->
                    addDoctor();
                case 2 ->
                    listAllDoctors();
                case 3 ->
                    searchDoctor();
                case 4 ->
                    editDoctor();
                case 5 ->
                    updateAvailability();
                case 6 ->
                    dutyReport();                
                case 7 ->
                    addDoctorToDutyDay();
                case 8 ->
                    removeDoctorFromDutyDay();
                 case 9 ->
                    regenerateSchedule();                   
                case 10 ->
                    workloadReport();
                case 11 ->
                    specialtyReport();
                //case  -> removeDoctor();
                //case  -> displayDutySchedule();
                case 0 ->
                    System.out.println("Loading...");
                default ->
                    System.out.println("Invalid choice!");
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
        System.out.print("Enter Doctor ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Specialization: ");
        String specialization = scanner.nextLine();
        System.out.print("Enter Duty Schedule (e.g., Monday-Friday 09AM-05PM): ");
        String dutySchedule = scanner.nextLine();

        boolean success = doctorManager.addDoctor(id, name, specialization, dutySchedule);

        if (success) {
            System.out.println("Doctor added successfully!");
        } else {
            System.out.println("Failed to add doctor. Doctor ID may already exist.");
        }
    }

    private void listAllDoctors() {
        System.out.println("\n=== All Doctors ===");
        Doctor[] doctors = doctorManager.getAllDoctors();
        if (doctors.length == 0) {
            System.out.println("No doctors found.");
        } else {
            for (Doctor doctor : doctors) {
                System.out.println(doctor);
            }
        }
    }

    private void searchDoctor() {
        System.out.print("Enter Doctor ID to search: ");
        String id = scanner.nextLine();
        Doctor doctor = doctorManager.searchDoctor(id);

        if (doctor != null) {
            System.out.println("Doctor found: " + doctor);
        } else {
            System.out.println("Doctor not found!");
        }
    }

    private void editDoctor() {
        System.out.print("Enter Doctor ID to edit: ");
        String id = scanner.nextLine();

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
            System.out.print("Enter new Schedule (current: " + doctor.getDutySchedule() + "): ");
            String dutySchedule = scanner.nextLine();
            if (dutySchedule.trim().isEmpty()) {
                dutySchedule = doctor.getDutySchedule(); // Keep current if empty
            }

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
        String id = scanner.nextLine();

        if (doctorManager.removeDoctor(id)) {
            System.out.println("Doctor removed successfully!");
        } else {
            System.out.println("Doctor not found!");
        }
    }
    
    //duty report visualise better
    private void displayDutySchedule() {
        System.out.println("\n=== Weekly Duty Schedule ===");
        doctorManager.displayDutySchedule();
    }

    private void updateAvailability() {
        System.out.print("Enter Doctor ID: ");
        String id = scanner.nextLine();
        System.out.print("Set available? (true/false): ");
        boolean available = scanner.nextBoolean();
        scanner.nextLine();

        if (doctorManager.updateAvailability(id, available)) {
            System.out.println("Availability updated successfully!");
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
                    System.out.println("  • " + doctor.getName() + " (" + doctor.getDoctorId() + ")");
                }
            }

            System.out.print("\nEnter Doctor ID to add: ");
            String doctorId = scanner.nextLine();

            if (doctorManager.addDoctorToDutyDay(dayIndex, doctorId)) {
                System.out.println("✓ Doctor successfully added to duty!");
            } else {
                System.out.println("✗ Failed to add doctor to duty.");
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
                System.out.println("  • " + doctor.getName() + " (" + doctor.getDoctorId() + ")");
            }

            System.out.print("\nEnter Doctor ID to remove: ");
            String doctorId = scanner.nextLine();

            if (doctorManager.removeDoctorFromDutyDay(dayIndex, doctorId)) {
                System.out.println("✓ Doctor successfully removed from duty!");
            } else {
                System.out.println("✗ Failed to remove doctor from duty.");
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
        System.out.println("\n=== Regenerating Schedule ===");
        doctorManager.regenerateSchedule();
        System.out.println("Doctor Duty Schedule is Ready!");
        doctorManager.displayDutySchedule();
    }

    //for testing
//    public static void main(String[] args) {
//        DoctorMenu menu = new DoctorMenu();
//        menu.displayMenu();
//    }
}
