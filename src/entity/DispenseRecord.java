package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a medicine dispense record in the pharmacy system
 */
public class DispenseRecord {
    private String dispenseID;
    private String patientID;
    private String patientName;
    private String medicineName;
    private String medicineID;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String paymentMethod;
    private LocalDateTime dispenseDateTime;
    private String treatmentID;

    public DispenseRecord() {
    }

    public DispenseRecord(String dispenseID, String patientID, String patientName, 
                         String medicineName, String medicineID, int quantity, 
                         double unitPrice, String paymentMethod, String treatmentID) {
        this.dispenseID = dispenseID;
        this.patientID = patientID;
        this.patientName = patientName;
        this.medicineName = medicineName;
        this.medicineID = medicineID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
        this.paymentMethod = paymentMethod;
        this.dispenseDateTime = LocalDateTime.now();
        this.treatmentID = treatmentID;
    }

    public String getDispenseID() {
        return dispenseID;
    }

    public void setDispenseID(String dispenseID) {
        this.dispenseID = dispenseID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineID() {
        return medicineID;
    }

    public void setMedicineID(String medicineID) {
        this.medicineID = medicineID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = quantity * unitPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getDispenseDateTime() {
        return dispenseDateTime;
    }

    public void setDispenseDateTime(LocalDateTime dispenseDateTime) {
        this.dispenseDateTime = dispenseDateTime;
    }

    public String getTreatmentID() {
        return treatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        this.treatmentID = treatmentID;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return String.format(
            "Dispense ID: %s\n" +
            "Patient: %s (%s)\n" +
            "Medicine: %s (%s)\n" +
            "Quantity: %d\n" +
            "Unit Price: RM %.2f\n" +
            "Total Price: RM %.2f\n" +
            "Payment Method: %s\n" +
            "Dispense Date: %s\n" +
            "Treatment ID: %s",
            dispenseID, patientName, patientID, medicineName, medicineID,
            quantity, unitPrice, totalPrice, paymentMethod,
            dispenseDateTime.format(formatter), treatmentID
        );
    }
}