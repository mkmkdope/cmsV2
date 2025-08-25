/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;


/**
 *
 * @author Asus
 */
public class Treatment{
    private String treatmentID;
    private Consultation consultation;
    private String diagnosis;           // flu
    private String prescribed;          // paracetamol
    private int prescribedQty;
    private String treatmentDate;       // 2025-08-08
    private String treatmentStatus;     // Pending or Completed(the Search treatment by Patient), completed they is no need any further action required for that treatment entry.

    public Treatment(){        
    }
    
    public Treatment(String treatmentID, Consultation consultation, String diagnosis, String prescribed, int prescribedQty, String treatmentDate, String treatmentStatus){
        this.treatmentID = treatmentID;
        this.consultation = consultation;
        this.diagnosis = diagnosis;
        this.prescribed = prescribed;
        this.prescribedQty = prescribedQty;
        this.treatmentDate = treatmentDate;
        this.treatmentStatus = treatmentStatus;
    }
    
    public String getTreatmentID(){
        return treatmentID;
    }
    
    public void setTreatmentID(String treatmentID){
        this.treatmentID = treatmentID;
    }
    
    public Consultation getConsultation(){
        return consultation;
    }

    public void setConsultation(Consultation consultation){
        this.consultation = consultation;
    }
    
    public String getDiagnosis(){
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis){
        this.diagnosis = diagnosis;
    }
    
    public String getPrescribed(){
        return prescribed;
    }
    
    public void setPrescribed(String prescribed){
        this.prescribed = prescribed;
    }
    
    public int getPrescribedQty(){
        return prescribedQty;
    }
    
    public void setPrescribedQty(int prescribedQty){
        this.prescribedQty = prescribedQty;
    }
    
    public String getTreatmentDate(){
        return treatmentDate;
    }
    
    public void setTreatmentDate(String treatmentDate){
        this.treatmentDate = treatmentDate;
    }
    
    public String getTreatmentStatus(){
        return treatmentStatus;
    }
    
    public void setTreatmentStatus(String treatmentStatus){
        this.treatmentStatus = treatmentStatus;
    }
    
    @Override
    public String toString(){
            return 
           "Treatment ID: " + treatmentID +
           "\nConsultation: " + (consultation != null ? consultation.getConsultationID() : "N/A") +
           "\nPatient: " + (consultation != null && consultation.getPatient() != null ? consultation.getPatient().getName() : "N/A") +
           "\nDoctor: " + (consultation != null && consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A") +
           "\nDiagnosis: " + diagnosis +
           "\nPrescribed Treatment: " + prescribed +
           " (Qty: " + prescribedQty + ")" +
           "\nTreatment Date: " + treatmentDate +
           "\nTreatment Status: " + treatmentStatus;
    }
}
