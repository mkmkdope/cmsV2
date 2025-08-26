package dao;

import adt.CircularDoublyLinkedList;
import entity.Doctor;
//import java.util.Comparator;

public class DoctorDAO {

    private CircularDoublyLinkedList<Doctor> doctorList;
    private DutySchedule dutySchedule;
    private static final int DAYS = 5; // assume working days is mon-fri, sat is OT.
    private static final int MAX_SLOTS = 10; // assume each days 10 slot of doctor is maximum.

    public DoctorDAO() {
        doctorList = new CircularDoublyLinkedList<>();
        dutySchedule = new DutySchedule(doctorList);
        dutySchedule.generateBaseSchedule();
    }

    public void addDoctor(Doctor doctor) {
        doctorList.add(doctor);
        dutySchedule.rearrangeSchedule();
    }

    public Doctor findDoctor(String doctorId) {
        for (int i = 1; i <= doctorList.size(); i++) {
            Doctor doctor = doctorList.getEntry(i);
            if (doctor.getDoctorId().equals(doctorId)) {
                return doctor;
            }
        }
        return null;
    }

    public void updateDoctorAvailability(String doctorId, boolean isAvailable) {
        Doctor doctor = findDoctor(doctorId);
        if (doctor != null) {
            doctor.setAvailable(isAvailable);
            dutySchedule.rearrangeSchedule();
        }
    }

    public boolean updateDoctor(String doctorId, String newName, String newSpecialization, String dutySchedule) {
        Doctor doctor = findDoctor(doctorId);
        if (doctor != null) {
            doctor.setName(newName);
            doctor.setSpecialization(newSpecialization);
            return true;
        }
        return false;
    }

    public void removeDoctor(String doctorId) {
        Doctor doctor = findDoctor(doctorId);
        if (doctor != null) {
            doctorList.remove(doctor);
            dutySchedule.rearrangeSchedule();
        }
    }

    public Doctor[] getAllDoctors() {
        Doctor[] doctors = new Doctor[doctorList.size()];
        for (int i = 0; i < doctorList.size(); i++) {
            doctors[i] = doctorList.getEntry(i + 1);
        }
        return doctors;
    }

    private void insertionSortById(Doctor[] doctors) {
        for (int i = 1; i < doctors.length; i++) {
            Doctor key = doctors[i];
            int j = i - 1;

            while (j >= 0 && doctors[j].getDoctorId().compareTo(key.getDoctorId()) > 0) {
                doctors[j + 1] = doctors[j];
                j = j - 1;
            }
            doctors[j + 1] = key;
        }
    }

    public Doctor[] getAvailableDoctors() {
        Doctor[] allDoctors = getAllDoctors();
        int availableCount = 0;

        for (Doctor doctor : allDoctors) {
            if (doctor.isAvailable()) {
                availableCount++;
            }
        }

        Doctor[] availableDoctors = new Doctor[availableCount];
        int index = 0;

        for (Doctor doctor : allDoctors) {
            if (doctor.isAvailable()) {
                availableDoctors[index++] = doctor;
            }
        }

        return availableDoctors;
    }

    public int getDoctorCount() {
        return doctorList.size();
    }

    public boolean addDoctorToDutyDay(int dayIndex, String doctorId) {
        if (dayIndex < 0 || dayIndex >= 5) {
            System.out.println("Error: Day index must be between 0-4 (Mon-Fri)");
            return false;
        }

        Doctor doctor = findDoctor(doctorId);
        if (doctor == null) {
            System.out.println("Error: Doctor with ID " + doctorId + " not found.");
            return false;
        }

        if (!doctor.isAvailable()) {
            System.out.println("Error: Doctor " + doctor.getName() + " is not available for duty.");
            return false;
        }

        boolean success = dutySchedule.addDoctorToDay(dayIndex, doctor);
        if (success) {
            System.out.println("Successfully added doctor to duty day.");
        }
        return success;
    }

    public boolean removeDoctorFromDutyDay(int dayIndex, String doctorId) {
        if (dayIndex < 0 || dayIndex >= 5) {
            System.out.println("Error: Day index must be between 0-4 (Mon-Fri)");
            return false;
        }

        Doctor doctor = findDoctor(doctorId);
        if (doctor == null) {
            System.out.println("Error: Doctor with ID " + doctorId + " not found.");
            return false;
        }

        boolean success = dutySchedule.removeDoctorFromDay(dayIndex, doctorId);
        if (success) {
            System.out.println("Successfully removed doctor from duty day.");
        }
        return success;
    }

    //Not Using
    public void displayDutySchedule() {
        dutySchedule.displayWeeklySchedule();
    }

    public void rearrangeDutySchedule() {
        dutySchedule.rearrangeSchedule();
    }

    public Doctor[] getDoctorsForDutyDay(int dayIndex) {
        return dutySchedule.getDoctorsForDay(dayIndex);
    }

    public int getTotalScheduledDoctors() {
        return dutySchedule.getTotalScheduledDoctors();
    }

    public void regenerateSchedule() {
        dutySchedule.generateBaseSchedule();
    }

    ////////////////////////////// Duty Report //////////////////////////////
public void generateDutyReport() {
        System.out.println("\n--- TARUMT CLINIC MANAGEMENT SYSTEM ---");
        System.out.println("----- Duty Schedule Report ------");
        System.out.println("ID   Name                 Mon Tue Wed Thu Fri Total");
        System.out.println("---------------------------------------------");

        Doctor[] doctors = getDoctorsSortedById();
        int[] dayTotals = new int[5]; // Mon-Fri totals
        int grandTotal = 0;
        int onLeaveCount = 0;

        for (Doctor doctor : doctors) {
            System.out.printf("%-5s %-20s", doctor.getDoctorId(), doctor.getName());

            int doctorTotal = 0;
            for (int day = 0; day < 5; day++) {
                boolean isOnDuty = isDoctorOnDuty(doctor, day);
                boolean isOnLeave = !doctor.isAvailable();

                if (isOnDuty && !isOnLeave) {
                    System.out.print("  o ");
                    doctorTotal++;
                    dayTotals[day]++;
                    grandTotal++;
                } else if (isOnLeave) {
                    System.out.print("  x ");
                } else {
                    System.out.print("    ");
                }
            }
            System.out.printf(" %2d%n", doctorTotal);

            if (!doctor.isAvailable()) {
                onLeaveCount++;
            }
        }

        System.out.println("---------------------------------------------");
        System.out.println("o = OnDuty, x = OnLeave");
        System.out.printf("Mon: %d | Tue: %d | Wed: %d | Thu: %d | Fri: %d%n",
                dayTotals[0], dayTotals[1], dayTotals[2], dayTotals[3], dayTotals[4]);
        System.out.println("Total Scheduled Duties: " + grandTotal);

        int availableDoctors = doctors.length - onLeaveCount;
        System.out.println("Doctors available: " + availableDoctors);
        System.out.println("Doctors on Leave: " + onLeaveCount);
        System.out.println("Total Doctors: " + doctors.length);
        System.out.println("\n----- End Of Report -----\n");
    }

    public Doctor[] getDoctorsSortedById() {
        Doctor[] doctors = getAllDoctors();
        insertionSortById(doctors);
        return doctors;
    }

    private boolean isDoctorOnDuty(Doctor doctor, int dayIndex) {
        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            if (dutySchedule.getWeeklySchedule()[dayIndex][slot] != null
                    && dutySchedule.getWeeklySchedule()[dayIndex][slot].equals(doctor)) {
                return true;
            }
        }
        return false;
    }

    ////////////////////////////// End of Report //////////////////////////////



    ////////////////////////////// Workload Report //////////////////////////////
public void generateWorkloadReport() {
        System.out.println("\n--- TARUMT CLINIC MANAGEMENT SYSTEM ---");
        System.out.println("----- Workload Analysis Report -----");

        System.out.println("ID    Doctor                Duties  Status      Workload %  Chart (| = 2%)");
        System.out.println("-----------------------------------------------------------------------");

        Doctor[] doctors = getAllDoctors();
        DoctorWorkload[] workloads = new DoctorWorkload[doctors.length];

        // Calculate workloads and total duties
        int totalDuties = 0;
        for (int i = 0; i < doctors.length; i++) {
            int dutyCount = countDuties(doctors[i]);
            workloads[i] = new DoctorWorkload(doctors[i], dutyCount);
            totalDuties += dutyCount;
        }

        // Sort by duty count (descending)
        bubbleSortWorkloads(workloads);

        int maxDuties = 0;
        String busiestDoctor = "";

        // Find max duties for identification
        for (DoctorWorkload wl : workloads) {
            if (wl.dutyCount > maxDuties) {
                maxDuties = wl.dutyCount;
                busiestDoctor = wl.doctor.getName();
            }
        }

        // Display each doctor with barchart
        for (DoctorWorkload wl : workloads) {
            String status = wl.doctor.isAvailable() ? "Available" : "On Leave";
            double workloadPercentage = totalDuties > 0
                    ? (double) wl.dutyCount / totalDuties * 100 : 0;
            String barChart = generateBarChart(workloadPercentage, 20); // 20 character width

            System.out.printf("%-5s %-20s %4d    %-10s %6.1f%%   %s%n",
                    wl.doctor.getDoctorId(),
                    wl.doctor.getName(),
                    wl.dutyCount,
                    status,
                    workloadPercentage,
                    barChart);
        }

        double averageDuties = doctors.length > 0 ? (double) totalDuties / doctors.length : 0;

        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Workload % = (Individual Duties / Total Duties) * 100%");
        System.out.printf("Total Duties: %d%n", totalDuties);
        System.out.printf("Average Duties: %.1f%n", averageDuties);
        System.out.printf("Busiest Doctor: %s (%d duties)%n", busiestDoctor, maxDuties);

        displayWorkloadDistribution(workloads);

        System.out.println("\n----- End of Report -----\n");
    }

    private String generateBarChart(double workloadPercentage, int maxWidth) {
        if (workloadPercentage == 0) {
            return " ".repeat(maxWidth);
        }

        // Each | character represents 2% of total workload
        int barLength = (int) Math.round(workloadPercentage / 2);

        // Ensure bar doesn't exceed max width
        if (barLength > maxWidth) {
            barLength = maxWidth;
        }

        StringBuilder bar = new StringBuilder();

        // Add | characters
        for (int i = 0; i < barLength; i++) {
            bar.append("|");
        }

        // Add empty space for the rest
        for (int i = barLength; i < maxWidth; i++) {
            bar.append(" ");
        }

        return bar.toString();
    }

    private void bubbleSortWorkloads(DoctorWorkload[] workloads) {
        boolean swapped;
        int n = workloads.length;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                // Sort by duty count descending (highest first)
                if (workloads[j].dutyCount < workloads[j + 1].dutyCount) {
                    // Swap
                    DoctorWorkload temp = workloads[j];
                    workloads[j] = workloads[j + 1];
                    workloads[j + 1] = temp;
                    swapped = true;
                } // If duty counts are equal, sort by doctor ID ascending
                else if (workloads[j].dutyCount == workloads[j + 1].dutyCount) {
                    if (workloads[j].doctor.getDoctorId().compareTo(workloads[j + 1].doctor.getDoctorId()) > 0) {
                        // Swap
                        DoctorWorkload temp = workloads[j];
                        workloads[j] = workloads[j + 1];
                        workloads[j + 1] = temp;
                        swapped = true;
                    }
                }
            }
            // If no swapping occurred, array is sorted
            if (!swapped) {
                break;
            }
        }
    }

    private void displayWorkloadDistribution(DoctorWorkload[] workloads) {
        int[] distribution = new int[6]; // 0, 1-2, 3-4, 5-6, 7-8, 9+ duties

        for (DoctorWorkload wl : workloads) {
            if (wl.dutyCount == 0) {
                distribution[0]++;
            } else if (wl.dutyCount <= 2) {
                distribution[1]++;
            } else if (wl.dutyCount <= 4) {
                distribution[2]++;
            } else if (wl.dutyCount <= 6) {
                distribution[3]++;
            } else if (wl.dutyCount <= 8) {
                distribution[4]++;
            } else {
                distribution[5]++;
            }
        }

        System.out.println("\nWorkload Distribution:");
        System.out.println("0 duties:    " + distribution[0] + " doctors");
        System.out.println("1-2 duties:  " + distribution[1] + " doctors");
        System.out.println("3-4 duties:  " + distribution[2] + " doctors");
        System.out.println("5-6 duties:  " + distribution[3] + " doctors");
        System.out.println("7-8 duties:  " + distribution[4] + " doctors");
        System.out.println("9+ duties:   " + distribution[5] + " doctors");
    }

    ////////////////////////////// End of workload report //////////////////////////////

private int countDuties(Doctor doctor) {
        int count = 0;
        for (int day = 0; day < DAYS; day++) {
            for (int slot = 0; slot < MAX_SLOTS; slot++) {
                if (dutySchedule.getWeeklySchedule()[day][slot] != null
                        && dutySchedule.getWeeklySchedule()[day][slot].equals(doctor)) {
                    count++;
                }
            }
        }
        return count;
    }

//    private void mergeSort(DoctorWorkload[] array, Comparator<DoctorWorkload> comparator) {
//        if (array.length <= 1) {
//            return;
//        }
//
//        int mid = array.length / 2;
//        DoctorWorkload[] left = new DoctorWorkload[mid];
//        DoctorWorkload[] right = new DoctorWorkload[array.length - mid];
//
//        for (int i = 0; i < mid; i++) {
//            left[i] = array[i];
//        }
//        for (int i = mid; i < array.length; i++) {
//            right[i - mid] = array[i];
//        }
//
//        mergeSort(left, comparator);
//        mergeSort(right, comparator);
//
//        merge(array, left, right, comparator);
//    }
//    private void merge(DoctorWorkload[] result, DoctorWorkload[] left, DoctorWorkload[] right, Comparator<DoctorWorkload> comparator) {
//        int i = 0, j = 0, k = 0;
//
//        while (i < left.length && j < right.length) {
//            if (comparator.compare(left[i], right[j]) <= 0) {
//                result[k++] = left[i++];
//            } else {
//                result[k++] = right[j++];
//            }
//        }
//
//        while (i < left.length) {
//            result[k++] = left[i++];
//        }
//        while (j < right.length) {
//            result[k++] = right[j++];
//        }
//    }
    ////////////////////////////// Specialty Report ////////////////////////////// 

    public void generateSpecialtyReport() {
        System.out.println("\n--- TARUMT CLINIC MANAGEMENT SYSTEM ---");
        System.out.println("----- Specialty Availability Report -----");
        System.out.println("MON TUE WED THU FRI  SPECIALTY       COVERAGE");
        System.out.println("---------------------------------------------");

        Doctor[] doctors = getAllDoctors();
        String[] specialties = extractUniqueSpecialties(doctors);
        SpecialtySummary[] summaries = new SpecialtySummary[specialties.length];

        // Initialize summaries
        for (int i = 0; i < specialties.length; i++) {
            summaries[i] = new SpecialtySummary(specialties[i]);
        }

        // Populate data
        for (Doctor doctor : doctors) {
            for (SpecialtySummary summary : summaries) {
                if (summary.specialty.equals(doctor.getSpecialization())) {
                    summary.totalCount++;
                    if (doctor.isAvailable()) {
                        summary.availableCount++;
                    }
                    // Count duties for each day
                    for (int day = 0; day < DAYS; day++) {
                        if (isDoctorOnDuty(doctor, day)) {
                            summary.dailyCoverage[day]++;
                        }
                    }
                    break;
                }
            }
        }

        // Sort by coverage rate (descending)
        bubbleSortSpecialtiesByCoverage(summaries);

        // Display matrix
        for (SpecialtySummary summary : summaries) {
            // Display daily coverage symbols
            for (int day = 0; day < DAYS; day++) {
                String symbol = getCoverageSymbol(summary, day);
                System.out.print(symbol + "   ");
            }

            // Calculate coverage percentage
            int totalPossibleSlots = summary.totalCount * DAYS;
            int actualCoverage = 0;
            for (int day = 0; day < DAYS; day++) {
                actualCoverage += summary.dailyCoverage[day];
            }
            double coverageRate = totalPossibleSlots > 0
                    ? (double) actualCoverage / totalPossibleSlots * 100 : 0;

            System.out.printf("  %-15s %3.0f%% (%d/%d)%n",
                    summary.specialty, coverageRate, actualCoverage, totalPossibleSlots);
        }

        System.out.println("---------------------------------------------");
        System.out.println("G = Fully covered  X = Understaffed  / = Partial");
        System.out.println("----- End of Report -----\n");
    }

    private String getCoverageSymbol(SpecialtySummary summary, int day) {
        int doctorsInSpecialty = summary.totalCount;
        int doctorsScheduled = summary.dailyCoverage[day];

        if (doctorsInSpecialty == 0) {
            return "x"; // No doctors in this specialty
        }

        double coverageRatio = (double) doctorsScheduled / doctorsInSpecialty;

        if (coverageRatio >= 0.8) {
            return "G"; // Good coverage (80-100%)
        } else if (coverageRatio >= 0.4) {
            return "/"; // Partial coverage (40-79%)
        } else {
            return "X"; // Poor coverage (0-39%)
        }
    }

    private void bubbleSortSpecialtiesByCoverage(SpecialtySummary[] summaries) {
        boolean swapped;
        int n = summaries.length;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                // Calculate coverage rates
                double coverage1 = calculateTotalCoverageRate(summaries[j]);
                double coverage2 = calculateTotalCoverageRate(summaries[j + 1]);

                if (coverage1 < coverage2) {
                    // Swap
                    SpecialtySummary temp = summaries[j];
                    summaries[j] = summaries[j + 1];
                    summaries[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    private double calculateTotalCoverageRate(SpecialtySummary summary) {
        int totalPossibleSlots = summary.totalCount * DAYS;
        if (totalPossibleSlots == 0) {
            return 0;
        }

        int actualCoverage = 0;
        for (int day = 0; day < DAYS; day++) {
            actualCoverage += summary.dailyCoverage[day];
        }

        return (double) actualCoverage / totalPossibleSlots * 100;
    }

    class SpecialtySummary {

        String specialty;
        int availableCount;
        int totalCount;
        int[] dailyCoverage; // Tracks coverage for each day (0-4 for Mon-Fri)

        SpecialtySummary(String specialty) {
            this.specialty = specialty;
            this.availableCount = 0;
            this.totalCount = 0;
            this.dailyCoverage = new int[DAYS]; // Initialize with zeros
        }
    }

// Helper method to extract unique specialties
    private String[] extractUniqueSpecialties(Doctor[] doctors) {
        String[] temp = new String[doctors.length];
        int uniqueCount = 0;

        for (Doctor doctor : doctors) {
            boolean found = false;
            for (int i = 0; i < uniqueCount; i++) {
                if (temp[i].equals(doctor.getSpecialization())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                temp[uniqueCount++] = doctor.getSpecialization();
            }
        }

        String[] result = new String[uniqueCount];
        for (int i = 0; i < uniqueCount; i++) {
            result[i] = temp[i];
        }
        return result;
    }

    ////////////////////////////// End of Specialty Report ////////////////////////////// 


    // Helper classes
    class DoctorWorkload {

        Doctor doctor;
        int dutyCount;

        DoctorWorkload(Doctor doctor, int dutyCount) {
            this.doctor = doctor;
            this.dutyCount = dutyCount;
        }
    }

}
