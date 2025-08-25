package control;

import dao.DoctorDAO;
import entity.Doctor;

public class DoctorManager {

    private DoctorDAO doctorDAO;

    public DoctorManager() {
        doctorDAO = new DoctorDAO();
    }

    public Doctor[] getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }
// Original method with default duty schedule
    public boolean addDoctor(String doctorId, String name, String specialization) {
        return addDoctor(doctorId, name, specialization, "Monday-Friday 09AM-05PM"); // Default duty schedule
    }

    // New method with custom duty schedule
    public boolean addDoctor(String doctorId, String name, String specialization, String dutySchedule) {
        if (doctorDAO.findDoctor(doctorId) != null) {
            return false;
        }

        Doctor doctor = new Doctor(doctorId, name, specialization, dutySchedule );
        doctor.setDutySchedule(dutySchedule); // Set the duty schedule
        doctorDAO.addDoctor(doctor);
        return true;
    }
    
    public Doctor[] getDoctorsForDutyDay(int dayIndex) {
        return doctorDAO.getDoctorsForDutyDay(dayIndex);
    }

    public void generateDutyReport() {
        doctorDAO.generateDutyReport();
    }

    public void generateWorkloadReport() {
        doctorDAO.generateWorkloadReport();
    }

    public void generateSpecialtyReport() {
        doctorDAO.generateSpecialtyReport();
    }

    public boolean updateDoctor(String doctorId, String name, String specialization, String dutySchedule) {
        return doctorDAO.updateDoctor(doctorId, name, specialization, dutySchedule);
    }

    public Doctor searchDoctor(String doctorId) {
        return doctorDAO.findDoctor(doctorId);
    }

    public boolean removeDoctor(String doctorId) {
        Doctor doctor = doctorDAO.findDoctor(doctorId);
        if (doctor != null) {
            doctorDAO.removeDoctor(doctorId);
            return true;
        }
        return false;
    }

    public boolean updateAvailability(String doctorId, boolean available) {
        Doctor doctor = doctorDAO.findDoctor(doctorId);
        if (doctor != null) {
            doctorDAO.updateDoctorAvailability(doctorId, available);
            return true;
        }
        return false;
    }

    public Doctor[] getDutyReport() {
        return doctorDAO.getDoctorsSortedById();
    }

    public Doctor[] getAvailabilityReport() {
        return doctorDAO.getDoctorsSortedById();
    }

    public boolean addDoctorToDutyDay(int dayIndex, String doctorId) {
        return doctorDAO.addDoctorToDutyDay(dayIndex, doctorId);
    }

    public boolean removeDoctorFromDutyDay(int dayIndex, String doctorId) {
        return doctorDAO.removeDoctorFromDutyDay(dayIndex, doctorId);
    }

    public void displayDutySchedule() {
        doctorDAO.displayDutySchedule();
    }

    public void rearrangeDutySchedule() {
        doctorDAO.rearrangeDutySchedule();
    }

    public int getDoctorCount() {
        return doctorDAO.getDoctorCount();
    }

    public int getTotalScheduledDoctors() {
        return doctorDAO.getTotalScheduledDoctors();
    }

    public void regenerateSchedule() {
        doctorDAO.regenerateSchedule();
    }
}
