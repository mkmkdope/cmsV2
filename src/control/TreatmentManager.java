package control;

import dao.TreatmentDAO;
import adt.CircularDoublyLinkedList;
import control.PharmacyManager;
import entity.Pharmacy;
import entity.Consultation;
import entity.Treatment;
import entity.Patient;
import entity.Doctor;
import java.time.LocalDateTime;
import adt.ListInterface;

public class TreatmentManager {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_COMPLETED = "Completed";
    
    private boolean isValidStatus(String status) {
        if (status == null) return false;
        String normalized = status.trim().toLowerCase();
        return normalized.equals("pending") || normalized.equals("completed");
    }
    
    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) return null;
        String normalized = status.trim().toLowerCase();
        switch(normalized){
            case "pending":
                return STATUS_PENDING;
            case "completed":
                return STATUS_COMPLETED;
            default:
                return null;
        }
    }
    
    public void showAvailableTransitions(String treatmentId) {
        Treatment treatment = getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return;
        }
        
        String current = treatment.getTreatmentStatus();
        System.out.println("\n=== STATUS TRANSITIONS FOR " + treatmentId + " ===");
        System.out.println("Current Status: " + current);
        
        if (current.equals(STATUS_PENDING)) {
            System.out.println("Available transitions:");
            System.out.println("   → COMPLETED (when medicine is dispensed)");
            
            // check if stock is available for COMPLETED transition
            if (canDispenseMedicine(treatmentId)) {
                System.out.println("Stock check: Available - can dispense medicine");
        } else {
                System.out.println("Stock check: Insufficient - cannot dispense medicine");
                System.out.println("   Please restock medicine first");
            }
        } else if (current.equals(STATUS_COMPLETED)) {
            System.out.println("No transitions available - FINAL STATUS");
            System.out.println("   Medicine has been dispensed to patient");
        }
        System.out.println("=====================================");
    }
    
    
     // check if medicine can be dispensed (stock available)
    
    public boolean canDispenseMedicine(String treatmentId) {
        Treatment treatment = getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }
        
        if (!STATUS_PENDING.equalsIgnoreCase(treatment.getTreatmentStatus())) {
            System.out.println("Treatment is not in " + STATUS_PENDING + " status");
            System.out.println("Current status: " + treatment.getTreatmentStatus());
            return false;
        }
        
        String medicineName = treatment.getPrescribed();
        int requiredQty = treatment.getPrescribedQty();
        
        Pharmacy med = findMedicine(medicineName);
        if (med == null) {
            System.out.println("Medicine '" + medicineName + "' not found in pharmacy");
            System.out.println("Action: Check medicine name or restock the medicine");
            return false;
        }
        
        if (med.getMedQty() < requiredQty) {
            System.out.println("Insufficient medicine stock!");
            System.out.println("   Required: " + requiredQty + " units");
            System.out.println("   Available: " + med.getMedQty() + " units");
            System.out.println("   Shortage: " + (requiredQty - med.getMedQty()) + " units");
            System.out.println("Action: Restock medicine before dispensing");
            return false;
        }
        
        System.out.println("Medicine can be dispensed!");
        System.out.println("   Medicine: " + medicineName);
        System.out.println("   Required: " + requiredQty + " units");
        System.out.println("   Available: " + med.getMedQty() + " units");
        System.out.println("   Remaining after deduction: " + (med.getMedQty() - requiredQty) + " units");
        return true;
    }

    private final TreatmentDAO dao = new TreatmentDAO();
    private final PharmacyManager pharmacyManager = new PharmacyManager();
    private final ConsultationManager consultationManager = new ConsultationManager();

public Treatment addTreatment(Consultation consultation, String diagnosis,
                              String prescribed, int prescribedQty, String status) {
    String today = java.time.LocalDate.now().toString();

    // create treatment ID
    String treatmentID = "T" + String.format("%04d", dao.size() + 1);

    // check medicine exists in pharmacy
    Pharmacy medicine = null;
    ListInterface<Pharmacy> allMedicines = pharmacyManager.getAllMedicines();
    for (int i = 1; i <= allMedicines.getNumberOfEntries(); i++) {
        Pharmacy m = allMedicines.getEntry(i);
        if (m.getMedID().equalsIgnoreCase(prescribed) ||
            m.getMedName().equalsIgnoreCase(prescribed)) {
            medicine = m;
            break;
        }
    }

    if (medicine == null) {
        System.out.println("Medicine not found in pharmacy: " + prescribed);
        System.out.println("Please check the medicine name or ID. Available medicines:");
        for (int i = 1; i <= allMedicines.getNumberOfEntries(); i++) {
            Pharmacy m = allMedicines.getEntry(i);
            System.out.println("   " + m.getMedID() + " - " + m.getMedName());
        }
        return null; // medicine truly not found
    }

    // create treatment object
    Treatment t = new Treatment(treatmentID, consultation, diagnosis, 
                  medicine.getMedName(), prescribedQty, today, "Pending");


    // check stock
    if (prescribedQty > medicine.getMedQty()) {
        System.out.println("Warning: Not enough stock for " + medicine.getMedName());
        System.out.println("Requested: " + prescribedQty + ", Available: " + medicine.getMedQty());
        System.out.println("Treatment created as Pending. Please restock before dispensing.");
        // Do not deduct stock yet
    } else {
        // stock sufficient → deduct immediately if status is Completed
        if (status.equalsIgnoreCase("Completed")) {
            medicine.setMedQty(medicine.getMedQty() - prescribedQty);
            t.setTreatmentStatus("Completed");
        }
    }

    dao.add(t);
    return t;
}

    
    // only for doctor
    // update diagnosis, prescribed, qty
    public boolean updateTreatment(String id, String diagnosis, String prescribed, Integer prescribedQty) {
        Treatment treatment = dao.findByID(id);
            if (treatment == null) {
                System.out.println("Treatment not found.");
                return false;
            }

            // block update if treatment is already Completed
            if (!canUpdateTreatment(id)) {
                return false;
            }

            // validate prescribed medicine (if provided)
            String finalPrescribed = null;
            if (prescribed != null && !prescribed.isBlank()) {
                Pharmacy med = findMedicine(prescribed);
                if (med == null) {
                    System.out.println("Medicine not found in pharmacy: " + prescribed);
                    return false;
                }
                finalPrescribed = med.getMedName(); // normalize to actual pharmacy name
            }

            // update fields (diagnosis, prescribed, qty) — but NOT status
            return dao.updateFields(id, diagnosis, finalPrescribed, prescribedQty, null);
    }




    // update only the status of a treatment (without changing diagnosis/prescription)
    // this is useful when you only want to change status from PENDING to COMPLETED
    
    public boolean updateTreatmentStatusOnly(String id, String status) {
        Treatment treatment = dao.findByID(id);
        if (treatment == null) {
            System.out.println("Treatment not found: " + id);
            return false;
        }
        
        // Check if treatment can be updated
        if (!canUpdateTreatment(id)) {
            return false;
        }
        
        return updateTreatmentStatus(id, status);
    }
    
    
    // check if a treatment can be updated
    // COMPLETED treatments cannot be updated
    
    public boolean canUpdateTreatment(String id) {
        Treatment treatment = dao.findByID(id);
        if (treatment == null) {
            System.out.println("Treatment not found: " + id);
            return false;
        }
        
        if (STATUS_COMPLETED.equals(treatment.getTreatmentStatus())) {
            System.out.println("Cannot update COMPLETED treatment!");
            System.out.println("Medicine has already been dispensed to patient.");
            System.out.println("Diagnosis, prescription, and status cannot be changed.");
            return false;
        }
        
        return true;
    }
    
    
    // display current treatment details
    
    public void displayTreatmentDetails(String id) {
        Treatment treatment = dao.findByID(id);
        if (treatment == null) {
            System.out.println("Treatment not found: " + id);
            return;
        }
        
        System.out.println("\n=== TREATMENT DETAILS ===");
        System.out.println("ID: " + treatment.getTreatmentID());
        System.out.println("Status: " + treatment.getTreatmentStatus());
        System.out.println("Diagnosis: " + treatment.getDiagnosis());
        System.out.println("Prescribed: " + treatment.getPrescribed());
        System.out.println("Quantity: " + treatment.getPrescribedQty());
        System.out.println("Date: " + treatment.getTreatmentDate());
        
        if (treatment.getConsultation() != null) {
            System.out.println("Patient: " + (treatment.getConsultation().getPatient() != null ? 
                treatment.getConsultation().getPatient().getName() : "N/A"));
            System.out.println("Doctor: " + (treatment.getConsultation().getDoctor() != null ? 
                treatment.getConsultation().getDoctor().getName() : "N/A"));
        }
        
        // show stock status if treatment is PENDING
        if (STATUS_PENDING.equals(treatment.getTreatmentStatus())) {
            System.out.println("\n--- Stock Status ---");
            showStockGuidance(id);
        }
        System.out.println("=====================");
    }
    
    public boolean updateTreatmentStatus(String id, String status) {
        if (status == null) return false;

        // validate input status
        if (!isValidStatus(status)) {
            System.out.println("Invalid status! Allowed: " + STATUS_PENDING + ", " + STATUS_COMPLETED);
            return false;
        }

        String finalStatus = normalizeStatus(status);
        Treatment treatment = dao.findByID(id);

        if (treatment == null) {
            System.out.println("Treatment not found: " + id);
            return false;
        }

        String currentStatus = treatment.getTreatmentStatus();

        // COMPLETED → Any other status: Not allowed
        if (currentStatus.equals(STATUS_COMPLETED)) {
            System.out.println("Cannot change status of completed treatment.");
            System.out.println("Medicine has already been dispensed to patient.");
            return false;
        }

        // PENDING → COMPLETED: check stock & deduct
        if (currentStatus.equals(STATUS_PENDING) && finalStatus.equals(STATUS_COMPLETED)) {
            Pharmacy medicine = null;
            ListInterface<Pharmacy> allMedicines = pharmacyManager.getAllMedicines();

            for (int i = 1; i <= allMedicines.getNumberOfEntries(); i++) {
                Pharmacy m = allMedicines.getEntry(i);
                if (m.getMedID().equalsIgnoreCase(treatment.getPrescribed()) ||
                    m.getMedName().equalsIgnoreCase(treatment.getPrescribed())) {
                    medicine = m;
                    break;
                }
            }

            if (medicine == null) {
                System.out.println("Failed to find prescribed medicine in pharmacy!");
                return false;
            }

            int required = treatment.getPrescribedQty();
            if (required > medicine.getMedQty()) {
                System.out.println("Not enough stock available. Treatment stays as Pending.");
                return false;
            }

            // deduct stock
            medicine.setMedQty(medicine.getMedQty() - required);

            // update treatment status
            treatment.setTreatmentStatus(STATUS_COMPLETED);

            // calculate price
            double unitPrice = medicine.getMedPrice();
            double totalPrice = unitPrice * required;

            // show success + billing info
            System.out.println("Treatment " + id + " marked as COMPLETED. Medicine dispensed.");
            System.out.println("\n PRICE CALCULATION:");
            System.out.println("Medicine: " + medicine.getMedName());
            System.out.println("Unit Price: RM " + String.format("%.2f", unitPrice));
            System.out.println("Quantity: " + required + " units");
            System.out.println("Total Price: RM " + String.format("%.2f", totalPrice));
            System.out.println("=======================================");

            return true;
        }

        // PENDING → PENDING: nothing changes
        if (currentStatus.equals(STATUS_PENDING) && finalStatus.equals(STATUS_PENDING)) {
            System.out.println("Treatment is already in PENDING status.");
            return true;
        }

        // any invalid transitions
        System.out.println("Invalid status transition from " + currentStatus + " to " + finalStatus);
        showAvailableTransitions(id);
        return false;
    }

//    private boolean dispenseMedicineFromPending(String treatmentId) {
//        Treatment treatment = dao.findByID(treatmentId);
//        if (treatment == null) {
//            System.out.println("Treatment not found: " + treatmentId);
//            return false;
//        }
//
//        // Check if medicine is available
//        String medicineName = treatment.getPrescribed();
//        int requiredQty = treatment.getPrescribedQty();
//
//        Pharmacy med = findMedicine(medicineName);
//        if (med == null) {
//            System.out.println("Medicine '" + medicineName + "' not found in pharmacy");
//            System.out.println("Cannot dispense medicine - medicine unavailable");
//            return false;
//        }
//
//        if (med.getMedQty() < requiredQty) {
//            System.out.println("Insufficient medicine stock! Required: " + requiredQty + ", Available: " + med.getMedQty());
//            System.out.println("Cannot dispense medicine - please restock medicine first");
//            return false;
//        }
//
//        // Deduct stock
//        boolean stockDeducted = pharmacyManager.restockMedicine(med.getMedID(), -requiredQty);
//        if (!stockDeducted) {
//            System.out.println("Failed to deduct medicine stock. Medicine cannot be dispensed.");
//            return false;
//        }
//
//        // update treatment status
//        boolean updated = dao.updateFields(treatmentId, null, null, null, STATUS_COMPLETED);
//        if (!updated) {
//            System.out.println("Failed to update treatment status.");
//            return false;
//        }
//
//        // show success and cost summary
//        System.out.println("Treatment " + treatmentId + " marked as COMPLETED");
//        System.out.println("Medicine dispensed: " + requiredQty + " units of " + medicineName);
//
//        // 🔹 Get total price (via PharmacyManager)
//        double totalPrice = pharmacyManager.calculateTreatmentPrice(treatmentId);
//        System.out.println("Total Cost for this treatment: RM " + String.format("%.2f", totalPrice));
//
//        return true;
//    }


    public boolean deleteTreatment(String id) {
        return dao.remove(id);
    }

    public void viewAllTreatments() {
        if (dao.isEmpty()) {
            System.out.println("No treatment records found.");
            return;
        }
        CircularDoublyLinkedList<Treatment> all = dao.getAll();
        System.out.println("=".repeat(50));
        for (int i = 1; i <= all.getNumberOfEntries(); i++) {
            System.out.println(all.getEntry(i));
            System.out.println("=".repeat(50));
        }
    }

    // simple bubble sort on the DAO's list (by yyyy-MM-dd string compare)
    public void sortTreatmentsByDate(boolean newestFirst) {
        if (dao.isEmpty()) {
            System.out.println("No treatments to sort.");
            return;
        }

        int n = dao.size();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j < n; j++) {
                Treatment t1 = dao.getAt(j);
                Treatment t2 = dao.getAt(j + 1);

                int cmp = t1.getTreatmentDate().compareTo(t2.getTreatmentDate());
                boolean shouldSwap = (newestFirst && cmp < 0) || (!newestFirst && cmp > 0);
                if (shouldSwap) {
                    dao.replaceAt(j, t2);
                    dao.replaceAt(j + 1, t1);
                }
            }
        }

        // display after sorting
        viewAllTreatments();
    }

    public void searchTreatmentByPatient(String patientId) {
        boolean found = false;
        CircularDoublyLinkedList<Treatment> all = dao.getAll();
        
        System.out.println("=".repeat(50));
        for (int i = 1; i <= all.getNumberOfEntries(); i++) {
            Treatment t = all.getEntry(i);
            if (t.getConsultation() != null &&
                t.getConsultation().getPatient() != null &&
                t.getConsultation().getPatient().getPatientId().equalsIgnoreCase(patientId)) {
                System.out.println(t);
                System.out.println("=".repeat(50));
                found = true;
            }
        }
        if (!found) System.out.println("No treatments found for patient ID: " + patientId);
    }

    public void searchTreatmentByDoctor(String doctorId) {
        boolean found = false;
        CircularDoublyLinkedList<Treatment> all = dao.getAll();
        System.out.println("=".repeat(50));
        
        for (int i = 1; i <= all.getNumberOfEntries(); i++) {
            Treatment t = all.getEntry(i);
            if (t.getConsultation() != null &&
                t.getConsultation().getDoctor() != null &&
                t.getConsultation().getDoctor().getDoctorId().equalsIgnoreCase(doctorId)) {
                System.out.println(t);
                System.out.println("=".repeat(50));
                found = true;
            }
        }
        if (!found) System.out.println("No treatments found for doctor ID: " + doctorId);
    }

    public void generateMostCommonDiagnosisReport() {
        if (dao.isEmpty()) {
            System.out.println("No treatments available.");
            return;
        }

        // We will count via parallel lists (keeps same ADT usage style)
        CircularDoublyLinkedList<String> diagNames = new CircularDoublyLinkedList<>();
        CircularDoublyLinkedList<Integer> diagCounts = new CircularDoublyLinkedList<>();

        CircularDoublyLinkedList<Treatment> all = dao.getAll();
        for (int i = 1; i <= all.getNumberOfEntries(); i++) {
            String diagnosis = all.getEntry(i).getDiagnosis();
            boolean exists = false;

            for (int j = 1; j <= diagNames.getNumberOfEntries(); j++) {
                if (diagNames.getEntry(j).equalsIgnoreCase(diagnosis)) {
                    diagCounts.replace(j, diagCounts.getEntry(j) + 1);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                diagNames.add(diagnosis);
                diagCounts.add(1);
            }
        }

        System.out.println("\n--- Most Common Diagnoses Report ---");
        for (int i = 1; i <= diagNames.getNumberOfEntries(); i++) {
            System.out.println(diagNames.getEntry(i) + " : " + diagCounts.getEntry(i));
        }
    }

    public void generateTreatmentHistoryByPatient(String patientId) {
        boolean found = false;
        System.out.println("\n--- Treatment History for Patient ID: " + patientId + " ---");

        CircularDoublyLinkedList<Treatment> all = dao.getAll();
        for (int i = 1; i <= all.getNumberOfEntries(); i++) {
            Treatment t = all.getEntry(i);
            if (t.getConsultation() != null &&
                t.getConsultation().getPatient() != null &&
                t.getConsultation().getPatient().getPatientId().equalsIgnoreCase(patientId)) {
                System.out.println(t);
                found = true;
            }
        }
        if (!found) System.out.println("No history for patient ID: " + patientId);
    }

    // get treatment by ID
    public Treatment getTreatmentById(String id) {
        return dao.findByID(id);
    }
    
    // get pharmacy manager for accessing medicine information
    public PharmacyManager getPharmacyManager() {
        return pharmacyManager;
    }
    
    // get consultation manager for accessing consultation information
    public ConsultationManager getConsultationManager() {
        return consultationManager;
    }
    
    // helper method to find medicine by ID or name (case-insensitive)
    private Pharmacy findMedicine(String medicineIdentifier) {
        if (medicineIdentifier == null || medicineIdentifier.trim().isEmpty()) return null;
        String key = medicineIdentifier.trim();

        ListInterface<Pharmacy> all = pharmacyManager.getAllMedicines();
        // Try ID (case-insensitive)
        for (int i = 1; i <= all.getNumberOfEntries(); i++) {
            Pharmacy m = all.getEntry(i);
            if (m.getMedID().equalsIgnoreCase(key)) return m;
        }
        // Try Name (case-insensitive)
        for (int i = 1; i <= all.getNumberOfEntries(); i++) {
            Pharmacy m = all.getEntry(i);
            if (m.getMedName().equalsIgnoreCase(key)) return m;
        }
        return null;
    }

    
    // get all treatments for a specific consultation
    public void getTreatmentsByConsultation(String consultationId) {
        boolean found = false;
        CircularDoublyLinkedList<Treatment> all = dao.getAll();
        
        System.out.println("=".repeat(50));
        System.out.println("Treatments for Consultation: " + consultationId);
        System.out.println("=".repeat(50));
        
        for (int i = 1; i <= all.getNumberOfEntries(); i++) {
            Treatment t = all.getEntry(i);
            if (t.getConsultation() != null &&
                t.getConsultation().getConsultationID().equalsIgnoreCase(consultationId)) {
                System.out.println(t);
                System.out.println("=".repeat(50));
                found = true;
            }
        }
        if (!found) System.out.println("No treatments found for consultation ID: " + consultationId);
    }
    
    // get all treatments (for display purposes)
    public CircularDoublyLinkedList<Treatment> getAll() {
        return dao.getAll();
    }
    
    // get all treatments (for display purposes) - enhanced version
    public ListInterface<Treatment> getAllTreatments() {
        return dao.getAll();
    }
    

    
    public void displayAllTreatments() {
        System.out.println("\n" + "=".repeat(130));
        System.out.println("                                            ALL TREATMENT RECORDS");
        System.out.println("=".repeat(130));
        
        ListInterface<Treatment> treatments = getAllTreatments();
        
        if (treatments.isEmpty()) {
            System.out.println("                                                                     No treatments found.");
            System.out.println("=".repeat(130));
            return;
        }
        
        System.out.println(" ID     Diagnosis           Prescribed Medicine      Qty    Date         Status      Patient ID   Patient Name     Doctor");
        System.out.println("-".repeat(130));
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            String patientId = t.getConsultation().getPatient() != null ? 
                t.getConsultation().getPatient().getPatientId() : "N/A";
            String patientName = t.getConsultation().getPatient() != null ? 
                t.getConsultation().getPatient().getName() : "N/A";
            String doctorName = t.getConsultation().getDoctor() != null ? 
                t.getConsultation().getDoctor().getName() : "N/A";
            
            System.out.printf(" %-6s %-18s %-25s %-6d %-12s %-11s %-12s %-16s %-16s%n",
                t.getTreatmentID(),
                truncateString(t.getDiagnosis(), 18),
                truncateString(t.getPrescribed(), 25),
                t.getPrescribedQty(),
                t.getTreatmentDate(),
                t.getTreatmentStatus(),
                patientId,
                truncateString(patientName, 16),
                truncateString(doctorName, 16)
            );
        }
        
        System.out.println("-".repeat(130));
        System.out.println("Total Treatments: " + treatments.getNumberOfEntries());
        System.out.println("=".repeat(130));
    }
    
    public void displayTreatmentSearchResults(ListInterface<Treatment> treatments, String searchType, String searchValue) {
        System.out.println("\n" + "=".repeat(130));
        System.out.println("                                    TREATMENT SEARCH RESULTS");
        System.out.println("=".repeat(130));
        System.out.println("Search Type: " + searchType + " | Search Value: " + searchValue);
        System.out.println("-".repeat(130));
        
        if (treatments.isEmpty()) {
            System.out.println("                                                                     No treatments found.");
            System.out.println("=".repeat(130));
            return;
        }
        
        System.out.println(" ID     Diagnosis           Prescribed Medicine      Qty    Date         Status      Patient ID   Patient Name     Doctor");
        System.out.println("-".repeat(130));
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            String patientId = t.getConsultation().getPatient() != null ? 
                t.getConsultation().getPatient().getPatientId() : "N/A";
            String patientName = t.getConsultation().getPatient() != null ? 
                t.getConsultation().getPatient().getName() : "N/A";
            String doctorName = t.getConsultation().getDoctor() != null ? 
                t.getConsultation().getDoctor().getName() : "N/A";
            
            System.out.printf(" %-6s %-18s %-25s %-6d %-12s %-11s %-12s %-16s %-16s%n",
                t.getTreatmentID(),
                truncateString(t.getDiagnosis(), 18),
                truncateString(t.getPrescribed(), 25),
                t.getPrescribedQty(),
                t.getTreatmentDate(),
                t.getTreatmentStatus(),
                patientId,
                truncateString(patientName, 16),
                truncateString(doctorName, 16)
            );
        }
        
        System.out.println("-".repeat(130));
        System.out.println("Total Results: " + treatments.getNumberOfEntries());
        System.out.println("=".repeat(130));
    }
    
    public void displayTreatmentHistory(String patientId) {
        System.out.println("\n" + "=".repeat(120));
        System.out.println(" PATIENT TREATMENT HISTORY");
        System.out.println("=".repeat(120));
        
        // get patient name - work around missing getAllPatients method
        String patientName = "Unknown";
        // try to get patient name from existing treatments first
        ListInterface<Treatment> treatments = getAllTreatments();
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getConsultation().getPatient() != null && 
                t.getConsultation().getPatient().getPatientId().equalsIgnoreCase(patientId)) {
                patientName = t.getConsultation().getPatient().getName();
                break;
            }
        }
        
        System.out.println("Patient ID: " + patientId + " | Patient Name: " + patientName);
        System.out.println("-".repeat(120));
        
        boolean found = false;
        
        System.out.println(" ID     Diagnosis           Prescribed Medicine      Qty    Date         Status      Doctor");
        System.out.println("-".repeat(120));
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getConsultation().getPatient() != null && 
                t.getConsultation().getPatient().getPatientId().equalsIgnoreCase(patientId)) {
                found = true;
                String doctorName = t.getConsultation().getDoctor() != null ? 
                    t.getConsultation().getDoctor().getName() : "N/A";
                
                System.out.printf(" %-6s %-18s %-25s %-6d %-12s %-11s %-16s%n",
                    t.getTreatmentID(),
                    truncateString(t.getDiagnosis(), 18),
                    truncateString(t.getPrescribed(), 25),
                    t.getPrescribedQty(),
                    t.getTreatmentDate(),
                    t.getTreatmentStatus(),
                    truncateString(doctorName, 16)
                );
            }
        }
        
        if (!found) {
            System.out.println("                                                                 No treatment history found for this patient.");
        }
        
        System.out.println("-".repeat(120));
        System.out.println("=".repeat(120));
    }
    
    public void displayDailyTreatmentReport(String date) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("                              DAILY TREATMENT REPORT " + date);
        System.out.println("=".repeat(100));
        
        ListInterface<Treatment> treatments = getAllTreatments();
        boolean found = false;
        
        // convert input date from DD-MM-YYYY to YYYY-MM-DD format for comparison
        String convertedDate = convertDateToStoredFormat(date);
        
        System.out.println(" ID     Diagnosis           Prescribed Medicine      Qty    Status      Patient         Doctor");
        System.out.println("-".repeat(100));
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            // compare with converted date format
            if (t.getTreatmentDate().equals(convertedDate)) {
                found = true;
                String patientName = t.getConsultation().getPatient() != null ? 
                    t.getConsultation().getPatient().getName() : "N/A";
                String doctorName = t.getConsultation().getDoctor() != null ? 
                    t.getConsultation().getDoctor().getName() : "N/A";
                
                System.out.printf(" %-6s %-18s %-25s %-6d %-11s %-16s %-16s%n",
                    t.getTreatmentID(),
                    truncateString(t.getDiagnosis(), 18),
                    truncateString(t.getPrescribed(), 25),
                    t.getPrescribedQty(),
                    t.getTreatmentStatus(),
                    truncateString(patientName, 16),
                    truncateString(doctorName, 16)
                );
            }
        }
        
        if (!found) {
            System.out.println("                                                                 No treatments found for this date.");
            System.out.println("                                                                 Note: Searching for date: " + convertedDate);
        }
        
        System.out.println("-".repeat(100));
        System.out.println("Total Treatments for " + date + ": " + (found ? "1" : "0"));
        System.out.println("=".repeat(100));
    }
    
    /**
     * Convert date from DD-MM-YYYY format to YYYY-MM-DD format for database comparison
     */
    private String convertDateToStoredFormat(String inputDate) {
        try {
            // parse DD-MM-YYYY format
            java.time.LocalDate date = java.time.LocalDate.parse(inputDate, 
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            
            // convert to YYYY-MM-DD format
            return date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            // if conversion fails, return original date
            return inputDate;
        }
    }

    public void displayDiagnosisReport() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(" DIAGNOSIS FREQUENCY REPORT");
        System.out.println("=".repeat(80));
        
        ListInterface<Treatment> treatments = getAll();
        
        if (treatments.isEmpty()) {
            System.out.println("                                                                 No treatments found.");
            System.out.println("=".repeat(80));
            return;
        }
        
        // Count diagnosis frequencies using only ADT classes
        ListInterface<String> diagNames = new CircularDoublyLinkedList<>();
        ListInterface<Integer> diagCounts = new CircularDoublyLinkedList<>();
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            String diagnosis = t.getDiagnosis();
            boolean exists = false;
            
            // check if diagnosis already exists in our list
            for (int j = 1; j <= diagNames.getNumberOfEntries(); j++) {
                if (diagNames.getEntry(j).equalsIgnoreCase(diagnosis)) {
                    // add count for existing diagnosis
                    int currentCount = diagCounts.getEntry(j);
                    diagCounts.replace(j, currentCount + 1);
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                // add new diagnosis and count
                diagNames.add(diagnosis);
                diagCounts.add(1);
            }
        }
        
        System.out.println(" Rank   Diagnosis                    Count   Percentage");
        System.out.println("-".repeat(80));
        
        int total = treatments.getNumberOfEntries();
        for (int i = 1; i <= diagNames.getNumberOfEntries(); i++) {
            String diagnosis = diagNames.getEntry(i);
            int count = diagCounts.getEntry(i);
            double percentage = (double) count / total * 100;
            
            System.out.printf(" %-6d %-30s %-6d %-8.1f%%%n",
                i,
                truncateString(diagnosis, 30),
                count,
                percentage
            );
        }
        
        System.out.println("-".repeat(80));
        System.out.println("Total Treatments: " + total);
        System.out.println("=".repeat(80));
    }


    public void displaySortedTreatmentsByStatus() {
        ListInterface<Treatment> treatments = getAll();
        if (treatments.isEmpty()) {
            System.out.println("No treatments to sort.");
            return;
        }
        
        // group treatments by status using only ADT classes
        ListInterface<String> statusTypes = new CircularDoublyLinkedList<>();
        statusTypes.add(STATUS_PENDING);
        statusTypes.add(STATUS_COMPLETED);
        
        System.out.println("\n" + "=".repeat(120));
        System.out.println(" SORTED TREATMENTS BY STATUS");
        System.out.println("=".repeat(120));
        
        for (int statusIndex = 1; statusIndex <= statusTypes.getNumberOfEntries(); statusIndex++) {
            String status = statusTypes.getEntry(statusIndex);
            ListInterface<Treatment> statusTreatments = new CircularDoublyLinkedList<>();
            
            // Collect treatments for this status
            for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
                Treatment t = treatments.getEntry(i);
                if (t.getTreatmentStatus().equals(status)) {
                    statusTreatments.add(t);
                }
            }
            
            if (statusTreatments.getNumberOfEntries() > 0) {
                System.out.println("\nStatus: " + status + " (" + statusTreatments.getNumberOfEntries() + " treatments)");
                System.out.println("-".repeat(120));
                
                System.out.println(" ID     Diagnosis           Prescribed Medicine      Qty    Date         Patient         Doctor");
                System.out.println("-".repeat(120));
                
                for (int i = 1; i <= statusTreatments.getNumberOfEntries(); i++) {
                    Treatment t = statusTreatments.getEntry(i);
                    String patientName = t.getConsultation().getPatient() != null ? 
                        t.getConsultation().getPatient().getName() : "N/A";
                    String doctorName = t.getConsultation().getDoctor() != null ? 
                        t.getConsultation().getDoctor().getName() : "N/A";
                    
                    System.out.printf(" %-6s %-18s %-25s %-6d %-12s %-16s %-16s%n",
                        t.getTreatmentID(),
                        truncateString(t.getDiagnosis(), 18),
                        truncateString(t.getPrescribed(), 25),
                        t.getPrescribedQty(),
                        t.getTreatmentDate(),
                        truncateString(patientName, 16),
                        truncateString(doctorName, 16)
                    );
                }
                System.out.println("-".repeat(120));
            }
        }
        
        System.out.println("=".repeat(120));
    }


    public void displayTreatmentsByConsultation(String consultationId) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println(" TREATMENTS FOR CONSULTATION " + consultationId);
        System.out.println("=".repeat(100));
        
        // get treatments for this consultation
        ListInterface<Treatment> treatments = getAllTreatments();
        boolean found = false;
        
        System.out.println(" ID     Diagnosis           Prescribed Medicine      Qty    Date         Status      Consultation");
        System.out.println("-".repeat(100));
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getConsultation().getConsultationID().equalsIgnoreCase(consultationId)) {
                found = true;
                System.out.printf(" %-6s %-18s %-25s %-6d %-12s %-11s %-12s%n",
                    t.getTreatmentID(),
                    truncateString(t.getDiagnosis(), 18),
                    truncateString(t.getPrescribed(), 25),
                    t.getPrescribedQty(),
                    t.getTreatmentDate(),
                    t.getTreatmentStatus(),
                    t.getConsultation().getConsultationID()
                );
            }
        }
        
        if (!found) {
            System.out.println("                                                                 No treatments found for this consultation.");
        }
        
        System.out.println("-".repeat(100));
        System.out.println("=".repeat(100));
    }
    

    public void displaySortedTreatmentsByDate(boolean ascending) {
        ListInterface<Treatment> treatments = getAllTreatments();
        if (treatments.isEmpty()) {
            System.out.println("No treatments to sort.");
            return;
        }
        
        // create a copy for sorting
        ListInterface<Treatment> sortedTreatments = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            sortedTreatments.add(treatments.getEntry(i));
        }
        
        // actually sort the treatments by date
        int n = sortedTreatments.getNumberOfEntries();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j < n; j++) {
                Treatment t1 = sortedTreatments.getEntry(j);
                Treatment t2 = sortedTreatments.getEntry(j + 1);
                
                // compare dates (YYYY-MM-DD format works with string comparison)
                int cmp = t1.getTreatmentDate().compareTo(t2.getTreatmentDate());
                boolean shouldSwap = (ascending && cmp > 0) || (!ascending && cmp < 0);
                
                if (shouldSwap) {
                    // swap treatments
                    sortedTreatments.replace(j, t2);
                    sortedTreatments.replace(j + 1, t1);
                }
            }
        }
        
        // display the sorted results
        System.out.println("\n" + "=".repeat(120));
        System.out.println(" SORTED TREATMENTS BY DATE");
        System.out.println("=".repeat(120));
        System.out.println("Sort Order: " + (ascending ? "Oldest First" : "Newest First"));
        System.out.println("-".repeat(120));
        
        System.out.println(" ID     Diagnosis           Prescribed Medicine      Qty    Date         Status      Patient         Doctor");
        System.out.println("-".repeat(120));
        
        for (int i = 1; i <= sortedTreatments.getNumberOfEntries(); i++) {
            Treatment t = sortedTreatments.getEntry(i);
            String patientName = t.getConsultation().getPatient() != null ? 
                t.getConsultation().getPatient().getName() : "N/A";
            String doctorName = t.getConsultation().getDoctor() != null ? 
                t.getConsultation().getDoctor().getName() : "N/A";
            
            System.out.printf(" %-6s %-18s %-25s %-6d %-12s %-11s %-16s %-16s%n",
                t.getTreatmentID(),
                truncateString(t.getDiagnosis(), 18),
                truncateString(t.getPrescribed(), 25),
                t.getPrescribedQty(),
                t.getTreatmentDate(),
                t.getTreatmentStatus(),
                truncateString(patientName, 16),
                truncateString(doctorName, 16)
            );
        }
        
        System.out.println("-".repeat(120));
        System.out.println("Total Treatments: " + sortedTreatments.getNumberOfEntries());
        System.out.println("=".repeat(120));
    }
    

    public void displaySortedTreatmentsById() {
        ListInterface<Treatment> treatments = getAllTreatments();
        if (treatments.isEmpty()) {
            System.out.println("No treatments to sort.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(120));
        System.out.println(" SORTED TREATMENTS BY ID");
        System.out.println("=".repeat(120));
        
        System.out.println(" ID     Diagnosis           Prescribed Medicine      Qty    Date         Status      Patient         Doctor");
        System.out.println("-".repeat(120));
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            String patientName = t.getConsultation().getPatient() != null ? 
                t.getConsultation().getPatient().getName() : "N/A";
            String doctorName = t.getConsultation().getDoctor() != null ? 
                t.getConsultation().getDoctor().getName() : "N/A";
            
            System.out.printf(" %-6s %-18s %-25s %-6d %-12s %-11s %-16s %-16s%n",
                t.getTreatmentID(),
                truncateString(t.getDiagnosis(), 18),
                truncateString(t.getPrescribed(), 25),
                t.getPrescribedQty(),
                t.getTreatmentDate(),
                t.getTreatmentStatus(),
                truncateString(patientName, 16),
                truncateString(doctorName, 16)
            );
        }
        
        System.out.println("-".repeat(120));
        System.out.println("Total Treatments: " + treatments.getNumberOfEntries());
        System.out.println("=".repeat(120));
    }
    

    public ListInterface<Treatment> searchTreatmentsByPatient(String patientId) {
        ListInterface<Treatment> treatments = getAllTreatments();
        ListInterface<Treatment> patientTreatments = new CircularDoublyLinkedList<>();
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getConsultation().getPatient() != null && 
                t.getConsultation().getPatient().getPatientId().equalsIgnoreCase(patientId)) {
                patientTreatments.add(t);
            }
        }
        
        return patientTreatments;
    }
    
    public ListInterface<Treatment> searchTreatmentsByDoctor(String doctorId) {
        ListInterface<Treatment> treatments = getAllTreatments();
        ListInterface<Treatment> doctorTreatments = new CircularDoublyLinkedList<>();
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getConsultation().getDoctor() != null && 
                t.getConsultation().getDoctor().getDoctorId().equalsIgnoreCase(doctorId)) {
                doctorTreatments.add(t);
            }
        }
        
        return doctorTreatments;
    }
    
    public ListInterface<Treatment> searchTreatmentsByConsultation(String consultationId) {
        ListInterface<Treatment> treatments = getAllTreatments();
        ListInterface<Treatment> consultationTreatments = new CircularDoublyLinkedList<>();
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getConsultation().getConsultationID().equalsIgnoreCase(consultationId)) {
                consultationTreatments.add(t);
            }
        }
        
        return consultationTreatments;
    }
    
    // filter treatments by status and display results

    public void filterTreatmentsByStatus(String status) {
        ListInterface<Treatment> treatments = getAllTreatments();
        ListInterface<Treatment> filteredTreatments = new CircularDoublyLinkedList<>();
        
        // Normalize the input status to match our constants
        String normalizedStatus = normalizeStatus(status);
        if (normalizedStatus == null) {
            System.out.println("Invalid status provided for filtering");
            return;
        }
        
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getTreatmentStatus().equals(normalizedStatus)) { // ✅ Use exact match with constants
                filteredTreatments.add(t);
            }
        }
        
        displayTreatmentSearchResults(filteredTreatments, "Status", normalizedStatus);
    }
    

    // date selection method for reports

    public String selectDateForReport() {
        java.util.Scanner sc = new java.util.Scanner(System.in);
        
        while (true) {
            System.out.println("\nSelect date for report:");
            System.out.println("1. Today");
            System.out.println("2. Other day");
            System.out.print("Enter your selection (1 or 2): ");
            
            String choice = sc.nextLine().trim();
            
            if (choice.equals("1")) {
                return getCurrentDate(); // ✅ Use consistent date format
            } else if (choice.equals("2")) {
                while (true) {
                    System.out.print("Please enter the date in DD-MM-YYYY format: ");
                    String dateInput = sc.nextLine().trim();
                    
                    if (dateInput.matches("\\d{2}-\\d{2}-\\d{4}")) {
                        return dateInput;
                    } else {
                        System.out.println("Error: Invalid date format. Please use DD-MM-YYYY format.");
                    }
                }
            } else {
                System.out.println("Error: Please enter 1 or 2.");
            }
        }
    }
    

    // helper method to truncate long strings

    private String truncateString(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    private String getCurrentDate() {
        return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public void showStockGuidance(String treatmentId) {
        Treatment treatment = getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return;
        }
        
        if (!STATUS_PENDING.equals(treatment.getTreatmentStatus())) {
            System.out.println("Treatment is not in PENDING status - no stock guidance needed");
            return;
        }
        
        String medicineName = treatment.getPrescribed();
        int requiredQty = treatment.getPrescribedQty();
        
        Pharmacy med = findMedicine(medicineName);
        if (med == null) {
            System.out.println("\n=== STOCK GUIDANCE FOR " + treatmentId + " ===");
            System.out.println("Medicine '" + medicineName + "' not found in pharmacy");
            System.out.println("\nRECOMMENDED ACTIONS:");
            System.out.println("1. Check if medicine name is correct");
            System.out.println("2. Add the medicine to pharmacy inventory");

            return;
        }
        
        if (med.getMedQty() < requiredQty) {
            System.out.println("\n=== STOCK GUIDANCE FOR " + treatmentId + " ===");
            System.out.println("Insufficient stock for medicine: " + medicineName);
            System.out.println("   Required: " + requiredQty + " units");
            System.out.println("   Available: " + med.getMedQty() + " units");
            System.out.println("   Shortage: " + (requiredQty - med.getMedQty()) + " units");
            
            System.out.println("\nRECOMMENDED ACTIONS:");
            System.out.println("1. Restock medicine with at least " + (requiredQty - med.getMedQty()) + " more units");
            System.out.println("2. Wait for restocking before dispensing");

        } else {
            System.out.println("\n=== STOCK STATUS FOR " + treatmentId + " ===");
            System.out.println("Sufficient stock available!");
            System.out.println("   Medicine: " + medicineName);
            System.out.println("   Required: " + requiredQty + " units");
            System.out.println("   Available: " + med.getMedQty() + " units");
            System.out.println("   Remaining after use: " + (med.getMedQty() - requiredQty) + " units");
            
            System.out.println("\nRECOMMENDED ACTION:");
            System.out.println("CAN dispense medicine now");
        }
    }
}
