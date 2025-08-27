package boundary;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import adt.ListInterface;
import entity.Pharmacy;
import entity.Treatment;
import entity.DispenseRecord;
import control.PharmacyManager;
import utility.PharmacyReport;

/**
 *
 * @author HEW MIN FEI
 * Pharmacy Boundary
 */
public class PharmacyMenu {

    String cancel ="(Type 'cancel' / 'c' to cancel operation)";
    String lineborder ="=".repeat(50);
    String lineborder2 ="-".repeat(50);
            
    private Scanner scanner = new Scanner(System.in);
    private PharmacyManager pharmacyManager;

    public PharmacyMenu() {
        this.pharmacyManager = new PharmacyManager();
        PharmacyReport.setPharmacyManager(pharmacyManager);
    }

    public void displayPharmacyMenu() {
        System.out.println("\n" + lineborder);
        System.out.println("PHARMACY MANAGEMENT");
        System.out.println(lineborder2);
        System.out.println("1 Add New Medicine");
        System.out.println("2 Update Medicine Info");
        System.out.println("3 Restock Medicine");
        System.out.println("4 Remove Medicine");
        System.out.println("5 View All Medicines");
        System.out.println("6 Search Medicines");
        System.out.println("7 Dispense Medicine");
        System.out.println("8 Pharmacy Reports");
        System.out.println("9 Back to Main Menu");
        System.out.println(lineborder);
        System.out.print("Enter Choice: ");
    }

    public int getPharmacyMenuChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= 9) {
                return choice;
            } else {
                System.out.println("Invalid choice! Please enter a number between 1-9.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number.");
            return -1;
        }
    }

    public void processPharmacyMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addNewMedicine();
                break;
            case 2:
                updateMedicineInfo();
                break;
            case 3:
                restockMedicine();
                break;
            case 4:
                removeMedicine();
                break;
            case 5:
                viewAllMedicines();
                break;
            case 6:
                searchMedicines();
                break;
            case 7:
                dispenseMedicineMenu();
                break;
            case 8:
                PharmacyReport.runReportMenu();
                break;
            case 9:
                System.out.println("Returning to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private void addNewMedicine() {
        System.out.println("\n" + lineborder);
        System.out.println("         ADD NEW MEDICINE");
        System.out.println(lineborder2);
        System.out.println(cancel);

        System.out.print("Enter Medicine Name: ");
        String medName = scanner.nextLine().trim();
        if (isCancelOperation(medName)) return;
        if (medName.isEmpty()) {
            System.out.println("Medicine name cannot be empty!");
            return;
        }

        System.out.print("Enter Quantity: ");
        String qtyInput = scanner.nextLine().trim();
        if (isCancelOperation(qtyInput)) return;
        int medQty;
        try {
            medQty = Integer.parseInt(qtyInput);
            if (medQty < 0) {
                System.out.println("Quantity cannot be negative!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity! Please enter a valid number.");
            return;
        }

        System.out.print("Enter Price (RM): ");
        String priceInput = scanner.nextLine().trim();
        if (isCancelOperation(priceInput)) return;
        double medPrice;
        try {
            medPrice = Double.parseDouble(priceInput);
            if (medPrice < 0) {
                System.out.println("Price cannot be negative!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid price! Please enter a valid number.");
            return;
        }

        System.out.print("Enter Expiry Date (dd/MM/yyyy): ");
        String expDateStr = scanner.nextLine().trim();
        if (isCancelOperation(expDateStr)) return;
        LocalDate expDate;
        try {
            expDate = LocalDate.parse(expDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            if (expDate.isBefore(LocalDate.now())) {
                System.out.println("Expiry date cann't be in the past!");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format! Please use dd/MM/yyyy format.");
            return;
        }

        if (pharmacyManager.addMedicine(medName, medQty, medPrice, expDate)) {
            System.out.println("\nMedicine added successfully!");
            ListInterface<Pharmacy> medicines = pharmacyManager.searchMedicinesByName(medName);
            if (!medicines.isEmpty()) {
                Pharmacy addedMedicine = medicines.getEntry(1);
                System.out.println("Medicine ID: " + addedMedicine.getMedID());
            }
        } else {
            System.out.println("\nFailed to add medicine!");
        }
    }

    private void updateMedicineInfo() {
        System.out.println("\n" + lineborder);
        System.out.println("         UPDATE MEDICINE INFO");
        System.out.println(lineborder2);
        System.out.println(cancel);

        System.out.print("Enter Medicine ID to update: ");
        String medID = scanner.nextLine().trim();
        if (isCancelOperation(medID)) return;
        
        Pharmacy existingMedicine = pharmacyManager.getMedicine(medID);
        if (existingMedicine == null) {
            System.out.println("Medicine not found!");
            return;
        }

        System.out.println("Current Medicine Information:");
        System.out.println(existingMedicine);

        System.out.println("\nEnter new information (press Enter to keep current value):");

        System.out.print("Enter new name [" + existingMedicine.getMedName() + "]: ");
        String medName = scanner.nextLine().trim();
        if (isCancelOperation(medName)) return;
        if (medName.isEmpty()) medName = existingMedicine.getMedName();

        System.out.print("Enter new quantity [" + existingMedicine.getMedQty() + "]: ");
        String qtyInput = scanner.nextLine().trim();
        if (isCancelOperation(qtyInput)) return;
        int medQty = existingMedicine.getMedQty();
        if (!qtyInput.isEmpty()) {
            try {
                medQty = Integer.parseInt(qtyInput);
                if (medQty < 0) {
                    System.out.println("Quantity cannot be negative! Keeping current value.");
                    medQty = existingMedicine.getMedQty();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity! Keeping current value.");
                medQty = existingMedicine.getMedQty();
            }
        }

        System.out.print("Enter new price [" + existingMedicine.getMedPrice() + "]: ");
        String priceInput = scanner.nextLine().trim();
        if (isCancelOperation(priceInput)) return;
        double medPrice = existingMedicine.getMedPrice();
        if (!priceInput.isEmpty()) {
            try {
                medPrice = Double.parseDouble(priceInput);
                if (medPrice < 0) {
                    System.out.println("Price cannot be negative! Keeping current value.");
                    medPrice = existingMedicine.getMedPrice();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid price! Keeping current value.");
                medPrice = existingMedicine.getMedPrice();
            }
        }

        System.out.print("Enter new expiry date [" + existingMedicine.getExpDate() + "] (dd/MM/yyyy): ");
        String dateInput = scanner.nextLine().trim();
        if (isCancelOperation(dateInput)) return;
        LocalDate expDate = existingMedicine.getExpDate();
        if (!dateInput.isEmpty()) {
            try {
                expDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (expDate.isBefore(LocalDate.now())) {
                    System.out.println("Expiry date cannot be in the past! Keeping current value.");
                    expDate = existingMedicine.getExpDate();
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Keeping current value.");
                expDate = existingMedicine.getExpDate();
            }
        }

        if (pharmacyManager.updateMedicineInfo(medID, medName, medQty, medPrice, expDate)) {
            System.out.println("\nMedicine information updated successfully!");
            System.out.println(pharmacyManager.getMedicine(medID));
        } else {
            System.out.println("\nFailed to update medicine information!");
        }
    }

    private void restockMedicine() {
        System.out.println("\n" + lineborder);
        System.out.println("         RESTOCK MEDICINE");
        System.out.println(lineborder2);
        System.out.println(cancel);

        System.out.print("Enter Medicine ID: ");
        String medID = scanner.nextLine().trim();
        if (isCancelOperation(medID)) return;

        Pharmacy medicine = pharmacyManager.getMedicine(medID);
        if (medicine == null) {
            System.out.println("Medicine not found!");
            return;
        }

        System.out.println("Current Medicine Information:");
        System.out.println(medicine);

        System.out.print("Enter additional quantity to add: ");
        String qtyInput = scanner.nextLine().trim();
        if (isCancelOperation(qtyInput)) return;
        int additionalQty;
        try {
            additionalQty = Integer.parseInt(qtyInput);
            if (additionalQty <= 0) {
                System.out.println("Additional quantity must be positive!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity! Please enter a valid number.");
            return;
        }

        if (pharmacyManager.restockMedicine(medID, additionalQty)) {
            System.out.println("\nMedicine restocked successfully!");
            System.out.println("Updated Medicine Information:");
            System.out.println(pharmacyManager.getMedicine(medID));
        } else {
            System.out.println("\nFailed to restock medicine!");
        }
    }

    private void removeMedicine() {
        System.out.println("\n" + lineborder);
        System.out.println("         REMOVE MEDICINE");
        System.out.println(lineborder2);
        System.out.println(cancel);

        System.out.print("Enter Medicine ID to remove: ");
        String medID = scanner.nextLine().trim();
        if (isCancelOperation(medID)) return;

        Pharmacy medicine = pharmacyManager.getMedicine(medID);
        if (medicine == null) {
            System.out.println("Medicine not found!");
            return;
        }

        System.out.println("Medicine to be removed:");
        System.out.println(medicine);

        System.out.print("Are you sure you want to remove this medicine? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (isCancelOperation(confirm)) return;

        if (confirm.equals("y") || confirm.equals("yes")) {
            Pharmacy removedMedicine = pharmacyManager.removeMedicine(medID);
            if (removedMedicine != null) {
                System.out.println("Medicine removed successfully!");
            } else {
                System.out.println("Failed to remove medicine!");
            }
        } else {
            System.out.println("Removal cancelled.");
        }
    }

    private void viewAllMedicines() {
        System.out.println("\n" + lineborder);
        System.out.println("         ALL MEDICINES");
        System.out.println(lineborder2);

        ListInterface<Pharmacy> medicines = pharmacyManager.getAllMedicines();
        if (medicines.isEmpty()) {
            System.out.println("No medicines found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("Sort Options:");
        System.out.println("1. By Expiry Date (Oldest First)");
        System.out.println("2. By Expiry Date (Latest First)");
        System.out.println("3. By Stock Quantity (Lowest First)");
        System.out.println("4. By Stock Quantity (Highest First)");
        System.out.println("5. No Sorting (Default)");
        System.out.print("Select sorting option (1-5): ");

        int sortChoice;
        try {
            sortChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Using default sorting.");
            sortChoice = 5;
        }

        ListInterface<Pharmacy> sortedMedicines = pharmacyManager.sortMedicines(medicines, sortChoice);

        System.out.println("Total Medicines: " + sortedMedicines.getNumberOfEntries());
        System.out.println(lineborder);
        
        for (int i = 1; i <= sortedMedicines.getNumberOfEntries(); i++) {
            Pharmacy medicine = sortedMedicines.getEntry(i);
            if (medicine != null) {
                System.out.println("\n" + "-".repeat(40));
                System.out.println("Medicine " + i + ":");
                System.out.println(medicine);
            }
        }
        
        System.out.println("\n" + lineborder);
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }


    private void searchMedicines() {
        System.out.println("\n" + lineborder);
        System.out.println("         SEARCH MEDICINES");
        System.out.println(lineborder2);
        System.out.println(cancel);

        System.out.println("1. Search by Medicine Name");
        System.out.println("2. Search by Medicine ID");
        System.out.print("Select search option (1-2): ");

        try {
            String choiceInput = scanner.nextLine().trim();
            if (isCancelOperation(choiceInput)) return;
            
            int choice = Integer.parseInt(choiceInput);
            switch (choice) {
                case 1:
                    searchByMedicineName();
                    break;
                case 2:
                    searchByMedicineId();
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
        }
    }

    private void searchByMedicineName() {
        System.out.print("Enter medicine name to search: ");
        String medName = scanner.nextLine().trim();
        if (isCancelOperation(medName)) return;
        if (medName.isEmpty()) {
            System.out.println("Medicine name cannot be empty!");
            return;
        }

        ListInterface<Pharmacy> results = pharmacyManager.searchMedicinesByName(medName);
        if (results.isEmpty()) {
            System.out.println("No medicines found with name containing: " + medName);
            return;
        }

        System.out.println("\nSearch Results (" + results.getNumberOfEntries() + " found):");
        System.out.println(lineborder);
        for (int i = 1; i <= results.getNumberOfEntries(); i++) {
            Pharmacy medicine = results.getEntry(i);
            if (medicine != null) {
                System.out.println("\n" + "-".repeat(40));
                System.out.println("Result " + i + ":");
                System.out.println(medicine);
            }
        }
        
        System.out.println("\n" + lineborder);
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void searchByMedicineId() {
        System.out.print("Enter medicine ID to search: ");
        String medID = scanner.nextLine().trim();
        if (isCancelOperation(medID)) return;
        if (medID.isEmpty()) {
            System.out.println("Medicine ID cannot be empty!");
            return;
        }

        ListInterface<Pharmacy> results = pharmacyManager.searchMedicinesById(medID);
        if (results.isEmpty()) {
            System.out.println("No medicines found with ID containing: " + medID);
            return;
        }

        System.out.println("\nSearch Results (" + results.getNumberOfEntries() + " found):");
        System.out.println(lineborder);
        for (int i = 1; i <= results.getNumberOfEntries(); i++) {
            Pharmacy medicine = results.getEntry(i);
            if (medicine != null) {
                System.out.println("\n" + "-".repeat(40));
                System.out.println("Result " + i + ":");
                System.out.println(medicine);
            }
        }
        
        System.out.println("\n" + lineborder);
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }


    // Integration with Treatment module //
    
    private void dispenseMedicineMenu() {
        int choice;
        do {
            System.out.println("\n" + lineborder);
            System.out.println("         DISPENSE MEDICINE");
            System.out.println(lineborder2);
            System.out.println("1. View Pending Treatments");
            System.out.println("2. Dispense Medicine");
            System.out.println("3. View Dispense History");
            System.out.println("4. Back to Pharmacy Menu");
            System.out.println(lineborder);
            System.out.print("Enter Choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                choice = 0;
            }

            switch (choice) {
                case 1:
                    viewPendingTreatments();
                    break;
                case 2:
                    dispenseMedicine();
                    break;
                case 3:
                    viewDispenseHistory();
                    break;
                case 4:
                    System.out.println("Returning to Pharmacy Menu...");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-4.");
            }
        } while (choice != 4);
    }

    private void viewPendingTreatments() {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("                    PENDING TREATMENTS");
        System.out.println("-".repeat(90));

        ListInterface<Treatment> pendingTreatments = pharmacyManager.getPendingTreatments();
        if (pendingTreatments.isEmpty()) {
            System.out.println("No pending treatments found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.printf("%-12s %-20s %-15s %-20s %-8s %-10s\n", 
                         "Treatment ID", "Patient Name", "Patient ID", "Prescribed Medicine", "Quantity", "Status");
        System.out.println("-".repeat(90));

        for (int i = 1; i <= pendingTreatments.getNumberOfEntries(); i++) {
            Treatment treatment = pendingTreatments.getEntry(i);
            if (treatment != null && treatment.getConsultation() != null && 
                treatment.getConsultation().getPatient() != null) {
                System.out.printf("%-12s %-20s %-15s %-20s %-8d %-10s\n",
                    treatment.getTreatmentID(),
                    treatment.getConsultation().getPatient().getName(),
                    treatment.getConsultation().getPatient().getPatientId(),
                    treatment.getPrescribed(),
                    treatment.getPrescribedQty(),
                    treatment.getTreatmentStatus()
                );
            }
        }

        System.out.println("=".repeat(90));
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void dispenseMedicine() {
        System.out.println("\n" + lineborder);
        System.out.println("         DISPENSE MEDICINE");
        System.out.println(lineborder2);

        ListInterface<Treatment> pendingTreatments = pharmacyManager.getPendingTreatments();
        if (pendingTreatments.isEmpty()) {
            System.out.println("No pending treatments found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("Available Pending Treatments:");
        System.out.println("-".repeat(50));
        for (int i = 1; i <= pendingTreatments.getNumberOfEntries(); i++) {
            Treatment treatment = pendingTreatments.getEntry(i);
            if (treatment != null && treatment.getConsultation() != null && 
                treatment.getConsultation().getPatient() != null) {
                System.out.printf("%d. %s - %s (%s) - %s x%d\n",
                    i,
                    treatment.getTreatmentID(),
                    treatment.getConsultation().getPatient().getName(),
                    treatment.getConsultation().getPatient().getPatientId(),
                    treatment.getPrescribed(),
                    treatment.getPrescribedQty()
                );
            }
        }

        System.out.print("\nEnter treatment number to dispense (or 0 to cancel): ");
        int treatmentChoice;
        try {
            treatmentChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
            return;
        }

        if (treatmentChoice == 0) {
            System.out.println("Dispense cancelled.");
            return;
        }

        if (treatmentChoice < 1 || treatmentChoice > pendingTreatments.getNumberOfEntries()) {
            System.out.println("Invalid treatment number!");
            return;
        }

        Treatment selectedTreatment = pendingTreatments.getEntry(treatmentChoice);
        if (selectedTreatment == null) {
            System.out.println("Treatment not found!");
            return;
        }

        System.out.println("\nPayment Methods:");
        System.out.println(lineborder2);
        System.out.println("1. Cash");
        System.out.println("2. Credit Card");
        System.out.println("3. E-Wallet");
        System.out.println("4. Bank Transfer");
        System.out.print("Select payment method (1-4): ");

        int paymentChoice;
        try {
            paymentChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid payment method!");
            return;
        }

        String paymentMethod;
        switch (paymentChoice) {
            case 1:
                paymentMethod = "Cash";
                break;
            case 2:
                paymentMethod = "Credit Card";
                break;
            case 3:
                paymentMethod = "E-Wallet";
                break;
            case 4:
                paymentMethod = "Bank Transfer";
                break;
            default:
                System.out.println("Invalid payment method!");
                return;
        }

        System.out.println("\nDispense Summary:");
        System.out.println(lineborder2);
        System.out.println("Treatment ID: " + selectedTreatment.getTreatmentID());
        System.out.println("Patient: " + selectedTreatment.getConsultation().getPatient().getName());
        System.out.println("Medicine: " + selectedTreatment.getPrescribed());
        System.out.println("Quantity: " + selectedTreatment.getPrescribedQty());
        System.out.println("Payment Method: " + paymentMethod);

        System.out.print("\nConfirm dispense? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Dispense cancelled.");
            return;
        }

        if (pharmacyManager.dispenseMedicine(selectedTreatment.getTreatmentID(), paymentMethod)) {
            System.out.println("\nMedicine dispensed successfully!");
            ListInterface<DispenseRecord> records = pharmacyManager.getAllDispenseRecords();
            if (!records.isEmpty()) {
                DispenseRecord lastRecord = records.getEntry(records.getNumberOfEntries());
                pharmacyManager.printReceipt(lastRecord);
            }
        } else {
            System.out.println("\nFailed to dispense medicine!");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewDispenseHistory() {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("                              DISPENSE HISTORY");
        System.out.println("-".repeat(120));

        ListInterface<DispenseRecord> records = pharmacyManager.getAllDispenseRecords();
        if (records.isEmpty()) {
            System.out.println("No dispense records found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.printf("%-12s %-20s %-15s %-25s %-8s %-12s %-15s %-20s\n",
                         "Dispense ID", "Patient Name", "Patient ID", "Medicine", "Quantity", "Total Price", "Payment Method", "Date");
        System.out.println("-".repeat(120));

        for (int i = 1; i <= records.getNumberOfEntries(); i++) {
            DispenseRecord record = records.getEntry(i);
            if (record != null) {
                System.out.printf("%-12s %-20s %-15s %-25s %-8d %-12s %-15s %-20s\n",
                    record.getDispenseID(),
                    record.getPatientName(),
                    record.getPatientID(),
                    record.getMedicineName(),
                    record.getQuantity(),
                    "RM " + String.format("%.2f", record.getTotalPrice()),
                    record.getPaymentMethod(),
                    record.getDispenseDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                );
            }
        }

        System.out.println("-".repeat(120));
        System.out.println("Total Records: " + records.getNumberOfEntries());
        System.out.println("Total Revenue: RM " + String.format("%.2f", pharmacyManager.getTotalRevenue()));
        System.out.println("=".repeat(120));
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private boolean isCancelOperation(String input) {
        if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("c")) {
            System.out.println("Operation cancelled.");
            return true;
        }
        return false;
    }

public void runPharmacyMenu() {
    int choice;
    do {
        displayPharmacyMenu();
        choice = getPharmacyMenuChoice();
        if (choice != -1) {
            processPharmacyMenuChoice(choice);
        }
        } while (choice != 9);
    System.out.println("Returning to Main Menu...");
}
}