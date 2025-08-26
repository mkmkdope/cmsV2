package boundary;

import control.TreatmentManager;
import entity.Consultation;
import entity.Treatment;
import entity.Pharmacy;
import entity.Patient;
import entity.Doctor;
import adt.ListInterface;
import java.time.LocalDateTime;
import java.util.Scanner;
import control.PharmacyManager;
import entity.Pharmacy;

public class TreatmentMenu {
    private Scanner sc = new Scanner(System.in);
    private TreatmentManager manager = new TreatmentManager();

    public void runTreatmentMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println(" TREATMENT MANAGEMENT MENU");
            System.out.println("=".repeat(60));
            System.out.println("1. Add New Diagnosis/Treatment");
            System.out.println("2. View All Treatment Records");
            System.out.println("3. Search & Filter Treatments");
            System.out.println("4. Edit Treatment");
            System.out.println("5. Delete Treatment");
            System.out.println("6. Generate Patient Treatment History");
            System.out.println("7. Generate Diagnosis Report");
            System.out.println("8. Sort Treatments");
            System.out.println("9. Daily Treatment Report");
            System.out.println("10. Update Status Only");
            System.out.println("11. Back to Main Menu");
            System.out.println("-".repeat(60));
            System.out.print("Enter choice: ");
            
            String input = sc.nextLine();
            int choice;
            
            try{
                choice = Integer.parseInt(input);
            }catch(NumberFormatException e){
                System.out.println("Invalid input! Please enter a number (1-11)");
                continue;
            }

            switch (choice) {
                case 1: 
                    addNewTreatment();
                    break;
                case 2:
                    manager.displayAllTreatments(); // Use new beautiful display
                    break;
                case 3: 
                    searchMenu();
                    break;
                case 4:
                    updateTreatment();
                    break;
                case 5:
                    deleteTreatment();
                    break;
                case 6:
                    generateHistory();
                    break;
                case 7:
                    manager.displayDiagnosisReport(); // Use new beautiful display
                    break;
                case 8:
                    sortTreatments();
                    break;
                case 9:
                    generateDailyReport();
                    break;
                case 10:
                    updateStatusOnly();
                    break;
                case 11:
                    System.out.println("Returning to Main Menu...");
                    return;
                default:
                    System.out.println("Invalid choice!");
                    
            }
        }
    }

    private void addNewTreatment() {
        // how available consultations
        System.out.println("\n=== Select Consultation for Treatment ===");
        
        // get consultations from TreatmentDAO to ensure data consistency
        // it prevents ID mismatch between treatments and consultations
        ListInterface<Treatment> treatments = manager.getAllTreatments();
        ListInterface<Consultation> consultations = new adt.CircularDoublyLinkedList<>();
        
        // extract unique consultations from existing treatments
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getConsultation() != null) {
                // Check if this consultation whether exist
                boolean exists = false;
                for (int j = 1; j <= consultations.getNumberOfEntries(); j++) {
                    if (consultations.getEntry(j).getConsultationID().equals(t.getConsultation().getConsultationID())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    consultations.add(t.getConsultation());
                }
            }
        }
        
        if (consultations.isEmpty()) {
            System.out.println("No consultations found in Treatment module.");
            System.out.println("Please create a treatment first to establish consultation data.");
            return;
        }
        
        // display consultations (newest first)
        System.out.println("Available consultations (newest first):");
        System.out.println("-".repeat(60));
        
        for (int i = consultations.getNumberOfEntries(); i >= 1; i--) {
            Consultation c = consultations.getEntry(i);
            System.out.println("ID: " + c.getConsultationID());
            System.out.println("Patient: " + (c.getPatient() != null ? c.getPatient().getName() : "N/A"));
            System.out.println("Doctor: " + (c.getDoctor() != null ? c.getDoctor().getName() : "N/A"));
            System.out.println("Date: " + c.getDateTime());
            System.out.println("Reason: " + c.getReason());
            System.out.println("-".repeat(40));
        }
        
        // get consultation ID with validation
        String consultationId = getValidConsultationId(consultations);
        if (consultationId == null) {
            return;
        }
        
        // find the consultation from our list
        Consultation consultation = null;
        for (int i = 1; i <= consultations.getNumberOfEntries(); i++) {
            if (consultations.getEntry(i).getConsultationID().equalsIgnoreCase(consultationId)) {
                consultation = consultations.getEntry(i);
                break;
            }
        }
        
        if (consultation == null) {
            System.out.println("Cannot create treatment without valid consultation.");
            return;
        }
        
        System.out.println("Selected consultation: " + consultation.getConsultationID());
        System.out.println("Patient: " + (consultation.getPatient() != null ? consultation.getPatient().getName() : "N/A"));
        System.out.println("Doctor: " + (consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A"));

        // get diagnosis with validation
        String diagnosis = getValidDiagnosis();
        if (diagnosis == null) {
            return;
        }
        
        // show available medicines to help user
        System.out.println("\nAvailable medicines in pharmacy:");
        ListInterface<Pharmacy> allMedicines = manager.getPharmacyManager().getAllMedicines();
        for(int i = 1; i <= allMedicines.getNumberOfEntries(); i++){
            Pharmacy medicine = allMedicines.getEntry(i);
            System.out.println("   " + medicine.getMedID() + " - " + medicine.getMedName() + " (Stock: " + medicine.getMedQty() + ")");
        }
        System.out.println();
        
        // get prescribed medicine with validation
        String prescribed = getValidPrescribedMedicine(allMedicines);
        if (prescribed == null) {
            return; // User cancelled
        }
        
        // get quantity with validation
        int prescribedQty = getValidQuantity();
        if (prescribedQty == -1) { // Check for cancellation
            return;
        }

        Treatment t = manager.addTreatment(consultation, diagnosis, prescribed, prescribedQty, "Pending");
        if(t != null){
            System.out.println("Treatment added with ID: " + t.getTreatmentID());
        } else {
            System.out.println("Failed to add treatment. Please check if medicine exists in pharmacy.");
        }
    }

    // consultation validation
    private String getValidConsultationId(ListInterface<Consultation> consultations) {
        while (true) {
            System.out.print("\nEnter Consultation ID to create treatment for (or 'cancel' to go back): ");
            String consultationId = sc.nextLine().trim();
            
            if (consultationId.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (consultationId.isEmpty()) {
                System.out.println("Error: Consultation ID cannot be empty. Please try again.");
                continue;
            }
            
            // Check if consultation ID exists
            boolean exists = false;
            for (int i = 1; i <= consultations.getNumberOfEntries(); i++) {
                if (consultations.getEntry(i).getConsultationID().equalsIgnoreCase(consultationId)) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                System.out.println("Error: Consultation ID '" + consultationId + "' not found. Please enter a valid ID.");
                continue;
            }
            
            return consultationId;
        }
    }
    
    private String getValidConsultationId() {
        while (true) {
            System.out.print("Enter Consultation ID (or 'cancel' to go back): ");
            String consultationId = sc.nextLine().trim();
            
            if (consultationId.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (consultationId.isEmpty()) {
                System.out.println("Error: Consultation ID cannot be empty. Please try again.");
                continue;
            }
            
            // basic format validation for Consultation ID (C followed by numbers) - case insensitive
            if (!consultationId.matches("(?i)C\\d+")) {
                System.out.println("Error: Consultation ID should start with 'C' followed by numbers. Please try again.");
                System.out.println("Example: C001, c001, C002, c002");
                continue;
            }
            
            // convert to uppercase for consistency
            consultationId = consultationId.toUpperCase();
            
            return consultationId;
        }
    }
    
    private String getValidDiagnosis() {
        while (true) {
            System.out.print("Enter diagnosis (or 'cancel' to go back): ");
            String diagnosis = sc.nextLine().trim();
            
            if (diagnosis.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (diagnosis.isEmpty()) {
                System.out.println("Error: Diagnosis cannot be empty. Please try again.");
                continue;
            }
            
            return diagnosis;
        }
    }
    
    private String getValidPrescribedMedicine(ListInterface<Pharmacy> allMedicines) {
        while (true) {
            System.out.print("Enter prescribed medicine name or ID (or 'cancel' to go back): ");
            String prescribed = sc.nextLine().trim();
            
            if (prescribed.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (prescribed.isEmpty()) {
                System.out.println("Error: Medicine name/ID cannot be empty. Please try again.");
                continue;
            }
            
            // check if medicine exists (by ID or name)
            boolean exists = false;
            for (int i = 1; i <= allMedicines.getNumberOfEntries(); i++) {
                Pharmacy medicine = allMedicines.getEntry(i);
                if (medicine.getMedID().equalsIgnoreCase(prescribed) || 
                    medicine.getMedName().equalsIgnoreCase(prescribed)) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                System.out.println("Error: Medicine '" + prescribed + "' not found in pharmacy. Please enter a valid medicine name or ID.");
                continue;
            }
            
            return prescribed;
        }
    }
    
    private int getValidQuantity() {
        while (true) {
            System.out.print("Enter quantity prescribed (or 'cancel' to go back): ");
            String input = sc.nextLine().trim();
            
            if (input.equalsIgnoreCase("cancel")) {
                return -1; // use -1 to indicate cancellation
            }
            
            if (input.isEmpty()) {
                System.out.println("Error: Quantity cannot be empty. Please try again.");
                continue;
            }
            
            try {
                int quantity = Integer.parseInt(input);
                if (quantity <= 0) {
                    System.out.println("Error: Quantity must be greater than 0. Please try again.");
                    continue;
                }
                if (quantity > 1000) {
                    System.out.println("Error: Quantity seems too high. Please verify and try again.");
                    continue;
                }
                return quantity;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number for quantity.");
                continue;
            }
        }
    }

    private void updateTreatment() {
        // get treatment ID with validation
        String id = getValidTreatmentId();
        if (id == null) {
            return; // User cancelled
        }

        // show current treatment details first
        manager.displayTreatmentDetails(id);

        // check if treatment can be updated
        if (!manager.canUpdateTreatment(id)) {
            System.out.println("Cannot update this treatment!");
            return;
        }

        // get new diagnosis with validation
        String diagnosis = getValidDiagnosis();
        if (diagnosis == null) {
            return;
        }

        // show available medicines and get new prescribed treatment
        System.out.println("\n=== Available Medicines in Pharmacy ===");
        ListInterface<Pharmacy> medicines = manager.getPharmacyManager().getAllMedicines();
        if (medicines.isEmpty()) {
            System.out.println("No medicines available in pharmacy.");
            return;
        }

        System.out.println("ID\t\tName\t\t\tPrice\t\tStock");
        System.out.println("-".repeat(60));
        for (int i = 1; i <= medicines.getNumberOfEntries(); i++) {
            Pharmacy med = medicines.getEntry(i);
            System.out.printf("%-8s\t%-20s\tRM %-8.2f\t%d%n",
                med.getMedID(),
                med.getMedName(),
                med.getMedPrice(),
                med.getMedQty());
        }
        System.out.println("-".repeat(60));

        // get new prescribed treatment with validation
        String prescribed = getValidMedicine();
        if (prescribed == null) {
            return;
        }

        // ask for new quantity (since qty is part of editing in option 4)
        Integer qty = getValidQuantity();
        if (qty == null) {
            return;
        }

        // call manager.updateTreatment (without status)
        if (manager.updateTreatment(id, diagnosis, prescribed, qty)) {
            System.out.println("Treatment updated successfully!");
            System.out.println("\nUpdated treatment details:");
            manager.displayTreatmentDetails(id);
        } else {
            System.out.println("Failed to update treatment.");
        }
    }

    
    private String getValidTreatmentId() {
        while (true) {
            System.out.print("Enter Treatment ID (or 'cancel' to go back): ");
            String id = sc.nextLine().trim();
            
            if (id.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (id.isEmpty()) {
                System.out.println("Error: Treatment ID cannot be empty. Please try again.");
                continue;
            }
            
            return id;
        }
    }
    
    private String getValidStatus() {
        while (true) {
            System.out.print("New status (Pending/Completed) (or 'cancel' to go back): ");
            String status = sc.nextLine().trim();
            
            if (status.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (status.isEmpty()) {
                System.out.println("Error: Status cannot be empty. Please try again.");
                continue;
            }
            
            // validate status values
            if (status.equalsIgnoreCase("Pending") || 
                status.equalsIgnoreCase("Completed")) {
                return status;
            } else {
                System.out.println("Error: Status must be one of: Pending or Completed. Please try again.");
                continue;
            }
        }
    }
    
    private String getValidMedicine() {
        while (true) {
            System.out.print("Enter Medicine ID or Name (or 'cancel' to go back): ");
            String medicine = sc.nextLine().trim();
            
            if (medicine.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (medicine.isEmpty()) {
                System.out.println("Error: Medicine cannot be empty. Please try again.");
                continue;
            }
            
            // check if medicine exists in pharmacy
            PharmacyManager pharmacyManager = manager.getPharmacyManager();
            Pharmacy foundMedicine = pharmacyManager.getMedicine(medicine);
            
            if (foundMedicine == null) {
                // try to find by name
                ListInterface<Pharmacy> medicinesByName = pharmacyManager.searchMedicinesByName(medicine);
                if (!medicinesByName.isEmpty()) {
                    foundMedicine = medicinesByName.getEntry(1);
                }
            }
            
            if (foundMedicine == null) {
                System.out.println("Medicine not found in pharmacy!");
                System.out.println("Please check the medicine ID or name from the list above.");
                continue;
            }
            
            System.out.println("Medicine found: " + foundMedicine.getMedName());
            return foundMedicine.getMedName(); // Return standardized name
        }
    }

    private void deleteTreatment() {
        String id = getValidTreatmentId();
        if (id == null) {
            return; // User cancelled
        }
        
        // confirm deletion
        System.out.print("Are you sure you want to delete treatment " + id + "? (yes/no): ");
        String confirm = sc.nextLine().trim();
        
        if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
            if (manager.deleteTreatment(id)) {
                System.out.println("Deleted successfully.");
            } else {
                System.out.println("Treatment not found.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void searchMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println(" SEARCH TREATMENT MENU");
            System.out.println("=".repeat(50));
            System.out.println("1. By Patient ID");
            System.out.println("2. By Doctor ID");
            System.out.println("3. By Status");
            System.out.println("4. By Consultation ID");
            System.out.println("5. Back");
            System.out.println("-".repeat(50));
            System.out.print("Enter choice: ");
            String input = sc.nextLine();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number (1-5).");
                continue; // retry search menu
            }

            switch (choice) {
                case 1:
                    String pid = getValidPatientId();
                    if (pid != null) {
                        ListInterface<Treatment> patientTreatments = manager.searchTreatmentsByPatient(pid);
                        manager.displayTreatmentSearchResults(patientTreatments, "Patient ID", pid);
                    }
                    break;
                case 2:
                    String did = getValidDoctorId();
                    if (did != null) {
                        ListInterface<Treatment> doctorTreatments = manager.searchTreatmentsByDoctor(did);
                        manager.displayTreatmentSearchResults(doctorTreatments, "Doctor ID", did);
                    }
                    break;
                case 3:
                    String status = getValidStatus();
                    if (status != null) {
                        manager.filterTreatmentsByStatus(status);
                    }
                    break;
                case 4:
                    String consultationId = getValidConsultationId();
                    if (consultationId != null) {
                        manager.displayTreatmentsByConsultation(consultationId);
                    }
                    break;
                case 5:
                    System.out.println("Returning to Treatment Menu...");
                    return; // back to main treatment menu
                default:
                    System.out.println("Invalid choice! Please enter 1-5.");
            }
        }
    }

    private String getValidPatientId() {
        while (true) {
            System.out.print("Enter Patient ID (or 'cancel' to go back): ");
            String pid = sc.nextLine().trim();
            
            if (pid.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (pid.isEmpty()) {
                System.out.println("Error: Patient ID cannot be empty. Please try again.");
                continue;
            }
            
            // B=basic format validation for Patient ID (P followed by numbers) - case insensitive
            if (!pid.matches("(?i)P\\d+")) {
                System.out.println("Error: Patient ID should start with 'P' followed by numbers. Please try again.");
                System.out.println("Example: P001, p001, P002, p002");
                continue;
            }
            
            // convert to uppercase for consistency
            pid = pid.toUpperCase();
            
            return pid;
        }
    }
    
    private String getValidDoctorId() {
        while (true) {
            System.out.print("Enter Doctor ID (or 'cancel' to go back): ");
            String did = sc.nextLine().trim();
            
            if (did.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            if (did.isEmpty()) {
                System.out.println("Error: Doctor ID cannot be empty. Please try again.");
                continue;
            }
            
            // basic format validation for Doctor ID (D followed by numbers) - case insensitive
            if (!did.matches("(?i)D\\d+")) {
                System.out.println("Error: Doctor ID should start with 'D' followed by numbers. Please try again.");
                System.out.println("Example: D001, d001, D002, d002");
                continue;
            }
            
            // convert to uppercase for consistency
            did = did.toUpperCase();
            
            return did;
        }
    }

    private void generateHistory() {
        String pid = getValidPatientId();
        if (pid != null) {
            manager.displayTreatmentHistory(pid);
        }
    }

    private void sortTreatments() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println(" SORT TREATMENTS MENU");
            System.out.println("=".repeat(60));
            System.out.println("1. By Date (Newest First)");
            System.out.println("2. By Date (Oldest First)");
            System.out.println("3. By Treatment ID");
            System.out.println("4. By Status");
            System.out.println("5. Back to Treatment Menu");
            System.out.println("-".repeat(60));
            System.out.print("Enter choice: ");
            
            String input = sc.nextLine();
            int choice;
            
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number (1-5).");
                continue;
            }
            
            switch (choice) {
                case 1:
                    System.out.println("Sorting treatments by date (newest first)...");
                    manager.displaySortedTreatmentsByDate(false);
                    break;
                case 2:
                    System.out.println("Sorting treatments by date (oldest first)...");
                    manager.displaySortedTreatmentsByDate(true);
                    break;
                case 3:
                    System.out.println("Sorting treatments by ID...");
                    manager.displaySortedTreatmentsById();
                    break;
                case 4:
                    System.out.println("Sorting treatments by status...");
                    manager.displaySortedTreatmentsByStatus();
                    break;
                case 5:
                    System.out.println("Returning to Treatment Menu...");
                    return;
                default:
                    System.out.println("Invalid choice! Please enter 1-5.");
            }
        }
    }
    
    private void viewTreatmentsByConsultation() {
        System.out.println("\n=== View Treatments by Consultation ===");
        
        // get all consultations
        ListInterface<Consultation> consultations = manager.getConsultationManager().getAllConsultations();
        
        if (consultations.isEmpty()) {
            System.out.println("No consultations found.");
            return;
        }
        
        // display consultations
        System.out.println("Available consultations:");
        System.out.println("-".repeat(60));
        
        for (int i = 1; i <= consultations.getNumberOfEntries(); i++) {
            Consultation c = consultations.getEntry(i);
            System.out.println("ID: " + c.getConsultationID());
            System.out.println("Patient: " + (c.getPatient() != null ? c.getPatient().getName() : "N/A"));
            System.out.println("Doctor: " + (c.getDoctor() != null ? c.getDoctor().getName() : "N/A"));
            System.out.println("Date: " + c.getDateTime());
            System.out.println("Reason: " + c.getReason());
            System.out.println("-".repeat(40));
        }
        
        // get consultation ID to view treatments
        String consultationId = getValidConsultationId(consultations);
        if (consultationId == null) {
            return; 
        }
        
        // find and display treatments for this consultation
        System.out.println("\nTreatments for consultation " + consultationId + ":");
        System.out.println("-".repeat(60));
        
        // display treatments for this consultation
        manager.displayTreatmentsByConsultation(consultationId);
    }

    private void generateDailyReport() {
        String date = manager.selectDateForReport();
        if (date != null) {
            manager.displayDailyTreatmentReport(date);
        }
    }
    
    private void updateStatusOnly() {
        String id = getValidTreatmentId();
        if (id == null) {
            return;
        }
        
        // show current treatment details first
        manager.displayTreatmentDetails(id);
        
        // get new status
        String status = getValidStatus();
        if (status == null) {
            return;
        }
        
        if (manager.updateTreatmentStatusOnly(id, status)) {
            System.out.println("Status updated successfully!");
            System.out.println("Updated treatment details:");
            manager.displayTreatmentDetails(id);
        } else {
            System.out.println("Failed to update status.");
        }
    }

        public static void main(String[] args) {
                       TreatmentMenu treatmentMenu = new TreatmentMenu();
                        treatmentMenu.runTreatmentMenu();
    }
    
}
