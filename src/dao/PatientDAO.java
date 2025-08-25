/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import entity.Patient;
import java.util.Comparator;

/**
 *
 * @author USER
 */
public class PatientDAO {

    private ListInterface<Patient> patientList = new CircularDoublyLinkedList<>();
    private int idCounter = 1; // for auto increment IDs

    public PatientDAO(){
        // Default patients
        addPatient(new Patient(generatePatientId(), "010101010101", "Alice Tan", "Female", 22,
                "012345678901", "Kuala Lumpur", "alice@example.com", "No allergies"));
        addPatient(new Patient(generatePatientId(), "990202020202", "John Lee", "Male", 30,
                "013876543210", "Selangor", "john@example.com", "Asthma"));
        addPatient(new Patient(generatePatientId(), "880303030303", "Mei Ling", "Female", 28,
                "011998877665", "Penang", "mei@example.com", "Diabetes"));
        addPatient(new Patient(generatePatientId(), "950404040404", "Ahmad Zulkifli", "Male", 29,
                "019223344556", "Johor Bahru", "ahmadzulkifli@example.com", "Hypertension"));
        addPatient(new Patient(generatePatientId(), "920505050505", "Siti Nurhaliza", "Female", 33,
                "017556677889", "Kuching", "sitinur@example.com", "No known allergies"));
        addPatient(new Patient(generatePatientId(), "870606060606", "David Wong", "Male", 38,
                "016889900112", "Kota Kinabalu", "davidw@example.com", "Heart disease"));
        addPatient(new Patient(generatePatientId(), "990707070707", "Priya Raj", "Female", 25,
                "014112233445", "Ipoh", "priyaraj@example.com", "Peanut allergy"));
        addPatient(new Patient(generatePatientId(), "850808080808", "Chen Wei", "Male", 40,
                "018445566778", "Melaka", "chenwei@example.com", "No allergies"));
        addPatient(new Patient(generatePatientId(), "960909090909", "Nur Aisyah", "Female", 27,
                "011223344556", "Shah Alam", "aisyah@example.com", "Eczema"));
        addPatient(new Patient(generatePatientId(), "890101010101", "Michael Lim", "Male", 36,
                "015998877665", "Seremban", "michaellim@example.com", "Lactose intolerance"));
    }

    public String generatePatientId() {
        return String.format("P%03d", idCounter++);
    }

    public boolean addPatient(Patient patient) {
        if (findPatientById(patient.getPatientId()) != null) {
            return false;
        }
       
        if (findPatientByIC(patient.getIcNumber()) != null) {
            return false;
        }
        
        return patientList.add(patient);
    }

    // Fixed: Updated to use correct priority comparator
    public boolean addPatientWithPriority(Patient patient, int priority){
        if(findPatientById(patient.getPatientId()) != null) {
            return false;
        }

        if(findPatientByIC(patient.getIcNumber()) != null){
            return false;
        }
        
        // Create a patient with priority wrapper for comparison
        PatientWithPriority patientWithPriority = new PatientWithPriority(patient, priority);
        
        // Use priority comparator (lower number = higher priority)
        Comparator<PatientWithPriority> priorityComparator = (p1, p2) -> 
            Integer.compare(p1.priority, p2.priority);
        
        // Convert existing patients to have default priority of 5 (lowest)
        ListInterface<PatientWithPriority> priorityList = new CircularDoublyLinkedList<>();
        
        // Add existing patients with default priority
        for(Patient p : patientList){
            priorityList.add(new PatientWithPriority(p, 5));
        };
        
        // Add new patient with specified priority
        priorityList.addWithPriority(priorityComparator, patientWithPriority);
        
        // Update main list
        patientList.clear();

        //using the iterator
       for(PatientWithPriority pwp : priorityList){
            patientList.add(pwp.patient);
        };
        
        return true;
    }

    // Inner class to handle priority
    private static class PatientWithPriority {
        Patient patient;
        int priority;
        
        PatientWithPriority(Patient patient, int priority) {
            this.patient = patient;
            this.priority = priority;
        }
    }

    public Patient findPatientById(String id){
        // Using the new searchByKey method for better performance
        Comparator<Patient> idComparator = (p1, p2) -> p1.getPatientId().compareToIgnoreCase(p2.getPatientId());
        Patient searchKey = new Patient(id, "", "", "", 0, "", "", "", "");
        
        int index = patientList.searchByKey(idComparator, searchKey);
        if (index != -1) {
            return patientList.getEntry(index);
        }
        
        // Fallback to traversal method
        final Patient[] result = {null};
        for(Patient patient : patientList ){
            if (patient.getPatientId().equalsIgnoreCase(id) && result[0] == null) {
                result[0] = patient;
            }
        };
        return result[0];
    }

    public Patient findPatientByIC(String ic) {
        // Using traversal for IC search (since IC is not the primary comparison key)
        final Patient[] result = {null};
       for(Patient patient : patientList){
            if (patient.getIcNumber().equalsIgnoreCase(ic) && result[0] == null) {
                result[0] = patient;
            }
        };
        return result[0];
    }

    public boolean updatePatient(Patient updated){
        for(int i = 1; i <= patientList.getNumberOfEntries(); i++){
            if(patientList.getEntry(i).getPatientId().equalsIgnoreCase(updated.getPatientId())){
                return patientList.replace(i, updated); // Updated to use boolean return
            }
        }
        return false;
    }

    public boolean deletePatient(String id){
        for(int i = 1; i <= patientList.getNumberOfEntries(); i++){
            if(patientList.getEntry(i).getPatientId().equalsIgnoreCase(id)){
                patientList.remove(i);
                return true;
            }
        }
        return false;
    }

    public ListInterface<Patient> getAllPatients(){
        return patientList;
    }

    //get the circularDoublyLinkedList directly for traversal operations
    public ListInterface<Patient> getPatientListForTraversal(){
        return patientList;
    }

    // Methods utilizing adt functions
    public boolean containsPatient(Patient patient) {
        return patientList.contains(patient);
    }

    public boolean isPatientListEmpty() {
        return patientList.isEmpty();
    }

    public boolean isPatientListFull() {
        return patientList.isFull();
    }

    public int getPatientCount() {
        return patientList.getNumberOfEntries();
    }

    public void clearAllPatients() {
        patientList.clear();
        idCounter = 1; // Reset ID counter
    }

    public Patient getPatientAtPosition(int position) {
        try {
            return patientList.getEntry(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean replacePatientAtPosition(int position, Patient newPatient) {
        return patientList.replace(position, newPatient);
    }

    public boolean insertPatientAtPosition(int position, Patient patient) {
        if (findPatientById(patient.getPatientId()) != null) {
            return false; // Duplicate ID
        }
        if (findPatientByIC(patient.getIcNumber()) != null) {
            return false; // Duplicate IC
        }
        try {
            return patientList.add(position, patient);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    // Enhanced search methods using the new searchByKey functionality
    public Patient searchPatientByName(String name) {
        Comparator<Patient> nameComparator = (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName());
        Patient searchKey = new Patient("", "", name, "", 0, "", "", "", "");
        
        int index = patientList.searchByKey(nameComparator, searchKey);
        if (index != -1) {
            return patientList.getEntry(index);
        }
        return null;
    }

    public Patient searchPatientByAge(int age) {
        Comparator<Patient> ageComparator = (p1, p2) -> Integer.compare(p1.getAge(), p2.getAge());
        Patient searchKey = new Patient("", "", "", "", age, "", "", "", "");
        
        int index = patientList.searchByKey(ageComparator, searchKey);
        if (index != -1) {
            return patientList.getEntry(index);
        }
        return null;
    }

    // Method to sort patients using the new MergeSort functionality
    public void sortPatients(Comparator<Patient> comparator) {
        patientList.mergeSort(comparator);
    }

    // // Additional utility methods
    // public void displayPatientStatistics() {
    //     System.out.println("=== Patient Statistics ===");
    //     System.out.println("Total Patients: " + getPatientCount());
    //     System.out.println("List Empty: " + isPatientListEmpty());
    //     System.out.println("List Full: " + isPatientListFull());
    // }

    // // Method to get all patients matching a criteria using traversal
    // public ListInterface<Patient> findPatientsByCriteria(java.util.function.Predicate<Patient> criteria) {
    //     ListInterface<Patient> matchingPatients = new CircularDoublyLinkedList<>();
        
    //    for (Patient patient : patientList){
    //         if (criteria.test(patient)) {
    //             matchingPatients.add(patient);
    //         }
    //     }
        
    //     return matchingPatients;
    // }

    // // Batch operations using traversal
    // public void updateAllPatientsEmail(String domain) {
    //      for(Patient patient : patientList){
    //         if (patient.getEmail().isEmpty()) {
    //             String newEmail = patient.getName().toLowerCase().replaceAll("\\s+", "") + "@" + domain;
    //             patient.setEmail(newEmail);
    //         }
    //     }
    // }







   
}