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
    // Make the treatment list shared across the whole app so changes
    // (like newly added treatments or status updates) are visible everywhere
    private static CircularDoublyLinkedList<Treatment> treatmentList;
    private static int idCounter = 1;
    private static boolean initialized = false;

    public TreatmentDAO() {
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
    // Get existing data from other modules for consistency
    PatientDAO patientDAO = new PatientDAO();
    ListInterface<Patient> patients = patientDAO.getAllPatients();
    
    // Get doctors from DoctorManager instead of DoctorInitializer
    DoctorManager doctorManager = new DoctorManager();
    Doctor[] doctorsArray = doctorManager.getAllDoctors();
    
    // Convert array to list
    CircularDoublyLinkedList<Doctor> doctors = new CircularDoublyLinkedList<>();
    for (Doctor doctor : doctorsArray) {
        doctors.add(doctor);
    }
    
    // Check if we have enough patients and doctors
    if (patients.getNumberOfEntries() < 2 || doctors.getNumberOfEntries() < 2) {
        System.out.println("Warning: Not enough sample data for treatments initialization");
        return;
    }
    
    try {
        // Use safe access with bounds checking
        Patient p1 = patients.getEntry(1);
        Patient p2 = patients.getEntry(Math.min(2, patients.getNumberOfEntries()));
        Patient p3 = patients.getEntry(Math.min(3, patients.getNumberOfEntries()));
        Patient p4 = patients.getEntry(Math.min(4, patients.getNumberOfEntries()));
        
        Doctor d1 = doctors.getEntry(1);
        Doctor d2 = doctors.getEntry(Math.min(2, doctors.getNumberOfEntries()));
        Doctor d3 = doctors.getEntry(Math.min(3, doctors.getNumberOfEntries()));

        // create consultations using the existing data
        Consultation c1 = new Consultation("C0001", p1, d1,
                java.time.LocalDateTime.of(2025, 1, 10, 9, 30),
                "Fever and cough");
        Consultation c2 = new Consultation("C0002", p2, d2,
                java.time.LocalDateTime.of(2025, 2, 5, 11, 0),
                "Chest pain");
        Consultation c3 = new Consultation("C0003", p3, d1,
                java.time.LocalDateTime.of(2025, 3, 20, 14, 15),
                "Asthma attack");
        Consultation c4 = new Consultation("C0004", p4, d3,
                java.time.LocalDateTime.of(2025, 4, 2, 10, 0),
                "Breathing difficulty");
        Consultation c5 = new Consultation("C0005", p1, d2,
                java.time.LocalDateTime.of(2025, 5, 15, 15, 45),
                "Irregular heartbeat");
        Consultation c6 = new Consultation("C0006", p2, d1,
                java.time.LocalDateTime.of(2025, 6, 25, 13, 30),
                "Headache and dizziness");

        // create treatments using the consultations
        Treatment t1 = new Treatment(generateTreatmentID(), c1, "Flu",
                "Paracetamol 500mg", 20, "2025-01-10", "Completed");
        Treatment t2 = new Treatment(generateTreatmentID(), c2, "Hypertension",
                "Omeprazole 20mg", 40, "2025-02-05", "Pending");
        Treatment t3 = new Treatment(generateTreatmentID(), c3, "Asthma",
                "Cetirizine 10mg", 30, "2025-03-20", "Completed");
        Treatment t4 = new Treatment(generateTreatmentID(), c4, "Allergic Rhinitis",
                "Cetirizine 10mg", 50, "2025-04-02", "Completed");
        Treatment t5 = new Treatment(generateTreatmentID(), c5, "Arrhythmia",
                "Paracetamol 500mg", 45, "2025-05-15", "Pending");
        Treatment t6 = new Treatment(generateTreatmentID(), c6, "Migraine",
                "Ibuprofen 400mg", 60, "2025-06-25", "Completed");

        treatmentList.add(t1);
        treatmentList.add(t2);
        treatmentList.add(t3);
        treatmentList.add(t4);
        treatmentList.add(t5);
        treatmentList.add(t6);
        
    } catch (Exception e) {
        System.out.println("Warning: Could not initialize sample treatment data: " + e.getMessage());
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