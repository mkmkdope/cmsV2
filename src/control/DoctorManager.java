package control;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import boundary.Messages;
import dao.DoctorDAO;
import entity.Doctor;
import java.util.Comparator;
import java.util.Scanner;

public class DoctorManager {
    private DoctorDAO dao;
    private Scanner sc = new Scanner(System.in);

    public DoctorManager(DoctorDAO doctorDAO) {
        this.dao = doctorDAO;
    }

    public boolean registerDoctor(String name, String email, String specialization, 
                                 String dutySchedule, boolean isAvailable, String joinDate, double consultationFee) {
        if (dao.findDoctorByEmail(email) != null) {
            System.out.println("Doctor with this email already exists.");
            return false;
        }
        
        if (consultationFee < 0) {
            System.out.println("Consultation fee cannot be negative.");
            return false;
        }

        String newId = dao.generateDoctorId();
        Doctor doctor = new Doctor(newId, name, email, specialization, dutySchedule, isAvailable, joinDate, consultationFee);
        boolean success = dao.addDoctor(doctor);
        if (success) {
            System.out.println("Doctor registered successfully with ID: " + newId);
        }
        return success;
    }

    public void displayAllDoctors() {
        ListInterface<Doctor> list = dao.getAllDoctors();
        if (list.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.printf("%-10s | %-25s | %-30s | %-20s | %-15s | %-10s | %-12s\n", 
                         "ID", "Name", "Email", "Specialization", "Duty Schedule", "Available", "Fee (RM)");
        System.out.println("=".repeat(100));

        for (Doctor doctor : list) {
            System.out.printf("%-10s | %-25s | %-30s | %-20s | %-15s | %-10s | %-12.2f\n",
                             doctor.getDoctorID(), doctor.getName(), doctor.getEmail(),
                             doctor.getSpecialization(), doctor.getDutySchedule(),
                             doctor.isAvailable() ? "Yes" : "No", doctor.getConsultationFee());
        }
        System.out.println("=".repeat(100));
    }

    public void searchDoctor(String keyword) {
        Doctor doctor = dao.findDoctorById(keyword);
        if (doctor == null) {
            doctor = dao.findDoctorByEmail(keyword);
        }
        
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.println("\n=== Doctor Information ===");
        System.out.println(doctor);
    }

    public boolean updateDoctor(String id, String name, String email, String specialization,
                               String dutySchedule, boolean isAvailable, String joinDate, double consultationFee) {
        Doctor existing = dao.findDoctorById(id);
        if (existing == null) {
            System.out.println("Doctor not found.");
            return false;
        }

        Doctor updated = new Doctor(id, name, email, specialization, dutySchedule, isAvailable, joinDate, consultationFee);
        boolean success = dao.updateDoctor(updated);
        if (success) {
            System.out.println("Doctor information updated successfully.");
        }
        return success;
    }

    public boolean deleteDoctor(String id) {
        boolean success = dao.deleteDoctor(id);
        if (success) {
            System.out.println("Doctor deleted successfully.");
        } else {
            System.out.println("Doctor not found.");
        }
        return success;
    }

    public void displayAvailableDoctors() {
        ListInterface<Doctor> availableDoctors = dao.getAvailableDoctors();
        if (availableDoctors.isEmpty()) {
            System.out.println("No available doctors found.");
            return;
        }

        System.out.println("\n=== Available Doctors ===");
        System.out.printf("%-10s | %-25s | %-20s | %-15s | %-12s\n", 
                         "ID", "Name", "Specialization", "Duty Schedule", "Fee (RM)");
        System.out.println("-".repeat(85));

        for (Doctor doctor : availableDoctors) {
            System.out.printf("%-10s | %-25s | %-20s | %-15s | %-12.2f\n",
                             doctor.getDoctorID(), doctor.getName(), doctor.getSpecialization(),
                             doctor.getDutySchedule(), doctor.getConsultationFee());
        }
        System.out.println("-".repeat(85));
    }

    public void displayDoctorsBySpecialization(String specialization) {
        ListInterface<Doctor> specializedDoctors = dao.getDoctorsBySpecialization(specialization);
        if (specializedDoctors.isEmpty()) {
            System.out.println("No doctors found with specialization: " + specialization);
            return;
        }

        System.out.println("\n=== Doctors with Specialization: " + specialization + " ===");
        System.out.printf("%-10s | %-25s | %-30s | %-15s | %-10s | %-12s\n", 
                         "ID", "Name", "Email", "Duty Schedule", "Available", "Fee (RM)");
        System.out.println("-".repeat(100));

        for (Doctor doctor : specializedDoctors) {
            System.out.printf("%-10s | %-25s | %-30s | %-15s | %-10s | %-12.2f\n",
                             doctor.getDoctorID(), doctor.getName(), doctor.getEmail(),
                             doctor.getDutySchedule(), doctor.isAvailable() ? "Yes" : "No", 
                             doctor.getConsultationFee());
        }
        System.out.println("-".repeat(100));
    }

    public void sortDoctorsByName() {
        dao.sortDoctorsByName();
        System.out.println("Doctors sorted by name.");
        displayAllDoctors();
    }

    public void sortDoctorsBySpecialization() {
        dao.sortDoctorsBySpecialization();
        System.out.println("Doctors sorted by specialization.");
        displayAllDoctors();
    }

    public void sortDoctorsByConsultationFee() {
        dao.sortDoctorsByConsultationFee();
        System.out.println("Doctors sorted by consultation fee.");
        displayAllDoctors();
    }

    public Doctor getDoctorById(String id) {
        return dao.findDoctorById(id);
    }

    public Doctor getDoctorByEmail(String email) {
        return dao.findDoctorByEmail(email);
    }

    public ListInterface<Doctor> getAllDoctors() {
        return dao.getAllDoctors();
    }

    public ListInterface<Doctor> getAvailableDoctors() {
        return dao.getAvailableDoctors();
    }

    public int getDoctorCount() {
        return dao.getDoctorCount();
    }

    public boolean isDoctorListEmpty() {
        return dao.isDoctorListEmpty();
    }

    public boolean isDoctorListFull() {
        return dao.isDoctorListFull();
    }
}