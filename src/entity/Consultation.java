/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author USER
 */
public class Consultation {
    //private static int counter = 0;
    private String consultationID;//auto generate
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;//consultation time
    private String reason;//Fever,cough
    private String status; //Scheduled(default), Completed, Cancelled, No-show
    private boolean followUpFlag;
    private String previousConsultationId; // can be null, for follow-up use

    
    public Consultation(){
        
    }
    
    public Consultation(String consultationID){
        this.consultationID = consultationID;
    }
    
    public Consultation(String consultationID, Patient patient, Doctor doctor, LocalDateTime dateTime, String reason) {
        this.consultationID = consultationID;
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.reason = reason;
        this.status = "Scheduled";
    }

    
//    public int getCounter() {
//        return counter;
//    }

    public String getConsultationID() {
        return consultationID;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public boolean isFollowUpFlag() {
        return followUpFlag;
    }

    public String getPreviousConsultationId() {
        return previousConsultationId;
    }

    public void setConsultationID(String consultationID) {
        this.consultationID = consultationID;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFollowUpFlag(boolean followUpFlag) {
        this.followUpFlag = followUpFlag;
    }

    public void setPreviousConsultationId(String previousConsultationId) {
        this.previousConsultationId = previousConsultationId;
    }
    
    //used by doctor
    public void complete(Boolean followUpFlag, String previousConsultationId) {
        this.status = "Completed";
        this.followUpFlag=followUpFlag;
        this.previousConsultationId=previousConsultationId;
    }
    
    @Override
    public String toString() {
    return String.format(" %-5s  %-25s  %-35s  %-16s  %-40s %-10s  %-3s  %-25s ",
            consultationID,
            patient.getName(),
            doctor.getName(),
            dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            reason,
            status != null ? status : "N/A",
            followUpFlag ? "Yes" : "No",
            previousConsultationId != null ? previousConsultationId : "-"
        );
    }

}
