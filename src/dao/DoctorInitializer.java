/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.Doctor;
import adt.ListInterface;
import adt.CircularDoublyLinkedList;

/**
 *
 * @author Tan Yu Hang Utility class for initializing sample doctor data
 */
public class DoctorInitializer {

    /**
     * Creates and returns a list of sample doctors for testing
     * 
     * @return ListInterface containing sample doctor data
     */
    public static ListInterface<Doctor> initializeSampleDoctors() {
        CircularDoublyLinkedList<Doctor> doctorList = new CircularDoublyLinkedList<>();

        // Sample Doctor 1 - General Medicine
        Doctor doctor1 = new Doctor(
            "D2001", 
            "Dr. Lim Wei Chen", 
            "dr.lim@clinic.tarumt.edu.my",
            "General Medicine", 
            "Monday-Friday 9AM-5PM", 
            true, 
            "15/01/2025", 
            50.00
        );

        // Sample Doctor 2 - Pediatrics
        Doctor doctor2 = new Doctor(
            "D2002", 
            "Dr. Nurul Ain Binti Ahmad", 
            "dr.nurul@clinic.tarumt.edu.my",
            "Pediatrics", 
            "Monday-Saturday 8AM-4PM", 
            true, 
            "20/02/2025", 
            60.00
        );

        // Sample Doctor 3 - Internal Medicine
        Doctor doctor3 = new Doctor(
            "D2003", 
            "Dr. Rajesh Kumar", 
            "dr.rajesh@clinic.tarumt.edu.my",
            "Internal Medicine", 
            "Tuesday-Saturday 10AM-6PM", 
            false, 
            "10/03/2025", 
            55.00
        );

        // Sample Doctor 4 - Dermatology
        Doctor doctor4 = new Doctor(
            "D2004", 
            "Dr. Sarah Tan Mei Ling", 
            "dr.sarah@clinic.tarumt.edu.my",
            "Dermatology", 
            "Monday-Thursday 9AM-3PM", 
            true, 
            "05/04/2025", 
            70.00
        );

        // Sample Doctor 5 - Orthopedics
        Doctor doctor5 = new Doctor(
            "D2005", 
            "Dr. Ahmad Fadzil Bin Ismail", 
            "dr.ahmad@clinic.tarumt.edu.my",
            "Orthopedics", 
            "Wednesday-Sunday 8AM-5PM", 
            true, 
            "12/05/2025", 
            80.00
        );

        // Sample Doctor 6 - Psychiatry
        Doctor doctor6 = new Doctor(
            "D2006", 
            "Dr. Priya Devi A/P Ramasamy", 
            "dr.priya@clinic.tarumt.edu.my",
            "Psychiatry", 
            "Monday-Friday 10AM-4PM", 
            false, 
            "18/06/2025", 
            65.00
        );

        // Sample Doctor 7 - Cardiology
        Doctor doctor7 = new Doctor(
            "D2007", 
            "Dr. Chong Ming Wei", 
            "dr.chong@clinic.tarumt.edu.my",
            "Cardiology", 
            "Monday-Saturday 9AM-6PM", 
            true, 
            "25/07/2025", 
            90.00
        );

        // Sample Doctor 8 - Neurology
        Doctor doctor8 = new Doctor(
            "D2008", 
            "Dr. Fatimah Binti Omar", 
            "dr.fatimah@clinic.tarumt.edu.my",
            "Neurology", 
            "Tuesday-Friday 8AM-3PM", 
            true, 
            "30/08/2025", 
            75.00
        );

        // Add all doctors to the list
        doctorList.add(doctor1);
        doctorList.add(doctor2);
        doctorList.add(doctor3);
        doctorList.add(doctor4);
        doctorList.add(doctor5);
        doctorList.add(doctor6);
        doctorList.add(doctor7);
        doctorList.add(doctor8);

        return doctorList;
    }

    /**
     * Creates a single sample doctor with specified parameters
     * 
     * @param doctorId The doctor ID
     * @param name The doctor's name
     * @param specialization The doctor's specialization
     * @param isAvailable Whether the doctor is available
     * @param consultationFee The consultation fee
     * @return A Doctor object with sample data
     */
    public static Doctor createSampleDoctor(String doctorId, String name, String specialization, 
                                          boolean isAvailable, double consultationFee) {
        return new Doctor(
            doctorId,
            name,
            "dr.sample@clinic.tarumt.edu.my", // Default email
            specialization,
            "Monday-Friday 9AM-5PM", // Default schedule
            isAvailable,
            "01/01/2025", // Default join date
            consultationFee
        );
    }

    /**
     * Gets a list of available specializations for testing
     * 
     * @return Array of specialization names
     */
    public static String[] getAvailableSpecializations() {
        return new String[]{
            "General Medicine",
            "Pediatrics",
            "Internal Medicine",
            "Dermatology",
            "Orthopedics",
            "Psychiatry",
            "Cardiology",
            "Neurology",
            "Gynecology",
            "Ophthalmology",
            "ENT (Ear, Nose, Throat)",
            "Emergency Medicine",
            "Family Medicine",
            "Geriatrics",
            "Oncology"
        };
    }

    /**
     * Gets sample duty schedules for testing
     * 
     * @return Array of duty schedule examples
     */
    public static String[] getSampleDutySchedules() {
        return new String[]{
            "Monday-Friday 9AM-5PM",
            "Monday-Saturday 8AM-4PM",
            "Tuesday-Saturday 10AM-6PM",
            "Monday-Thursday 9AM-3PM",
            "Wednesday-Sunday 8AM-5PM",
            "Monday-Friday 10AM-4PM",
            "Monday-Saturday 9AM-6PM",
            "Tuesday-Friday 8AM-3PM",
            "Monday-Wednesday 9AM-2PM",
            "Thursday-Sunday 10AM-5PM"
        };
    }

    /**
     * Gets sample consultation fees for different specializations
     * 
     * @return Array of consultation fee ranges
     */
    public static double[] getSampleConsultationFees() {
        return new double[]{
            50.00, // General Medicine
            60.00, // Pediatrics
            55.00, // Internal Medicine
            70.00, // Dermatology
            80.00, // Orthopedics
            65.00, // Psychiatry
            90.00, // Cardiology
            75.00, // Neurology
            85.00, // Gynecology
            95.00, // Ophthalmology
            60.00, // ENT
            100.00, // Emergency Medicine
            45.00, // Family Medicine
            70.00, // Geriatrics
            120.00 // Oncology
        };
    }

    /**
     * Prints sample doctor data for reference
     */
    public static void printSampleDataInfo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         SAMPLE DOCTOR DATA INFO");
        System.out.println("=".repeat(60));
        System.out.println("Total Sample Doctors: 8");
        System.out.println("Available Doctors: 6");
        System.out.println("Unavailable Doctors: 2");
        System.out.println("\nSpecializations Available:");
        String[] specs = getAvailableSpecializations();
        for (int i = 0; i < specs.length; i++) {
            System.out.println((i + 1) + ". " + specs[i]);
        }
        System.out.println("\nConsultation Fee Range: RM 50.00 - RM 90.00");
        System.out.println("=".repeat(60));
    }
} 