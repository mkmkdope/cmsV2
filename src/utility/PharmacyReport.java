package utility;

import adt.ListInterface;
import entity.Pharmacy;
import control.PharmacyManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 *
 * @author HEW MIN FEI
 * Pharmacy Reporting Utility Class
 */
public class PharmacyReport {
    
    private static Scanner scanner = new Scanner(System.in);
    private static PharmacyManager pharmacyManager;
    
    public static void setPharmacyManager(PharmacyManager manager) {
        pharmacyManager = manager;
    }
    
    public static void displayReportMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         PHARMACY REPORTS");
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
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         SUMMARY STOCK REPORT");
        System.out.println("=".repeat(50));
        
        ListInterface<Pharmacy> allMedicines = pharmacyManager.getAllMedicines();
        ListInterface<Pharmacy> expiredMedicines = pharmacyManager.getExpiredMedicines();
        ListInterface<Pharmacy> lowStockMedicines = pharmacyManager.getLowStockMedicines(10);
        
        // Calculate totals
        int totalQuantity = pharmacyManager.getTotalQuantity();
        double totalValue = pharmacyManager.getTotalInventoryValue();
        int expiredCount = expiredMedicines.getNumberOfEntries();
        int lowStockCount = lowStockMedicines.getNumberOfEntries();
        
        System.out.println("INVENTORY SUMMARY");
        System.out.println("-".repeat(30));
        System.out.println("Total Medicines        : " + pharmacyManager.getTotalMedicines());
        System.out.println("Total Quantity (In)    : " + totalQuantity + " units");
        System.out.println("Total Quantity (Out)   : " + expiredCount + " units (expired)");
        System.out.println("Total Existing Stock   : " + (totalQuantity - expiredCount) + " units");
        System.out.println("Total Inventory Value  : RM " + String.format("%.2f", totalValue));
        
        System.out.println("\nSTOCK ALERTS");
        System.out.println("-".repeat(30));
        System.out.println("Expired Medicines      : " + expiredCount);
        System.out.println("Low Stock Medicines    : " + lowStockCount + " (<=10 units)");
        
        // Stock Status Breakdown
        System.out.println("\nSTOCK STATUS BREAKDOWN");
        System.out.println("-".repeat(30));
        
        int highStock = 0, mediumStock = 0, lowStock = 0;
        for (int i = 1; i <= allMedicines.getNumberOfEntries(); i++) {
            Pharmacy medicine = allMedicines.getEntry(i);
            if (medicine != null) {
                int qty = medicine.getMedQty();
                if (qty > 50) highStock++;
                else if (qty > 10) mediumStock++;
                else lowStock++;
            }
        }
        
        System.out.println("High Stock (>50 units)  : " + highStock + " medicines");
        System.out.println("Medium Stock (11-50)    : " + mediumStock + " medicines");
        System.out.println("Low Stock (≤10 units)   : " + lowStock + " medicines");
        
        if (expiredCount > 0) {
            System.out.println("\nEXPIRED MEDICINES:");
            System.out.println("-".repeat(30));
            for (int i = 1; i <= expiredMedicines.getNumberOfEntries(); i++) {
                Pharmacy medicine = expiredMedicines.getEntry(i);
                if (medicine != null) {
                    System.out.println(medicine.getMedName() + " (ID: " + medicine.getMedID() + ") - " + medicine.getMedQty() + " units");
                }
            }
        }
        
        if (lowStockCount > 0) {
            System.out.println("\nLOW STOCK MEDICINES:");
            System.out.println("-".repeat(30));
            for (int i = 1; i <= lowStockMedicines.getNumberOfEntries(); i++) {
                Pharmacy medicine = lowStockMedicines.getEntry(i);
                if (medicine != null) {
                    System.out.println(medicine.getMedName() + " (ID: " + medicine.getMedID() + ") - " + medicine.getMedQty() + " units");
                }
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Report Generated: " + getCurrentDate());
        System.out.println("=".repeat(50));
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    public static void displayExpiringMedicineReport() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         EXPIRING MEDICINE REPORT");
        System.out.println("=".repeat(50));
        
        System.out.print("Enter days threshold for expiring soon (default 30): ");
        String thresholdInput = scanner.nextLine().trim();
        int daysThreshold = 30; // default value
        
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
        
        ListInterface<Pharmacy> allMedicines = pharmacyManager.getAllMedicines();
        ListInterface<Pharmacy> expiredMedicines = pharmacyManager.getExpiredMedicines();
        ListInterface<Pharmacy> expiringSoonMedicines = pharmacyManager.getExpiringSoonMedicines(daysThreshold);
        
        System.out.println("\nEXPIRY STATUS REPORT");
        System.out.println("-".repeat(30));
        System.out.println("Total Medicines         : " + allMedicines.getNumberOfEntries());
        System.out.println("Expired Medicines       : " + expiredMedicines.getNumberOfEntries());
        System.out.println("Expiring Soon (<=" + daysThreshold + " days) : " + expiringSoonMedicines.getNumberOfEntries());
        System.out.println("Valid Medicines         : " + (allMedicines.getNumberOfEntries() - expiredMedicines.getNumberOfEntries() - expiringSoonMedicines.getNumberOfEntries()));
        
        if (expiredMedicines.getNumberOfEntries() > 0) {
            System.out.println("\nEXPIRED MEDICINES:");
            System.out.println("-".repeat(30));
            for (int i = 1; i <= expiredMedicines.getNumberOfEntries(); i++) {
                Pharmacy medicine = expiredMedicines.getEntry(i);
                if (medicine != null) {
                    long daysExpired = LocalDate.now().toEpochDay() - medicine.getExpDate().toEpochDay();
                    System.out.println(medicine.getMedName() + " (ID: " + medicine.getMedID() + ")");
                    System.out.println("  Expired: " + medicine.getExpDate() + " (" + daysExpired + " days ago)");
                    System.out.println("  Quantity: " + medicine.getMedQty() + " units");
                }
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Report Generated: " + getCurrentDate());
        System.out.println("=".repeat(50));
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
}