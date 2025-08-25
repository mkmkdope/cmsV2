package dao;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import entity.Doctor;
import java.util.Comparator;

/**
 * Data Access Object for Doctor operations
 * Uses CircularDoublyLinkedList from ADT package
 */
public class DoctorDAO {
    
    private ListInterface<Doctor> doctorList = new CircularDoublyLinkedList<>();
    private int idCounter = 1;

    public DoctorDAO() {
        // Initialize with sample doctors
        addDoctor(new Doctor("D2001", "Dr. Lim Wei Chen", "dr.lim@clinic.tarumt.edu.my", 
                           "General Medicine", "Monday-Friday 09AM-09PM", true, "15/01/2025", 50.00));
        
        addDoctor(new Doctor("D2002", "Dr. Nurul Ain Binti Ahmad", "dr.nurul@clinic.tarumt.edu.my", 
                           "Pediatrics", "Monday-Saturday 08AM-04PM", true, "20/02/2025", 60.00));
        
        addDoctor(new Doctor("D2003", "Dr. Rajesh Kumar", "dr.rajesh@clinic.tarumt.edu.my", 
                           "Internal Medicine", "Tuesday-Saturday 10AM-06PM", false, "10/03/2025", 55.00));
        
        addDoctor(new Doctor("D2004", "Dr. Sarah Tan", "dr.sarah@clinic.tarumt.edu.my", 
                           "Cardiology", "Monday-Friday 08AM-05PM", true, "05/04/2025", 80.00));
        
        addDoctor(new Doctor("D2005", "Dr. Ahmad Zulkifli", "dr.ahmad@clinic.tarumt.edu.my", 
                           "Orthopedics", "Wednesday-Sunday 09AM-06PM", true, "12/05/2025", 70.00));
    }

    public String generateDoctorId() {
        return String.format("D%04d", idCounter++);
    }

    public boolean addDoctor(Doctor doctor) {
        if (findDoctorById(doctor.getDoctorID()) != null) {
            return false; // Duplicate ID
        }
        
        if (findDoctorByEmail(doctor.getEmail()) != null) {
            return false; // Duplicate email
        }
        
        return doctorList.add(doctor);
    }

    public Doctor findDoctorById(String doctorId) {
        Comparator<Doctor> idComparator = (d1, d2) -> d1.getDoctorID().compareToIgnoreCase(d2.getDoctorID());
        Doctor searchKey = new Doctor(doctorId, "", "", "", "", false, "", 0.0);
        
        int index = doctorList.searchByKey(idComparator, searchKey);
        if (index != -1) {
            return doctorList.getEntry(index);
        }
        
        // Fallback to traversal
        for (Doctor doctor : doctorList) {
            if (doctor.getDoctorID().equalsIgnoreCase(doctorId)) {
                return doctor;
            }
        }
        return null;
    }

    public Doctor findDoctorByEmail(String email) {
        for (Doctor doctor : doctorList) {
            if (doctor.getEmail().equalsIgnoreCase(email)) {
                return doctor;
            }
        }
        return null;
    }

    public boolean updateDoctor(Doctor updatedDoctor) {
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            if (doctorList.getEntry(i).getDoctorID().equalsIgnoreCase(updatedDoctor.getDoctorID())) {
                return doctorList.replace(i, updatedDoctor);
            }
        }
        return false;
    }

    public boolean deleteDoctor(String doctorId) {
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            if (doctorList.getEntry(i).getDoctorID().equalsIgnoreCase(doctorId)) {
                doctorList.remove(i);
                return true;
            }
        }
        return false;
    }

    public ListInterface<Doctor> getAllDoctors() {
        return doctorList;
    }

    public ListInterface<Doctor> getAvailableDoctors() {
        ListInterface<Doctor> availableDoctors = new CircularDoublyLinkedList<>();
        for (Doctor doctor : doctorList) {
            if (doctor.isAvailable()) {
                availableDoctors.add(doctor);
            }
        }
        return availableDoctors;
    }

    public ListInterface<Doctor> getDoctorsBySpecialization(String specialization) {
        ListInterface<Doctor> specializedDoctors = new CircularDoublyLinkedList<>();
        for (Doctor doctor : doctorList) {
            if (doctor.getSpecialization().equalsIgnoreCase(specialization)) {
                specializedDoctors.add(doctor);
            }
        }
        return specializedDoctors;
    }

    public void sortDoctorsByName() {
        doctorList.mergeSort((d1, d2) -> d1.getName().compareToIgnoreCase(d2.getName()));
    }

    public void sortDoctorsBySpecialization() {
        doctorList.mergeSort((d1, d2) -> d1.getSpecialization().compareToIgnoreCase(d2.getSpecialization()));
    }

    public void sortDoctorsByConsultationFee() {
        doctorList.mergeSort((d1, d2) -> Double.compare(d1.getConsultationFee(), d2.getConsultationFee()));
    }

    public int getDoctorCount() {
        return doctorList.getNumberOfEntries();
    }

    public boolean isDoctorListEmpty() {
        return doctorList.isEmpty();
    }

    public boolean isDoctorListFull() {
        return doctorList.isFull();
    }

    public void clearAllDoctors() {
        doctorList.clear();
        idCounter = 1;
    }

    public Doctor getDoctorAtPosition(int position) {
        try {
            return doctorList.getEntry(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean replaceDoctorAtPosition(int position, Doctor newDoctor) {
        return doctorList.replace(position, newDoctor);
    }

    public boolean insertDoctorAtPosition(int position, Doctor doctor) {
        if (findDoctorById(doctor.getDoctorID()) != null) {
            return false; // Duplicate ID
        }
        if (findDoctorByEmail(doctor.getEmail()) != null) {
            return false; // Duplicate email
        }
        try {
            return doctorList.add(position, doctor);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}