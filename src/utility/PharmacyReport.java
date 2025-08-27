package utility;

import adt.ListInterface;
import adt.CircularDoublyLinkedList;
import entity.Pharmacy;
import entity.DispenseRecord;
import control.PharmacyManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Comparator;

/**
 * @author HEW MIN FEI
 * Pharmacy Report Utility
 */
public class PharmacyReport {
    
    private static Scanner scanner = new Scanner(System.in);
    private static PharmacyManager pharmacyManager;
    
    public static void setPharmacyManager(PharmacyManager manager) {
        pharmacyManager = manager;
    }
    
    public static void displayReportMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         PHARMACY MANAGEMENT REPORTS");
        System.out.println("=".repeat(50));
        System.out.println("1. Summary Stock Report");
        System.out.println("2. Expiring Medicine Report");
        System.out.println("3. Back to Pharmacy Menu");
        System.out.println("=".repeat(50));
        System.out.print("Enter Choice: ");
    }
    
    public static int getReportMenuChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= 3) {
                return choice;
            } else {
                System.out.println("Invalid choice! Please enter a number between 1-3.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number.");
            return -1;
        }
    }
    
    public static void processReportMenuChoice(int choice) {
        switch (choice) {
            case 1:
                displaySummaryStockReport();
                break;
            case 2:
                displayExpiringMedicineReport();
                break;
            case 3:
                System.out.println("Returning to Pharmacy Menu...");
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }
    
    public static void displaySummaryStockReport() {
        System.out.println("\n" + "Report Generated: " + getCurrentDate()+"                                       "+getCurrentTime());
        System.out.println("=".repeat(90));
        System.out.println("               TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY              ");
        System.out.println("                     CLINIC MANAGEMENT SYSTEM - PHARMACY MANAGEMENT             ");
        System.out.println("=".repeat(90));
        System.out.println("                               SUMMARY STOCK REPORT");
        System.out.println("+".repeat(90));
        
        ListInterface<Pharmacy> allMedicines = pharmacyManager.getAllMedicines();
        ListInterface<DispenseRecord> dispenseHistory = pharmacyManager.getAllDispenseRecords();

        CircularDoublyLinkedList<StockOutRecord> stockOutList = new CircularDoublyLinkedList<>();
        CircularDoublyLinkedList<StockInRecord> stockInList = new CircularDoublyLinkedList<>();
        CircularDoublyLinkedList<Pharmacy> lowStockList = new CircularDoublyLinkedList<>();

        for (int i = 1; i <= dispenseHistory.getNumberOfEntries(); i++) {
            DispenseRecord r = dispenseHistory.getEntry(i);
            if (r == null) continue;

            // Check if medicine already exists in stock-out list
            boolean found = false;
            for (int j = 1; j <= stockOutList.getNumberOfEntries(); j++) {
                StockOutRecord existing = stockOutList.getEntry(j);
                if (existing.medicineID.equals(r.getMedicineID())) {
                    existing.quantity += r.getQuantity();
                    existing.totalValue += r.getTotalPrice();
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                StockOutRecord newRecord = new StockOutRecord(
                    r.getMedicineID(), r.getMedicineName(), r.getQuantity(), r.getTotalPrice()
                );
                stockOutList.addWithPriority(new StockOutComparator(), newRecord);
            }
        }

        for (int i = 1; i <= allMedicines.getNumberOfEntries(); i++) {
            Pharmacy m = allMedicines.getEntry(i);
            if (m == null) continue;

            int dispensedQty = 0;
            for (int j = 1; j <= stockOutList.getNumberOfEntries(); j++) {
                StockOutRecord outRecord = stockOutList.getEntry(j);
                if (outRecord.medicineID.equals(m.getMedID())) {
                    dispensedQty = outRecord.quantity;
                    break;
                }
            }
            
            int inQtyApprox = m.getMedQty() + dispensedQty;
            double inValApprox = inQtyApprox * m.getMedPrice();
            
            StockInRecord inRecord = new StockInRecord(
                m.getMedID(), m.getMedName(), inQtyApprox, inValApprox
            );
            stockInList.addWithPriority(new StockInComparator(), inRecord);
            
            if (m.getMedQty() <= 10) {
                lowStockList.addWithPriority(new LowStockComparator(), m);
            }
        }

        int totalOutQty = 0;
        double totalOutValue = 0.0;
        for (int i = 1; i <= stockOutList.getNumberOfEntries(); i++) {
            StockOutRecord record = stockOutList.getEntry(i);
            totalOutQty += record.quantity;
            totalOutValue += record.totalValue;
        }

        int totalInQty = 0;
        double totalInValue = 0.0;
        for (int i = 1; i <= stockInList.getNumberOfEntries(); i++) {
            StockInRecord record = stockInList.getEntry(i);
            totalInQty += record.quantity;
            totalInValue += record.totalValue;
        }

        int existingQty = pharmacyManager.getTotalQuantity();
        double existingValue = pharmacyManager.getTotalInventoryValue();

        System.out.printf("%-18s %-15s %-20s%n", "Category", "Quantity", "Total Value (RM)");
        System.out.println("-".repeat(90));
        System.out.printf("%-18s %-15d %-20.2f%n", "Stock In", totalInQty, totalInValue);
        System.out.printf("%-18s %-15d %-20.2f%n", "Stock Out", totalOutQty, totalOutValue);
        System.out.printf("%-18s %-15d %-20.2f%n", "Existing", existingQty, existingValue);
        System.out.println("-".repeat(90));

        System.out.println("\nSTOCK IN DETAILS (Highest quantity first)");
        System.out.println("-".repeat(90));
        if (stockInList.isEmpty()) {
            System.out.println("No medicines in inventory.");
        } else {
            System.out.printf("%-10s %-28s %-10s %-15s%n", "Med ID", "Medicine Name", "Qty In", "Total (RM)");
            for (int i = 1; i <= stockInList.getNumberOfEntries(); i++) {
                StockInRecord record = stockInList.getEntry(i);
                System.out.printf("%-10s %-28s %-10d %-15.2f%n",
                        record.medicineID, record.medicineName, record.quantity, record.totalValue);
            }
        }
        
        System.out.println("\nSTOCK OUT DETAILS");
        System.out.println("-".repeat(90));
        if (stockOutList.isEmpty()) {
            System.out.println("No stock out records found.");
        } else {
            System.out.printf("%-10s %-28s %-10s %-15s%n", "Med ID", "Medicine Name", "Qty", "Total (RM)");
            for (int i = 1; i <= stockOutList.getNumberOfEntries(); i++) {
                StockOutRecord record = stockOutList.getEntry(i);
                System.out.printf("%-10s %-28s %-10d %-15.2f%n", 
                    record.medicineID, record.medicineName, record.quantity, record.totalValue);
            }
        }

        System.out.println("\nLOW STOCK DETAILS (<= 10 units)");
        System.out.println("-".repeat(90));
        if (lowStockList.isEmpty()) {
            System.out.println("No medicines currently low on stock.");
        } else {
            System.out.printf("%-10s %-28s %-10s %-15s%n", "Med ID", "Medicine Name", "Qty", "Value (RM)");
            for (int i = 1; i <= lowStockList.getNumberOfEntries(); i++) {
                Pharmacy medicine = lowStockList.getEntry(i);
                System.out.printf("%-10s %-28s %-10d %-15.2f%n",
                        medicine.getMedID(), medicine.getMedName(), medicine.getMedQty(), 
                        medicine.getMedQty() * medicine.getMedPrice());
            }
        }

        System.out.println("\n" + "=".repeat(90));
        System.out.println("                               END OF THE REPORT");
        System.out.println("=".repeat(90));
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    public static void displayExpiringMedicineReport() {
        System.out.println("\n" + "Report Generated: " + getCurrentDate()+"                                       "+getCurrentTime());
        System.out.println("=".repeat(90));
        System.out.println("               TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY              ");
        System.out.println("                     CLINIC MANAGEMENT SYSTEM - PHARMACY MANAGEMENT             ");
        System.out.println("=".repeat(90));
        System.out.println("                               EXPIRING MEDICINE REPORT");
        System.out.println("+".repeat(90));
        
        System.out.print("Enter days threshold for expiring soon (default 30): ");
        String thresholdInput = scanner.nextLine().trim();
        int daysThreshold = 30;
        
        if (!thresholdInput.isEmpty()) {
            try {
                daysThreshold = Integer.parseInt(thresholdInput);
                if (daysThreshold < 0) {
                    System.out.println("Threshold cannot be negative! Using default value of 30 days.");
                    daysThreshold = 30;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid threshold! Using default value of 30 days.");
                daysThreshold = 30;
            }
        }
        
        ListInterface<Pharmacy> expiredMedicines = pharmacyManager.getExpiredMedicines();
        ListInterface<Pharmacy> expiringSoonMedicines = pharmacyManager.getExpiringSoonMedicines(daysThreshold);
        
        CircularDoublyLinkedList<Pharmacy> sortedExpiredList = new CircularDoublyLinkedList<>();
        CircularDoublyLinkedList<Pharmacy> sortedExpiringSoonList = new CircularDoublyLinkedList<>();

            for (int i = 1; i <= expiredMedicines.getNumberOfEntries(); i++) {
                Pharmacy medicine = expiredMedicines.getEntry(i);
            if (medicine != null) {
                // Use addWithPriority from ADT - longest expired first
                sortedExpiredList.addWithPriority(new ExpiredDaysComparator(), medicine);
            }
        }

        for (int i = 1; i <= expiringSoonMedicines.getNumberOfEntries(); i++) {
            Pharmacy medicine = expiringSoonMedicines.getEntry(i);
            if (medicine != null) {
                // Use addWithPriority from ADT - longest days until expiry first
                sortedExpiringSoonList.addWithPriority(new ExpiringSoonComparator(), medicine);
            }
        }

        System.out.println("\nEXPIRY STATUS");
        System.out.println("-".repeat(90));
        System.out.println("Expired Medicines       : " + sortedExpiredList.getNumberOfEntries());
        System.out.println("Expiring Soon (<= " + daysThreshold + " days) : " + sortedExpiringSoonList.getNumberOfEntries());

        if (sortedExpiredList.getNumberOfEntries() > 0) {
            System.out.println("\nEXPIRED MEDICINES :");
            System.out.println("-".repeat(90));
            for (int i = 1; i <= sortedExpiredList.getNumberOfEntries(); i++) {
                Pharmacy medicine = sortedExpiredList.getEntry(i);
                if (medicine != null) {
                    long daysExpired = LocalDate.now().toEpochDay() - medicine.getExpDate().toEpochDay();
                    System.out.println(medicine.getMedName() + " (ID: " + medicine.getMedID() + ")");
                    System.out.println("  Expired: " + medicine.getExpDate() + " (" + daysExpired + " days ago)");
                    System.out.println("  Quantity: " + medicine.getMedQty() + " units");
                }
            }
        }
        
        System.out.println("\nEXPIRING SOON DETAILS  :");
        System.out.println("-".repeat(90));
        if (sortedExpiringSoonList.isEmpty()) {
            System.out.println("No medicines expiring within " + daysThreshold + " days.");
        } else {
            System.out.printf("%-10s %-25s %-12s %-12s%n", "Med ID", "Medicine Name", "Qty", "Expiry Date");
            for (int i = 1; i <= sortedExpiringSoonList.getNumberOfEntries(); i++) {
                Pharmacy m = sortedExpiringSoonList.getEntry(i);
                if (m != null) {
                    System.out.printf("%-10s %-25s %-12d %-12s%n",
                            m.getMedID(), m.getMedName(), m.getMedQty(), m.getExpDate());
                }
            }
        }

        System.out.println("\n" + "=".repeat(90));
        System.out.println("                               END OF THE REPORT");
        System.out.println("=".repeat(90));
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    private static String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    public static void runReportMenu() {
        int choice;
        do {
            displayReportMenu();
            choice = getReportMenuChoice();
            if (choice != -1) {
                processReportMenuChoice(choice);
            }
        } while (choice != 3);
    }

    // Helper classes for sorting
    private static class StockOutRecord {
        String medicineID;
        String medicineName;
        int quantity;
        double totalValue;
        
        StockOutRecord(String id, String name, int qty, double value) {
            this.medicineID = id;
            this.medicineName = name;
            this.quantity = qty;
            this.totalValue = value;
        }
    }
    
    private static class StockInRecord {
        String medicineID;
        String medicineName;
        int quantity;
        double totalValue;
        
        StockInRecord(String id, String name, int qty, double value) {
            this.medicineID = id;
            this.medicineName = name;
            this.quantity = qty;
            this.totalValue = value;
        }
    }
    
    private static class StockOutComparator implements Comparator<StockOutRecord> {
        @Override
        public int compare(StockOutRecord a, StockOutRecord b) {
            return Integer.compare(b.quantity, a.quantity);
        }
    }
    
    private static class StockInComparator implements Comparator<StockInRecord> {
        @Override
        public int compare(StockInRecord a, StockInRecord b) {
            return Integer.compare(b.quantity, a.quantity); 
        }
    }
    
    private static class LowStockComparator implements Comparator<Pharmacy> {
        @Override
        public int compare(Pharmacy a, Pharmacy b) {
            return Integer.compare(a.getMedQty(), b.getMedQty()); 
        }
    }
    
    private static class ExpiredDaysComparator implements Comparator<Pharmacy> {
        @Override
        public int compare(Pharmacy a, Pharmacy b) {
            long daysA = LocalDate.now().toEpochDay() - a.getExpDate().toEpochDay();
            long daysB = LocalDate.now().toEpochDay() - b.getExpDate().toEpochDay();
            return Long.compare(daysB, daysA); 
        }
    }
    
    private static class ExpiringSoonComparator implements Comparator<Pharmacy> {

        @Override
        public int compare(Pharmacy a, Pharmacy b) {
            long daysA = a.getExpDate().toEpochDay() - LocalDate.now().toEpochDay();
            long daysB = b.getExpDate().toEpochDay() - LocalDate.now().toEpochDay();
            return Long.compare(daysA, daysB); 
        }
    }
}