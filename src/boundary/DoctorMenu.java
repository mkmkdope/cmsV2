package boundary;

import control.DoctorManager;
import dao.DoctorDAO;
import entity.Doctor;
import java.util.Scanner;

public class DoctorMenu {
    private DoctorManager manager;
    private Scanner sc = new Scanner(System.in);

    public DoctorMenu(DoctorManager manager) {
        this.manager = manager;
    }

    public void start() {
        int choice;
        do {
            System.out.println(Messages.DOCTOR_MENU_HEADER);
            System.out.println(Messages.DOCTOR_MENU_OPTION);
            System.out.print(Messages.DOCTOR_MENU_PROMPT);

            while (!sc.hasNextInt()) {
                System.out.println(Messages.INVALID_NUM);
                sc.nextLine();
            }
            choice = sc.nextInt();
            sc.nextLine();  // clear buffer

            switch (choice) {
                case 1:
                    registerDoctor();
                    break;
                case 2:
                    manager.displayAllDoctors();
                    break;
                case 3:
                    searchDoctor();
                    break;
                case 4:
                    updateDoctor();
                    break;
                case 5:
                    deleteDoctor();
                    break;
                case 6:
                    manager.displayAvailableDoctors();
                    break;
                case 7:
                    searchBySpecialization();
                    break;
                case 8:
                    sortDoctorsMenu();
                    break;
                case 9:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.printf(Messages.INVALID_CHOICE, 1, 9);
            }
        } while (choice != 9);
    }

    private void registerDoctor() {
        System.out.print("Enter Doctor Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            System.out.println("Invalid email format.");
            return;
        }

        System.out.print("Enter Specialization: ");
        String specialization = sc.nextLine().trim();
        if (specialization.isEmpty()) {
            System.out.println("Specialization cannot be empty.");
            return;
        }

        System.out.print("Enter Duty Schedule (e.g., Monday-Friday 9AM-5PM): ");
        String dutySchedule = sc.nextLine().trim();
        if (dutySchedule.isEmpty()) {
            System.out.println("Duty schedule cannot be empty.");
            return;
        }

        System.out.print("Is Available? (true/false): ");
        String availableStr = sc.nextLine().trim();
        boolean isAvailable;
        if (availableStr.equalsIgnoreCase("true")) {
            isAvailable = true;
        } else if (availableStr.equalsIgnoreCase("false")) {
            isAvailable = false;
        } else {
            System.out.println("Please enter true or false.");
            return;
        }

        System.out.print("Enter Join Date (dd/MM/yyyy): ");
        String joinDate = sc.nextLine().trim();
        if (joinDate.isEmpty()) {
            System.out.println("Join date cannot be empty.");
            return;
        }

        System.out.print("Enter Consultation Fee (RM): ");
        double consultationFee;
        try {
            consultationFee = Double.parseDouble(sc.nextLine().trim());
            if (consultationFee < 0) {
                System.out.println("Consultation fee cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid consultation fee.");
            return;
        }

        manager.registerDoctor(name, email, specialization, dutySchedule, isAvailable, joinDate, consultationFee);
    }

    private void searchDoctor() {
        System.out.print("Enter Doctor ID or Email: ");
        String keyword = sc.nextLine().trim();
        manager.searchDoctor(keyword);
    }

    private void updateDoctor() {
        System.out.print("Enter Doctor ID to update: ");
        String id = sc.nextLine().trim();

        Doctor existing = manager.getDoctorById(id);
        if (existing == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.println("Current doctor information:");
        System.out.println(existing);

        System.out.print("Enter new name [" + existing.getName() + "]: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = existing.getName();

        System.out.print("Enter new email [" + existing.getEmail() + "]: ");
        String email = sc.nextLine().trim();
        if (email.isEmpty()) email = existing.getEmail();

        System.out.print("Enter new specialization [" + existing.getSpecialization() + "]: ");
        String specialization = sc.nextLine().trim();
        if (specialization.isEmpty()) specialization = existing.getSpecialization();

        System.out.print("Enter new duty schedule [" + existing.getDutySchedule() + "]: ");
        String dutySchedule = sc.nextLine().trim();
        if (dutySchedule.isEmpty()) dutySchedule = existing.getDutySchedule();

        System.out.print("Is Available? [" + existing.isAvailable() + "] (true/false): ");
        String availableStr = sc.nextLine().trim();
        boolean isAvailable = existing.isAvailable();
        if (!availableStr.isEmpty()) {
            if (availableStr.equalsIgnoreCase("true")) {
                isAvailable = true;
            } else if (availableStr.equalsIgnoreCase("false")) {
                isAvailable = false;
            } else {
                System.out.println("Invalid input. Keeping current value.");
            }
        }

        System.out.print("Enter new join date [" + existing.getJoinDate() + "]: ");
        String joinDate = sc.nextLine().trim();
        if (joinDate.isEmpty()) joinDate = existing.getJoinDate();

        System.out.print("Enter new consultation fee [" + existing.getConsultationFee() + "]: ");
        String feeStr = sc.nextLine().trim();
        double consultationFee = existing.getConsultationFee();
        if (!feeStr.isEmpty()) {
            try {
                consultationFee = Double.parseDouble(feeStr);
                if (consultationFee < 0) {
                    System.out.println("Consultation fee cannot be negative. Keeping current value.");
                    consultationFee = existing.getConsultationFee();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid consultation fee. Keeping current value.");
                consultationFee = existing.getConsultationFee();
            }
        }

        System.out.print("Are you sure you want to update this doctor? (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            manager.updateDoctor(id, name, email, specialization, dutySchedule, isAvailable, joinDate, consultationFee);
        } else {
            System.out.println("Update cancelled.");
        }
    }

    private void deleteDoctor() {
        System.out.print("Enter Doctor ID to delete: ");
        String id = sc.nextLine().trim();

        Doctor existing = manager.getDoctorById(id);
        if (existing == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.println("Doctor to be deleted:");
        System.out.println(existing);

        System.out.print("Are you sure you want to delete this doctor? (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            manager.deleteDoctor(id);
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private void searchBySpecialization() {
        System.out.print("Enter specialization to search: ");
        String specialization = sc.nextLine().trim();
        if (specialization.isEmpty()) {
            System.out.println("Specialization cannot be empty.");
            return;
        }
        manager.displayDoctorsBySpecialization(specialization);
    }

    private void sortDoctorsMenu() {
        System.out.println("\n" + Messages.FORMAT);
        System.out.println(" SORT DOCTORS");
        System.out.println(Messages.FORMAT);
        System.out.println("1. Sort by Name");
        System.out.println("2. Sort by Specialization");
        System.out.println("3. Sort by Consultation Fee");
        System.out.println("4. Back to Doctor Menu");
        System.out.print("Enter choice: ");

        while (!sc.hasNextInt()) {
            System.out.println(Messages.INVALID_INPUT);
            sc.nextLine();
        }

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                manager.sortDoctorsByName();
                break;
            case 2:
                manager.sortDoctorsBySpecialization();
                break;
            case 3:
                manager.sortDoctorsByConsultationFee();
                break;
            case 4:
                System.out.println("Returning to Doctor Menu...");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
}