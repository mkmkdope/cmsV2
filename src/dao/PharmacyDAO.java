package dao;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import entity.Pharmacy;
import java.time.LocalDate;

/**
 *
 * @author HEW MIN FEI
 * PharmacyDAO
 */
public class PharmacyDAO {
    
    private static ListInterface<Pharmacy> medicineList;
    private static int nextMedicineId = 1006;
    
    private static int totalStockIn = 0;
    private static int totalStockOut = 0;
    
    static {
        medicineList = new CircularDoublyLinkedList<>();
        initializeSampleData();
    }
    
    private static void initializeSampleData() {
        addMedicine("M1001", "Paracetamol 500mg", 100, 15.50, LocalDate.of(2027, 12, 31));
        addMedicine("M1002", "Amoxicillin 250mg", 50, 25.00, LocalDate.of(2028, 10, 15));
        addMedicine("M1003", "Ibuprofen 400mg", 75, 18.75, LocalDate.of(2025, 9, 20));
        addMedicine("M1004", "Omeprazole 20mg", 30, 45.00, LocalDate.of(2024, 8, 10));
        addMedicine("M1005", "Cetirizine 10mg", 60, 22.50, LocalDate.of(2025, 1, 15));
        
        totalStockIn = 100 + 50 + 75 + 30 + 60;
    }
    
    public static boolean addMedicine(String medID, String medName, int medQty, double medPrice, LocalDate expDate) {
        if (medName == null || medName.trim().isEmpty() || medQty < 0 || medPrice < 0 || expDate == null) {
            return false;
        }
        
        Pharmacy medicine = new Pharmacy(medID, medName.trim(), medQty, medPrice, expDate);
        boolean added = medicineList.add(medicine);
        if (added) {
            totalStockIn += medQty; 
        }
        return added;
    }
    
    public static boolean updateMedicine(String medID, String medName, int medQty, double medPrice, LocalDate expDate) {
        int position = findMedicinePosition(medID);
        if (position == -1) {
            return false;
        }
        
        Pharmacy oldMedicine = medicineList.getEntry(position);
        int oldQty = oldMedicine != null ? oldMedicine.getMedQty() : 0;
        
        Pharmacy updatedMedicine = new Pharmacy(medID, medName, medQty, medPrice, expDate);
        boolean updated = medicineList.replace(position, updatedMedicine);
        
        if (updated && oldMedicine != null) {
            if (medQty > oldQty) {
                totalStockIn += (medQty - oldQty);
            } else if (medQty < oldQty) {
                totalStockOut += (oldQty - medQty);
            }
        }
        
        return updated;
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
            boolean updated = medicineList.replace(position, medicine);
            if (updated) {
                totalStockIn += additionalQty; 
            }
            return updated;
        }
        return false;
    }

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
        boolean updated = medicineList.replace(position, medicine);
        if (updated) {
            totalStockOut += deductQty; 
        }
        return updated;
    }
    
    public static Pharmacy removeMedicine(String medID) {
        int position = findMedicinePosition(medID);
        if (position == -1) {
            return null;
        }
        Pharmacy removed = medicineList.remove(position);
        if (removed != null) {
            totalStockOut += removed.getMedQty(); 
        }
        return removed;
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
   
    
    public static ListInterface<Pharmacy> getExpiredMedicines() {
        ListInterface<Pharmacy> result = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null && medicine.isExpired()) {
                result.add(medicine);
            }
        }
        return result;
    }
    
    public static ListInterface<Pharmacy> getExpiringSoonMedicines(int daysThreshold) {
        ListInterface<Pharmacy> result = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= medicineList.getNumberOfEntries(); i++) {
            Pharmacy medicine = medicineList.getEntry(i);
            if (medicine != null && medicine.isExpiringSoon(daysThreshold)) {
                result.add(medicine);
            }
        }
        return result;
    }
    
    public static ListInterface<Pharmacy> getLowStockMedicines(int threshold) {
        ListInterface<Pharmacy> result = new CircularDoublyLinkedList<>();
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

    public static int getTotalStockInQuantity() {
        return totalStockIn;
    }

    public static int getTotalStockOutQuantity() {
        return totalStockOut;
    }

    private static int getDispensedQuantityForMedicine(String medID) {
        return 0; 
    }
}