package control;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import boundary.PharmacyMenu;
import entity.Pharmacy;
import entity.DispenseRecord;
import entity.Treatment;
import dao.PharmacyDAO;
import dao.DispenseRecordDAO;
import dao.TreatmentDAO;
import java.time.LocalDate;
import java.util.Comparator;

/**
 *@author HEW MIN FEI
 * Pharmacy Control
 */
public class PharmacyManager {
    
    String lineborder ="=".repeat(50);

    private TreatmentDAO treatmentDAO;

    public PharmacyManager(TreatmentDAO treatmentDAO) {
        this.treatmentDAO=treatmentDAO;
    }

    public boolean addMedicine(String medName, int medQty, double medPrice, LocalDate expDate) {
        String medID = PharmacyDAO.generateNextMedicineId();
        return PharmacyDAO.addMedicine(medID, medName, medQty, medPrice, expDate);
    }

    public boolean updateMedicineInfo(String medID, String medName, int medQty, double medPrice, LocalDate expDate) {
        return PharmacyDAO.updateMedicine(medID, medName, medQty, medPrice, expDate);
    }

    public boolean restockMedicine(String medID, int additionalQty) {
        return PharmacyDAO.restockMedicine(medID, additionalQty);
    }

    public Pharmacy removeMedicine(String medID) {
        return PharmacyDAO.removeMedicine(medID);
    }

    public Pharmacy getMedicine(String medID) {
        return PharmacyDAO.getMedicine(medID);
    }

    public ListInterface<Pharmacy> getAllMedicines() {
        return PharmacyDAO.getAllMedicines();
    }

    public ListInterface<Pharmacy> searchMedicinesByName(String name) {
        ListInterface<Pharmacy> results = new CircularDoublyLinkedList<>();
        ListInterface<Pharmacy> allMedicines = PharmacyDAO.getAllMedicines();
        
        Pharmacy searchKey = new Pharmacy("", name, 0, 0.0, null);
        
        int index = allMedicines.searchByKey(new PharmacyNameComparator(), searchKey);
        if (index != -1) {
            results.add(allMedicines.getEntry(index));
        }
        
        for (int i = 1; i <= allMedicines.getNumberOfEntries(); i++) {
            Pharmacy medicine = allMedicines.getEntry(i);
            if (medicine != null && medicine.getMedName().toLowerCase().contains(name.toLowerCase())) {
                results.add(medicine);
            }
        }
        
        return results;
    }
    
    public ListInterface<Pharmacy> searchMedicinesById(String id) {
        ListInterface<Pharmacy> results = new CircularDoublyLinkedList<>();
        ListInterface<Pharmacy> allMedicines = PharmacyDAO.getAllMedicines();
        
        Pharmacy searchKey = new Pharmacy(id, "", 0, 0.0, null);
        
        int index = allMedicines.searchByKey(new PharmacyIdComparator(), searchKey);
        if (index != -1) {
            results.add(allMedicines.getEntry(index));
        }
        
        return results;
    }
    
    private static class PharmacyNameComparator implements Comparator<Pharmacy> {
        @Override
        public int compare(Pharmacy a, Pharmacy b) {
            return a.getMedName().compareToIgnoreCase(b.getMedName());
        }
    }
    
    private static class PharmacyIdComparator implements Comparator<Pharmacy> {
        @Override
        public int compare(Pharmacy a, Pharmacy b) {
            return a.getMedID().compareToIgnoreCase(b.getMedID());
        }
    }
    public ListInterface<Pharmacy> getExpiredMedicines() {
        return PharmacyDAO.getExpiredMedicines();
    }

    public ListInterface<Pharmacy> getExpiringSoonMedicines(int daysThreshold) {
        return PharmacyDAO.getExpiringSoonMedicines(daysThreshold);
    }

    public ListInterface<Pharmacy> getLowStockMedicines(int threshold) {
        return PharmacyDAO.getLowStockMedicines(threshold);
    }

    public int getTotalMedicines() {
        return PharmacyDAO.getTotalMedicines();
    }

    public double getTotalInventoryValue() {
        return PharmacyDAO.getTotalInventoryValue();
    }

    public int getTotalQuantity() {
        return PharmacyDAO.getTotalQuantity();
    }

    public double getAveragePrice() {
        ListInterface<Pharmacy> medicines = PharmacyDAO.getAllMedicines();
        if (medicines.isEmpty()) {
            return 0.0;
        }

        double totalPrice = 0.0;
        int count = 0;
        for (int i = 1; i <= medicines.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicines.getEntry(i);
            if (medicine != null) {
                totalPrice += medicine.getMedPrice();
                count++;
            }
        }
        return count > 0 ? totalPrice / count : 0.0;
    }

    public ListInterface<Pharmacy> getMedicinesByPriceRange(double minPrice, double maxPrice) {
        ListInterface<Pharmacy> allMedicines = PharmacyDAO.getAllMedicines();
        ListInterface<Pharmacy> result = new adt.CircularDoublyLinkedList<>();
        
        for (int i = 1; i <= allMedicines.getNumberOfEntries(); i++) {
            Pharmacy medicine = allMedicines.getEntry(i);
            if (medicine != null && 
                medicine.getMedPrice() >= minPrice && 
                medicine.getMedPrice() <= maxPrice) {
                result.add(medicine);
            }
        }
        return result;
    }

    public boolean isMedicineExists(String medID) {
        return PharmacyDAO.getMedicine(medID) != null;
    }

    public boolean isMedicineNameExists(String medName) {
        ListInterface<Pharmacy> medicines =searchMedicinesByName(medName);
        return !medicines.isEmpty();
    }

    public ListInterface<Pharmacy> sortMedicines(ListInterface<Pharmacy> medicines, int sortChoice) {
        ListInterface<Pharmacy> sortedList = new adt.CircularDoublyLinkedList<>();
        
        for (int i = 1; i <= medicines.getNumberOfEntries(); i++) {
            sortedList.add(medicines.getEntry(i));
        }
        
        switch (sortChoice) {
            case 1: 
                sortedList.mergeSort((p1, p2) -> p1.getExpDate().compareTo(p2.getExpDate()));
                break;
            case 2: 
                sortedList.mergeSort((p1, p2) -> p2.getExpDate().compareTo(p1.getExpDate()));
                break;
            case 3: 
                sortedList.mergeSort((p1, p2) -> Integer.compare(p1.getMedQty(), p2.getMedQty()));
                break;
            case 4: 
                sortedList.mergeSort((p1, p2) -> Integer.compare(p2.getMedQty(), p1.getMedQty()));
                break;
            case 5: 
            default:
                break;
        }
        
        return sortedList;
    }

    public ListInterface<Treatment> getPendingTreatments() {
        ListInterface<Treatment> allTreatments = treatmentDAO.getAll();
        ListInterface<Treatment> pendingTreatments = new adt.CircularDoublyLinkedList<>();
        
        for (int i = 1; i <= allTreatments.getNumberOfEntries(); i++) {
            Treatment treatment = allTreatments.getEntry(i);
            if (treatment != null && "Pending".equalsIgnoreCase(treatment.getTreatmentStatus())) {
                pendingTreatments.add(treatment);
            }
        }
        return pendingTreatments;
    }

    public boolean dispenseMedicine(String treatmentID, String paymentMethod) {
        Treatment treatment = treatmentDAO.findByID(treatmentID);
        
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentID);
            return false;
        }
        
        if (!"Pending".equalsIgnoreCase(treatment.getTreatmentStatus())) {
            System.out.println("Treatment is not in Pending status");
            return false;
        }
        
        Pharmacy medicine = findMedicineByName(treatment.getPrescribed());
        if (medicine == null) {
            System.out.println("Prescribed medicine not found: " + treatment.getPrescribed());
            return false;
        }
        
        if (medicine.getMedQty() < treatment.getPrescribedQty()) {
            System.out.println("Insufficient stock. Available: " + medicine.getMedQty() + ", Required: " + treatment.getPrescribedQty());
            return false;
        }

        boolean deducted = PharmacyDAO.deductMedicine(medicine.getMedID(), treatment.getPrescribedQty());
        if (!deducted) {
            System.out.println("Failed to deduct stock.");
            return false;
        }
        
        String patientID = treatment.getConsultation().getPatient().getPatientId();
        String patientName = treatment.getConsultation().getPatient().getName();
        
        boolean recordCreated = DispenseRecordDAO.addDispenseRecord(
            patientID, patientName, medicine.getMedName(), medicine.getMedID(),
            treatment.getPrescribedQty(), medicine.getMedPrice(), paymentMethod, treatmentID
        );
        
        if (!recordCreated) {
            System.out.println("Failed to create dispense record.");
            return false;
        }

        boolean updated = treatmentDAO.updateFields(treatmentID, null, null, null, "Completed");
        if (!updated) {
            System.out.println("Failed to update treatment status.");
            return false;
        }
        return true;
    }

    private Pharmacy findMedicineByName(String medicineName) {
        ListInterface<Pharmacy> medicines = searchMedicinesByName(medicineName);
        if (!medicines.isEmpty()) {
            return medicines.getEntry(1);
        }
        return null;
    }

    public ListInterface<DispenseRecord> getAllDispenseRecords() {
        return DispenseRecordDAO.getAllDispenseRecords();
    }

    public ListInterface<DispenseRecord> getDispenseRecordsByPatient(String patientID) {
        return DispenseRecordDAO.getDispenseRecordsByPatient(patientID);
    }

    public DispenseRecord getDispenseRecordById(String dispenseID) {
        return DispenseRecordDAO.getDispenseRecordById(dispenseID);
    }

    public double getTotalRevenue() {
        return DispenseRecordDAO.getTotalRevenue();
    }

    public void printReceipt(DispenseRecord record) {
        System.out.println("\n" + lineborder);
        System.out.println("           PHARMACY RECEIPT");
        System.out.println(lineborder);
        System.out.println("Receipt ID: " + record.getDispenseID());
        System.out.println("Date: " + record.getDispenseDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        System.out.println("Patient: " + record.getPatientName() + " (" + record.getPatientID() + ")");
        System.out.println("Treatment ID: " + record.getTreatmentID());
        System.out.println("Medicine: " + record.getMedicineName());
        System.out.println("Quantity: " + record.getQuantity());
        System.out.println("Unit Price: RM " + String.format("%.2f", record.getUnitPrice()));
        System.out.println("-".repeat(50));
        System.out.println("Total Amount: RM " + String.format("%.2f", record.getTotalPrice()));
        System.out.println("Payment Method: " + record.getPaymentMethod());
        System.out.println(lineborder);
        System.out.println("Thank you for your purchase!");
        System.out.println(lineborder);
    }

    public int getTotalStockInQuantity() {
        return PharmacyDAO.getTotalStockInQuantity();
    }

    public int getTotalStockOutQuantity() {
        return PharmacyDAO.getTotalStockOutQuantity();
    }

    public int getTotalExistingStockQuantity() {
        return PharmacyDAO.getTotalQuantity();
    }
}