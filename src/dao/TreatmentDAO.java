/**
 *
 * @author Angelo Wan Kai Zhe
 */
package dao;

import adt.CircularDoublyLinkedList;
import entity.Treatment;
import entity.Consultation;
import entity.Patient;
import entity.Doctor;
import adt.ListInterface;
import dao.PatientDAO;
import control.DoctorManager;
import dao.ConsultationDAO;

public class TreatmentDAO {
    private static CircularDoublyLinkedList<Treatment> treatmentList;
    private ListInterface<Consultation> consultations;
    private ConsultationDAO consultationDAO;
    private static int idCounter = 1;
    private static boolean initialized = false;

    public TreatmentDAO(ConsultationDAO consultationDAO) {
        this.consultationDAO=consultationDAO;
        if (!initialized) {
            treatmentList = new CircularDoublyLinkedList<>();
            initializeData();
            initialized = true;
        }
    }

    private String generateTreatmentID() {
        return String.format("T%04d", idCounter++); // T0001, T0002, ...
    }
    
    public Treatment addNewTreatment(Consultation consultation, String diagnosis,
                                     String prescribed, int prescribedQty, String treatmentDate, String status) {
        String id = generateTreatmentID();
        Treatment t = new Treatment(id, consultation, diagnosis, prescribed, prescribedQty, treatmentDate, status);
        treatmentList.add(t);
        return t;
    }

private void initializeData() {
    consultations = consultationDAO.getAllConsultation();

    if (consultations.isEmpty()) {
        System.out.println("WARNING: No consultations available for treatment initialization");
        return;
    }
    
    try {
        // create consultations using the existing data
        Consultation c1 = consultations.getEntry(1);
        Consultation c2 = consultations.getEntry(2);
        Consultation c3 = consultations.getEntry(3);
        Consultation c4 = consultations.getEntry(4);
        Consultation c5 = consultations.getEntry(5);
        Consultation c6 = consultations.getEntry(6);


        // create treatments using the consultations
        Treatment t1 = new Treatment(generateTreatmentID(), c1, "Flu",
                "Paracetamol 500mg", 20, "2025-01-10", "Completed");//Fever
        Treatment t2 = new Treatment(generateTreatmentID(), c2, "Hypertension",
                "Omeprazole 20mg", 40, "2025-02-05", "Pending");//Fever
        Treatment t3 = new Treatment(generateTreatmentID(), c3, "Asthma",
                "Cetirizine 10mg", 30, "2025-03-20", "Completed");//Cough
        Treatment t4 = new Treatment(generateTreatmentID(), c4, "Allergic Rhinitis",
                "Cetirizine 10mg", 50, "2025-04-02", "Completed");//Cough and Fever
        Treatment t5 = new Treatment(generateTreatmentID(), c5, "Arrhythmia",
                "Paracetamol 500mg", 45, "2025-05-15", "Pending");//Headache
        Treatment t6 = new Treatment(generateTreatmentID(), c6, "Migraine",
                "Ibuprofen 400mg", 60, "2025-06-25", "Completed");//Fever

        treatmentList.add(t1);
        treatmentList.add(t2);
        treatmentList.add(t3);
        treatmentList.add(t4);
        treatmentList.add(t5);
        treatmentList.add(t6);
        
    } catch (Exception e) {
        System.out.println("WARNING: Could not initialize sample treatment data: " + e.getMessage());
        e.printStackTrace();
    }
}

    public void add(Treatment treatment) {
        treatmentList.add(treatment);
    }

    public Treatment findByID(String treatmentID) {
        for (int i = 1; i <= treatmentList.getNumberOfEntries(); i++) {
            Treatment t = treatmentList.getEntry(i);
            if (t.getTreatmentID().equalsIgnoreCase(treatmentID)) {
                return t;
            }
        }
        return null;
    }

    public CircularDoublyLinkedList<Treatment> getAll() {
        return treatmentList;
    }

    public boolean isEmpty() {
        return treatmentList.isEmpty();
    }

    public int size() {
        return treatmentList.getNumberOfEntries();
    }

    public Treatment getAt(int index1Based) {
        return treatmentList.getEntry(index1Based);
    }

    public void replaceAt(int index1Based, Treatment replacement) {
        treatmentList.replace(index1Based, replacement);
    }

    public boolean updateFields(String id, String diagnosis, String prescribed, Integer prescribedQty, String status) {
        Treatment t = findByID(id);
        if (t == null) return false;

        if (diagnosis != null && !diagnosis.isEmpty()) t.setDiagnosis(diagnosis);
        if (prescribed != null && !prescribed.isEmpty()) t.setPrescribed(prescribed);
        if (prescribedQty != null && prescribedQty > 0) t.setPrescribedQty(prescribedQty);
        if (status != null && !status.isEmpty()) t.setTreatmentStatus(status);
        
        return true;
    }

    public boolean remove(String treatmentID) {
        for (int i = 1; i <= treatmentList.getNumberOfEntries(); i++) {
            if (treatmentList.getEntry(i).getTreatmentID().equalsIgnoreCase(treatmentID)) {
                treatmentList.remove(i);
                return true;
            }
        }
        return false;
    }
    
}