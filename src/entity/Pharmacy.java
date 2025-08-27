package entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author HEW MIN FEI
 * Pharmacy Entity
 */
public class Pharmacy implements Serializable {

    private String medID;
    private String medName;
    private int medQty;
    private double medPrice;
    private LocalDate expDate;

    public Pharmacy() {
    }

    public Pharmacy(String medID, String medName, int medQty, double medPrice, LocalDate expDate) {
        this.medID = medID;
        this.medName = medName;
        this.medQty = medQty;
        this.medPrice = medPrice;
        this.expDate = expDate;
    }

    public String getMedID() {
        return medID;
    }

    public void setMedID(String medID) {
        this.medID = medID;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public int getMedQty() {
        return medQty;
    }

    public void setMedQty(int medQty) {
        this.medQty = medQty;
    }

    public double getMedPrice() {
        return medPrice;
    }

    public void setMedPrice(double medPrice) {
        this.medPrice = medPrice;
    }

    public LocalDate getExpDate() {
        return expDate;
    }

    public void setExpDate(LocalDate expDate) {
        this.expDate = expDate;
    }

    @Override
    public String toString() {
        return "Medicine {\n"
                + "  Medicine ID      : " + medID + "\n"
                + "  Medicine Name    : " + medName + "\n"
                + "  Quantity         : " + medQty + " units\n"
                + "  Price            : RM " + String.format("%.2f", medPrice) + "\n"
                + "  Expiry Date      : " + expDate + "\n"
                + "  Status           : " + (isExpired() ? "EXPIRED" : "Valid") + "\n"
                + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Pharmacy pharmacy = (Pharmacy) obj;
        return medID.equals(pharmacy.medID);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expDate);
    }

    public boolean isExpiringSoon(int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return expDate.isBefore(thresholdDate) && !isExpired();
    }
}