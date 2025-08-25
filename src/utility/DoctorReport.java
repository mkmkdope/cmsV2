/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import adt.ListInterface;
import entity.Doctor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tan Yu Hang Utility class for generating doctor reports
 */
public class DoctorReport {

    /**
     * Generates a comprehensive summary report for all doctors
     * 
     * @param doctors List of all doctors
     * @return Formatted summary report string
     */
    public static String generateSummaryReport(ListInterface<Doctor> doctors) {
        if (doctors.isEmpty()) {
            return "No doctors found in the system.";
        }

        int totalDoctors = doctors.getNumberOfEntries();
        int availableDoctors = 0;
        int unavailableDoctors = 0;
        double totalConsultationFee = 0.0;
        double minFee = Double.MAX_VALUE;
        double maxFee = 0.0;
        for (int i = 1; i <= doctors.getNumberOfEntries(); i++) {
            Doctor doctor = doctors.getEntry(i);
            if (doctor != null) {
                if (doctor.isAvailable()) {
                    availableDoctors++;
                } else {
                    unavailableDoctors++;
                }
                
                totalConsultationFee += doctor.getConsultationFee();
                
                if (doctor.getConsultationFee() < minFee) {
                    minFee = doctor.getConsultationFee();
                }
                if (doctor.getConsultationFee() > maxFee) {
                    maxFee = doctor.getConsultationFee();
                }
            }
        }

        double averageFee = totalConsultationFee / totalDoctors;

        StringBuilder report = new StringBuilder();
        report.append("=".repeat(60)).append("\n");
        report.append("                    DOCTOR SUMMARY REPORT\n");
        report.append("=".repeat(60)).append("\n");
        report.append("Report Generated: ").append(getCurrentDate()).append("\n");
        report.append("Total Doctors: ").append(totalDoctors).append("\n");
        report.append("Available Doctors: ").append(availableDoctors).append("\n");
        report.append("Unavailable Doctors: ").append(unavailableDoctors).append("\n");
        report.append("Average Consultation Fee: RM ").append(String.format("%.2f", averageFee)).append("\n");
        report.append("Minimum Consultation Fee: RM ").append(String.format("%.2f", minFee)).append("\n");
        report.append("Maximum Consultation Fee: RM ").append(String.format("%.2f", maxFee)).append("\n");
        report.append("Total Consultation Fees: RM ").append(String.format("%.2f", totalConsultationFee)).append("\n");
        report.append("=".repeat(60));

        return report.toString();
    }

    /**
     * Generates a specialization distribution report
     * 
     * @param doctors List of all doctors
     * @return Formatted specialization report string
     */
    public static String generateSpecializationReport(ListInterface<Doctor> doctors) {
        if (doctors.isEmpty()) {
            return "No doctors found in the system.";
        }

        Map<String, Integer> specializationCount = new HashMap<>();
        Map<String, Double> specializationTotalFee = new HashMap<>();

        for (int i = 1; i <= doctors.getNumberOfEntries(); i++) {
            Doctor doctor = doctors.getEntry(i);
            if (doctor != null) {
                String spec = doctor.getSpecialization();
                specializationCount.put(spec, specializationCount.getOrDefault(spec, 0) + 1);
                specializationTotalFee.put(spec, specializationTotalFee.getOrDefault(spec, 0.0) + doctor.getConsultationFee());
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("=".repeat(60)).append("\n");
        report.append("                SPECIALIZATION DISTRIBUTION REPORT\n");
        report.append("=".repeat(60)).append("\n");
        report.append("Report Generated: ").append(getCurrentDate()).append("\n\n");

        for (Map.Entry<String, Integer> entry : specializationCount.entrySet()) {
            String spec = entry.getKey();
            int count = entry.getValue();
            double totalFee = specializationTotalFee.get(spec);
            double avgFee = totalFee / count;

            report.append("Specialization: ").append(spec).append("\n");
            report.append("Number of Doctors: ").append(count).append("\n");
            report.append("Average Consultation Fee: RM ").append(String.format("%.2f", avgFee)).append("\n");
            report.append("Total Consultation Fees: RM ").append(String.format("%.2f", totalFee)).append("\n");
            report.append("-".repeat(40)).append("\n");
        }

        report.append("=".repeat(60));
        return report.toString();
    }

    /**
     * Generates a consultation fee analysis report
     * 
     * @param doctors List of all doctors
     * @return Formatted fee analysis report string
     */
    public static String generateFeeAnalysisReport(ListInterface<Doctor> doctors) {
        if (doctors.isEmpty()) {
            return "No doctors found in the system.";
        }

        double totalFees = 0.0;
        double minFee = Double.MAX_VALUE;
        double maxFee = 0.0;

        for (int i = 1; i <= doctors.getNumberOfEntries(); i++) {
            Doctor doctor = doctors.getEntry(i);
            if (doctor != null) {
                double fee = doctor.getConsultationFee();
                totalFees += fee;
                if (fee < minFee) minFee = fee;
                if (fee > maxFee) maxFee = fee;
            }
        }

        double averageFee = totalFees / doctors.getNumberOfEntries();

        StringBuilder report = new StringBuilder();
        report.append("=".repeat(60)).append("\n");
        report.append("                    CONSULTATION FEE ANALYSIS REPORT\n");
        report.append("=".repeat(60)).append("\n");
        report.append("Report Generated: ").append(getCurrentDate()).append("\n\n");
        report.append("Total Doctors: ").append(doctors.getNumberOfEntries()).append("\n");
        report.append("Total Consultation Fees: RM ").append(String.format("%.2f", totalFees)).append("\n");
        report.append("Average Consultation Fee: RM ").append(String.format("%.2f", averageFee)).append("\n");
        report.append("Minimum Consultation Fee: RM ").append(String.format("%.2f", minFee)).append("\n");
        report.append("Maximum Consultation Fee: RM ").append(String.format("%.2f", maxFee)).append("\n");
        report.append("=".repeat(60));
        return report.toString();
    }

    /**
     * Generates a detailed doctor listing report
     * 
     * @param doctors List of all doctors
     * @return Formatted detailed listing report string
     */
    public static String generateDetailedListingReport(ListInterface<Doctor> doctors) {
        if (doctors.isEmpty()) {
            return "No doctors found in the system.";
        }

        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("                        DETAILED DOCTOR LISTING\n");
        report.append("=".repeat(80)).append("\n");
        report.append("Report Generated: ").append(getCurrentDate()).append("\n\n");

        for (int i = 1; i <= doctors.getNumberOfEntries(); i++) {
            Doctor doctor = doctors.getEntry(i);
            if (doctor != null) {
                report.append("Doctor ").append(i).append(":\n");
                report.append(doctor.toString()).append("\n");
                report.append("=".repeat(80)).append("\n");
            }
        }

        return report.toString();
    }

    /**
     * Generates a doctor availability report
     * 
     * @param doctors List of all doctors
     * @return Formatted availability report string
     */
    public static String generateAvailabilityReport(ListInterface<Doctor> doctors) {
        if (doctors.isEmpty()) {
            return "No doctors found in the system.";
        }

        int availableDoctors = 0;
        int unavailableDoctors = 0;
        double availableTotalFee = 0.0;
        double unavailableTotalFee = 0.0;

        for (int i = 1; i <= doctors.getNumberOfEntries(); i++) {
            Doctor doctor = doctors.getEntry(i);
            if (doctor != null) {
                if (doctor.isAvailable()) {
                    availableDoctors++;
                    availableTotalFee += doctor.getConsultationFee();
                } else {
                    unavailableDoctors++;
                    unavailableTotalFee += doctor.getConsultationFee();
                }
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("=".repeat(60)).append("\n");
        report.append("                    DOCTOR AVAILABILITY REPORT\n");
        report.append("=".repeat(60)).append("\n");
        report.append("Report Generated: ").append(getCurrentDate()).append("\n\n");
        report.append("Total Doctors: ").append(availableDoctors + unavailableDoctors).append("\n");
        report.append("Available Doctors: ").append(availableDoctors).append("\n");
        report.append("Unavailable Doctors: ").append(unavailableDoctors).append("\n");
        report.append("Availability Rate: ").append(String.format("%.1f", (double) availableDoctors / (availableDoctors + unavailableDoctors) * 100)).append("%\n");
        
        if (availableDoctors > 0) {
            report.append("Average Fee (Available): RM ").append(String.format("%.2f", availableTotalFee / availableDoctors)).append("\n");
        }
        if (unavailableDoctors > 0) {
            report.append("Average Fee (Unavailable): RM ").append(String.format("%.2f", unavailableTotalFee / unavailableDoctors)).append("\n");
        }
        
        report.append("=".repeat(60));
        return report.toString();
    }

    /**
     * Gets the current date in dd/MM/yyyy format
     * 
     * @return Current date string
     */
    private static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
