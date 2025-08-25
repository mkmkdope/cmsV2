package dao;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import entity.DispenseRecord;
import java.time.LocalDateTime;

/**
 * Data Access Object for DispenseRecord operations
 */
public class DispenseRecordDAO {
    
    private static ListInterface<DispenseRecord> dispenseRecords;
    private static int nextDispenseId = 1001;
    
    static {
        dispenseRecords = new CircularDoublyLinkedList<>();
        initializeSampleData();
    }
    
    private static void initializeSampleData() {
        addDispenseRecord("P001", "John Doe", "Paracetamol 500mg", "M1001", 2, 15.50, "Cash", "T0001");
        addDispenseRecord("P002", "Jane Smith", "Amoxicillin 250mg", "M1002", 1, 25.00, "Credit Card", "T0002");
        addDispenseRecord("P003", "Mike Johnson", "Ibuprofen 400mg", "M1003", 3, 18.75, "E-Wallet", "T0003");
        addDispenseRecord("P001", "John Doe", "Cetirizine 10mg", "M1005", 1, 22.50, "Cash", "T0004");
        addDispenseRecord("P004", "Sarah Wilson", "Omeprazole 20mg", "M1004", 1, 45.00, "Credit Card", "T0005");
    }
    
    public static boolean addDispenseRecord(String patientID, String patientName, 
                                          String medicineName, String medicineID, 
                                          int quantity, double unitPrice, 
                                          String paymentMethod, String treatmentID) {
        String dispenseID = generateNextDispenseId();
        DispenseRecord record = new DispenseRecord(dispenseID, patientID, patientName, 
                                                  medicineName, medicineID, quantity, 
                                                  unitPrice, paymentMethod, treatmentID);
        return dispenseRecords.add(record);
    }
    
    public static ListInterface<DispenseRecord> getAllDispenseRecords() {
        return dispenseRecords;
    }
    
    public static ListInterface<DispenseRecord> getDispenseRecordsByPatient(String patientID) {
        CircularDoublyLinkedList<DispenseRecord> result = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= dispenseRecords.getNumberOfEntries(); i++) {
            DispenseRecord record = dispenseRecords.getEntry(i);
            if (record != null && record.getPatientID().equals(patientID)) {
                result.add(record);
            }
        }
        return result;
    }
    
    public static DispenseRecord getDispenseRecordById(String dispenseID) {
        for (int i = 1; i <= dispenseRecords.getNumberOfEntries(); i++) {
            DispenseRecord record = dispenseRecords.getEntry(i);
            if (record != null && record.getDispenseID().equals(dispenseID)) {
                return record;
            }
        }
        return null;
    }
    
    public static String generateNextDispenseId() {
        return "D" + nextDispenseId++;
    }
    
    public static int getTotalDispenseRecords() {
        return dispenseRecords.getNumberOfEntries();
    }
    
    public static double getTotalRevenue() {
        double totalRevenue = 0.0;
        for (int i = 1; i <= dispenseRecords.getNumberOfEntries(); i++) {
            DispenseRecord record = dispenseRecords.getEntry(i);
            if (record != null) {
                totalRevenue += record.getTotalPrice();
            }
        }
        return totalRevenue;
    }
}