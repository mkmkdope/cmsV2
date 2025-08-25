/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import control.PatientManager;
import java.time.LocalDateTime;

/**
 *
 * @author Ng Mei Yen
 */
public class ConsultationDAO {

    private ListInterface<Consultation> consultationList = new CircularDoublyLinkedList<>();
    private ListInterface<Doctor> doctorList = new CircularDoublyLinkedList<>();
    private ListInterface<Patient> patientList = new CircularDoublyLinkedList<>();
    private static int counter = 0;

    public ConsultationDAO() {
        PatientDAO patientDAO = new PatientDAO();
        ListInterface<Patient> patientList = patientDAO.getAllPatients();

        Patient patient1 = patientList.getEntry(1);
        Patient patient2 = patientList.getEntry(2);
        Patient patient3 = patientList.getEntry(3);
        Patient patient4 = patientList.getEntry(4);
        Patient patient5 = patientList.getEntry(5);

        //temporarily placement for doctor initializer
        //remove after combine
        Doctor d1 = new Doctor(
                "D2001",
                "Dr. Lim Wei Chen",
                "dr.lim@clinic.tarumt.edu.my",
                "General Medicine",
                "Monday-Friday 09AM-09PM",
                true,
                "15/01/2025",
                50.00
        );

        // Sample Doctor 2 - Pediatrics
        Doctor d2 = new Doctor(
                "D2002",
                "Dr. Nurul Ain Binti Ahmad",
                "dr.nurul@clinic.tarumt.edu.my",
                "Pediatrics",
                "Monday-Saturday 08AM-04PM",
                true,
                "20/02/2025",
                60.00
        );

        // Sample Doctor 3 - Internal Medicine
        Doctor d3 = new Doctor(
                "D2003",
                "Dr. Rajesh Kumar",
                "dr.rajesh@clinic.tarumt.edu.my",
                "Internal Medicine",
                "Tuesday-Saturday 10AM-06PM",
                false,
                "10/03/2025",
                55.00
        );

        doctorList.add(d1);
        doctorList.add(d2);
        doctorList.add(d3);
        //end doctor initialization

        Consultation c1 = new Consultation(generateID(), patient1, d1,
                LocalDateTime.of(2025, 8, 6, 9, 0), "Fever");
        c1.setStatus("Completed");
        c1.setFollowUpFlag(true);
        c1.setPreviousConsultationId(null);

        Consultation c2 = new Consultation(generateID(), patient1, d1,
                LocalDateTime.of(2025, 8, 7, 3, 0), "Follow-up");
        c2.setStatus("Completed");
        c2.setFollowUpFlag(false);
        c2.setPreviousConsultationId("C0001");
        
        Consultation c3 = new Consultation(generateID(), patient2, d2,
        LocalDateTime.of(2025, 7, 9, 9, 30), "Cough");
        c3.setStatus("Cancelled");
        c3.setFollowUpFlag(false);
        c3.setPreviousConsultationId(null);
        
        Consultation c4 = new Consultation(generateID(), patient3, d2,
        LocalDateTime.of(2025, 7, 10, 10, 0), "Cough");
        c4.setStatus("Completed");
        c4.setFollowUpFlag(false);
        c4.setPreviousConsultationId(null);
        
        Consultation c5 = new Consultation(generateID(), patient4, d3,
        LocalDateTime.of(2025, 8, 25, 12, 0), "Cough and Fever");
        c5.setStatus("Scheduled");
        c5.setFollowUpFlag(false);
        c5.setPreviousConsultationId(null);

        consultationList.add(c1);
        consultationList.add(c2);
        consultationList.add(c3);
        consultationList.add(c4);
        consultationList.add(c5);
        
    }

    public static String generateID() {
        counter++;
        return String.format("C%04d", counter);
    }

    public void addConsultation(Consultation consultation) {
        consultationList.add(consultation);
    }

    //temporarily placement!!!!!!!!
    public ListInterface<Doctor> getAllDoctors() {
        return doctorList;
    }

    public Doctor findDoctorById(String doctorId) {
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor doctor = doctorList.getEntry(i);
            if (doctor.getDoctorID().equalsIgnoreCase(doctorId)) {
                return doctor;
            }
        }
        return null;
    }
    //end temporarily placement!!!!!!!!

    public ListInterface<Consultation> getAllConsultation() {
        return consultationList;
    }
    // find consultation by ID for Treatment module integration
    public Consultation findConsultationByID(String consultationId) {
        for (int i = 1; i <= consultationList.getNumberOfEntries(); i++) {
            Consultation consultation = consultationList.getEntry(i);
            if (consultation.getConsultationID().equalsIgnoreCase(consultationId)) {
                return consultation;
            }
        }
        return null;
    }
}
