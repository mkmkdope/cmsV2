package control;

import dao.DoctorDAO;
import entity.Doctor;

/**
 *
 * @author Yap Ming Kang
 */

public class DoctorManager {

    private DoctorDAO doctorDAO;

    public DoctorManager() {
        doctorDAO = new DoctorDAO();
    }

    public Doctor[] getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }

    //default duty schedule
    public boolean addDoctor(String doctorId, String name, String specialization) {
        return addDoctor(doctorId, name, specialization, "Monday-Friday 09AM-05PM");
    }

    //custom duty schedule
    public boolean addDoctor(String doctorId, String name, String specialization, String dutySchedule) {
        if (doctorDAO.findDoctor(doctorId) != null) {
            return false;
        }

        Doctor doctor = new Doctor(doctorId, name, specialization, dutySchedule);
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

    //set to default edy
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

    //Not using
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

    public void rotateDoctors(int steps) {
        doctorDAO.rotateDoctors(steps);
        System.out.println("Doctor list rotated by " + steps + " positions.");
        System.out.println("Duty schedule has been updated.");
    }

    public boolean swapDoctors(String doctorId1, String doctorId2) {
        Doctor doctor1 = doctorDAO.findDoctor(doctorId1);
        Doctor doctor2 = doctorDAO.findDoctor(doctorId2);

        if (doctor1 == null || doctor2 == null) {
            System.out.println("One or both doctors not found!");
            return false;
        }

        if (doctorId1.equals(doctorId2)) {
            System.out.println("Doctors are the same. No swap needed.");
            return true;
        }

        doctorDAO.swapDoctors(doctorId1, doctorId2);
        System.out.println("Swapped positions of Dr. " + doctorId1 + " and Dr. " + doctorId2);
        System.out.println("Duty schedule has been updated.");
        return true;
    }

    public void generateNextCycleReport() {
        //System.out.println("\n--- TARUMT CLINIC MANAGEMENT SYSTEM ---");
        System.out.println("\n--- Next Cycle Duty Schedule Report --->>>>>");
        System.out.println("ID   Name                 Mon Tue Wed Thu Fri Total");
        System.out.println("---------------------------------------------");

        // Get current doctors in current order
        Doctor[] doctors = doctorDAO.getDoctorsInCurrentOrder();

        // Generate and display simulation using continuous round-robin
        displayContinuousRoundRobinSchedule(doctors);
    }

    private void displayContinuousRoundRobinSchedule(Doctor[] doctors) {
        int[] dayTotals = new int[5]; // Mon-Fri totals
        int grandTotal = 0;
        int availableCount = 0;

        // Create duty matrix [doctor][day]
        boolean[][] duties = new boolean[doctors.length][5];

        // Continuous round-robin assignment
        int currentIndex = 0; // Start from doctor at position 0
        int slotsPerDay = 10; // 10 slots per day

        for (int day = 0; day < 5; day++) { // All 5 days (Mon-Fri)
            for (int slot = 0; slot < slotsPerDay; slot++) {
                if (doctors.length > 0) {
                    duties[currentIndex][day] = true;
                    dayTotals[day]++;
                    grandTotal++;

                    // Move to next doctor, wrap around if needed
                    currentIndex = (currentIndex + 1) % doctors.length;
                }
            }
        }

        // Display the schedule
        for (int i = 0; i < doctors.length; i++) {
            Doctor doctor = doctors[i];

            System.out.printf("%-5s %-20s", doctor.getDoctorId(), doctor.getName());

            int doctorTotal = 0;
            for (int day = 0; day < 5; day++) {
                if (duties[i][day]) {
                    System.out.print("  * ");
                    doctorTotal++;
                } else {
                    System.out.print("    ");
                }
            }
            System.out.printf(" %2d%n", doctorTotal);

            if (doctor.isAvailable()) {
                availableCount++;
            }
        }

        System.out.println("---------------------------------------------");
        System.out.println("* = New Duty Cycle");
        System.out.printf("Mon: %d | Tue: %d | Wed: %d | Thu: %d | Fri: %d%n",
                dayTotals[0], dayTotals[1], dayTotals[2], dayTotals[3], dayTotals[4]);
        //System.out.println("Total Projected Duties: " + grandTotal);
        //System.out.println("Total Doctors: " + doctors.length);
        //System.out.println("----- End Of Report -----");
    }

}
