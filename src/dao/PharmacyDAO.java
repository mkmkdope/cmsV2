package dao;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import entity.Pharmacy;
import java.time.LocalDate;

/**
 *
 * @author HEW MIN FEI
 * Data Access Object for Pharmacy operations
 */
public class PharmacyDAO {
    
    private static ListInterface<Pharmacy> medicineList;
    private static int nextMedicineId = 1006;
    
    static {
        medicineList = new CircularDoublyLinkedList<>();
        initializeSampleData();
    }
    
    private static void initializeSampleData() {
        //sample medicines
        addMedicine("M1001", "Paracetamol 500mg", 100, 15.50, LocalDate.of(2027, 12, 31));
        addMedicine("M1002", "Amoxicillin 250mg", 50, 25.00, LocalDate.of(2028, 10, 15));
        addMedicine("M1003", "Ibuprofen 400mg", 75, 18.75, LocalDate.of(2025, 3, 20));
        addMedicine("M1004", "Omeprazole 20mg", 30, 45.00, LocalDate.of(2024, 8, 10));
        addMedicine("M1005", "Cetirizine 10mg", 60, 22.50, LocalDate.of(2025, 1, 15));
    }
    
    public static boolean addMedicine(String medID, String medName, int medQty, double medPrice, LocalDate expDate) {
        if (medName == null || medName.trim().isEmpty() || medQty < 0 || medPrice < 0 || expDate == null) {
            return false;
        }
        
        Pharmacy medicine = new Pharmacy(medID, medName.trim(), medQty, medPrice, expDate);
        return medicineList.add(medicine);
    }
    
    public static boolean updateMedicine(String medID, String medName, int medQty, double medPrice, LocalDate expDate) {
        int position = findMedicinePosition(medID);
        if (position == -1) {
            return false;
        }
        
        Pharmacy updatedMedicine = new Pharmacy(medID, medName, medQty, medPrice, expDate);
        return medicineList.replace(position, updatedMedicine);
    }
    
    public static boolean restockMedicine(String medID, int additionalQty) {
        if (additionalQty <= 0) {
            return false;
        }
        
        int position = findMedicinePosition(medID);
        if (position == -1) {
            return false;
        }
        
        Pharmacy medicine = medicineList.getEntry(position);
        if (medicine != null) {
            medicine.setMedQty(medicine.getMedQty() + additionalQty);
            return medicineList.replace(position, medicine);
        }
        return false;
    }

    // New: deduct stock safely for dispensing
    public static boolean deductMedicine(String medID, int deductQty) {
        if (deductQty <= 0) {
            return false;
        }
        int position = findMedicinePosition(medID);
        if (position == -1) {
            return false;
        }
        Pharmacy medicine = medicineList.getEntry(position);
        if (medicine == null) {
            return false;
        }
        if (medicine.getMedQty() < deductQty) {
            return false;
        }
        medicine.setMedQty(medicine.getMedQty() - deductQty);
        return medicineList.replace(position, medicine);
    }
    
    public static Pharmacy removeMedicine(String medID) {
        int position = findMedicinePosition(medID);
        if (position == -1) {
            return null;
        }
        return medicineList.remove(position);
    }
    
    public static Pharmacy getMedicine(String medID) {
        int position = findMedicinePosition(medID);
        if (position == -1) {
            return null;
        }
        return medicineList.getEntry(position);
    }
    
    public static ListInterface<Pharmacy> getAllMedicines() {
        return medicineList;
    }
    
    public static ListInterface<Pharmacy> searchMedicinesByName(String medName) {
        CircularDoublyLinkedList<Pharmacy> result = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null && 
                medicine.getMedName().toLowerCase().contains(medName.toLowerCase())) {
                result.add(medicine);
            }
        }
        return result;
    }
    
    public static ListInterface<Pharmacy> searchMedicinesById(String medID) {
        CircularDoublyLinkedList<Pharmacy> result = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null && 
                medicine.getMedID().toLowerCase().contains(medID.toLowerCase())) {
                result.add(medicine);
            }
        }
        return result;
    }
    
    public static ListInterface<Pharmacy> getExpiredMedicines() {
        CircularDoublyLinkedList<Pharmacy> result = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null && medicine.isExpired()) {
                result.add(medicine);
            }
        }
        return result;
    }
    
    public static ListInterface<Pharmacy> getExpiringSoonMedicines(int daysThreshold) {
        CircularDoublyLinkedList<Pharmacy> result = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null && medicine.isExpiringSoon(daysThreshold)) {
                result.add(medicine);
            }
        }
        return result;
    }
    
    public static ListInterface<Pharmacy> getLowStockMedicines(int threshold) {
        CircularDoublyLinkedList<Pharmacy> result = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null && medicine.getMedQty() <= threshold) {
                result.add(medicine);
            }
        }
        return result;
    }
    
    private static int findMedicinePosition(String medID) {
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null && medicine.getMedID().equals(medID)) {
                return i;
            }
        }
        return -1;
    }
    
    public static String generateNextMedicineId() {
        return "M" + nextMedicineId++;
    }
    
    public static int getTotalMedicines() {
        return medicineList.getNumberOfEntries();
    }
    
    public static double getTotalInventoryValue() {
        double totalValue = 0.0;
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null) {
                totalValue += medicine.getMedPrice() * medicine.getMedQty();
            }
        }
        return totalValue;
    }
    
    public static int getTotalQuantity() {
        int totalQty = 0;
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null) {
                totalQty += medicine.getMedQty();
            }
        }
        return totalQty;
    }
}