/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;

/**
 *
 * @author Tan Yu Hang Represent a doctor in the clinic management system
 */
public class Doctor implements Serializable {

    private String doctorID;
    private String name;
    private String email;
    private String specialization;
    private String dutySchedule; // e.g., "Monday-Friday 9AM-5PM"
    private boolean isAvailable;
    private String joinDate;
    private double consultationFee;

    public Doctor() {
    }

    public Doctor(String doctorID, String name, String email, String specialization, String dutySchedule, boolean isAvailable, String joinDate, double consultationFee) {
        this.doctorID = doctorID;
        this.name = name;
        this.email = email;
        this.specialization = specialization;
        this.dutySchedule = dutySchedule;
        this.isAvailable = isAvailable;
        this.joinDate = joinDate;
        this.consultationFee = consultationFee;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDutySchedule() {
        return dutySchedule;
    }

    public void setDutySchedule(String dutySchedule) {
        this.dutySchedule = dutySchedule;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    @Override
    public String toString() {
        return "Doctor {\n"
                + "  Doctor ID        : " + doctorID + "\n"
                + "  Name             : " + name + "\n"
                + "  Email            : " + email + "\n"
                + "  Specialization   : " + specialization + "\n"
                + "  Duty Schedule    : " + dutySchedule + "\n"
                + "  Available        : " + (isAvailable ? "Yes" : "No") + "\n"
                + "  Join Date        : " + joinDate + "\n"
                + "  Consultation Fee : RM " + String.format("%.2f", consultationFee) + "\n"
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
        Doctor doctor = (Doctor) obj;
        return doctorID.equals(doctor.doctorID);
    }
}
