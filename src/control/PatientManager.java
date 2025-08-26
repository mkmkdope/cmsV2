/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import boundary.Messages;
import dao.PatientDAO;
import entity.Patient;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author USER
 */
public class PatientManager {

    private PatientDAO dao;
    private ListInterface<Patient> waitingQueue = new CircularDoublyLinkedList<>();

    public PatientManager(PatientDAO dAO) {
        this.dao = dAO;
    }

    public boolean registerPatient(String ic, String name, String gender, int age,
            String phone, String address, String email, String history) {
        if (!ic.matches("\\d{12}")) {
            System.out.println(Messages.INVALID_IC);
            return false;
        }
        if (dao.findPatientByIC(ic) != null) {
            System.out.println(Messages.DUPLICATE_IC);
            return false;
        }
        if (age <= 0 || age > 120) {
            System.out.println(Messages.INVALID_AGE);
            return false;
        }
        if (!phone.matches("\\d{12}")) {
            System.out.println(Messages.INVALID_PHONE);
            return false;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            System.out.println(Messages.INVALID_EMAIL);
            return false;
        }

        String newId = dao.generatePatientId();
        Patient p = new Patient(newId, ic, name, gender, age, phone, address, email, history);
        boolean success = dao.addPatient(p);
        if (!success) {
            System.out.println(Messages.DUPLICATE_ID);
        }
        return success;
    }

    public boolean registerPatientWithPriority(String ic, String name, String gender, int age,
            String phone, String address, String email, String history, int priority) {
        if (!ic.matches("\\d{12}")) {
            System.out.println(Messages.INVALID_IC);
            return false;
        }
        if (dao.findPatientByIC(ic) != null) {
            System.out.println(Messages.DUPLICATE_IC);
            return false;
        }
        if (age <= 0 || age > 120) {
            System.out.println(Messages.INVALID_AGE);
            return false;
        }
        if (!phone.matches("\\d{12}")) {
            System.out.println(Messages.INVALID_PHONE);
            return false;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            System.out.println(Messages.INVALID_EMAIL);
            return false;
        }

        if (priority < 1 || priority > 5) {
            System.out.println("Invalid priority. Priority must be between 1(highest) and 5 (lowest). ");
            return false;
        }

        String newId = dao.generatePatientId();
        Patient p = new Patient(newId, ic, name, gender, age, phone, address, email, history);
        boolean success = dao.addPatientWithPriority(p, priority);
        if (!success) {
            System.out.println(Messages.DUPLICATE_ID);
        }
        return success;
    }

    public void displayAllPatients() {
        ListInterface<Patient> list = dao.getAllPatients();

        if (list.isEmpty()) {
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return;
        }
        System.out.println(Messages.PATIENT_TABLE_LINE);
        System.out.printf(Messages.PATIENT_TABLE_FORMAT, "ID", "IC Number", "Name", "Gender", "Age", "Phone", "Address", "Email", "Medical History");
        System.out.println(Messages.PATIENT_TABLE_LINE);

        for (Patient patient : list) {
            System.out.printf(Messages.PATIENT_TABLE_FORMAT,
                    patient.getPatientId(), patient.getIcNumber(), patient.getName(),
                    patient.getGender(), patient.getAge(), patient.getPhoneNumber(),
                    patient.getAddress(), patient.getEmail(), patient.getMedicalHistory());
        }

        System.out.println(Messages.PATIENT_TABLE_LINE);
    }

    //search patient by ID or IC
    public void searchPatient(String keyword) {
        Patient patient = dao.findPatientById(keyword);
        if (patient == null) {
            patient = dao.findPatientByIC(keyword);
        }
        if (patient == null) {
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return;
        }

        System.out.println(Messages.PATIENT_TABLE_LINE);
        System.out.printf(Messages.PATIENT_TABLE_FORMAT, "ID", "IC Number", "Name", "Gender", "Age", "Phone", "Address", "Email", "Medical History");
        System.out.println(Messages.PATIENT_TABLE_LINE);
        System.out.printf(Messages.PATIENT_TABLE_FORMAT,
                patient.getPatientId(), patient.getIcNumber(), patient.getName(), patient.getGender(), patient.getAge(),
                patient.getPhoneNumber(), patient.getAddress(), patient.getEmail(), patient.getMedicalHistory());
        System.out.println(Messages.PATIENT_TABLE_LINE);
    }

    public Patient getPatientById(String id) {
        return dao.findPatientById(id);
    }

    public Patient getPatientByIC(String ic) {
        return dao.findPatientByIC(ic);
    }

    public boolean updatePatient(String id, String ic, String name, String gender, int age, String phone, String address, String email, String history) {
        Patient existing = dao.findPatientById(id);
        if (existing == null) {
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return false;
        }

        Patient updated = new Patient(id, ic, name, gender, age, phone, address, email, history);
        boolean success = dao.updatePatient(updated);
        if (success) {
            System.out.println(Messages.PATIENT_UPDATED);
            return true;
        } else {
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return false;
        }
    }

    public boolean deletePatient(String id) {
        boolean success = dao.deletePatient(id);

        if (success) {
            System.out.println(Messages.PATIENT_DELETED);
        } else {
            System.out.println(Messages.PATIENT_NOT_FOUND);
        }
        return success;
    }

    public boolean addPatientToQueue(String patientId) {
        Patient p = dao.findPatientById(patientId);

        if (p == null) {
            return false;
        }
        return waitingQueue.add(p);
    }

    public boolean addPatientToQueueWithPriority(String patientId, int priority) {
        Patient p = dao.findPatientById(patientId);

        if (p == null) {
            return false;
        }
        if (priority < 1 || priority > 5) {
            System.out.println("Invalid priority. Priority must be between 1 (highest) and 5 (lowest).");
            return false;
        }

        PatientWithPriority patientWithPriority = new PatientWithPriority(p, priority);

        Comparator<PatientWithPriority> priorityComparator = (p1, p2)
                -> Integer.compare(p1.priority, p2.priority);

        ListInterface<PatientWithPriority> priorityQueue = new CircularDoublyLinkedList<>();

        for (Patient patient : waitingQueue) {
            priorityQueue.add(new PatientWithPriority(patient, 5));
        }

        priorityQueue.addWithPriority(priorityComparator, patientWithPriority);

        waitingQueue.clear();
        for (PatientWithPriority pwp : priorityQueue) {
            waitingQueue.add(pwp.patient);
        }

        return true;
    }

    private static class PatientWithPriority {

        Patient patient;
        int priority;

        PatientWithPriority(Patient patient, int priority) {
            this.patient = patient;
            this.priority = priority;
        }
    }

    public Patient serveNextPatient() {
        if (waitingQueue.isEmpty()) {
            return null;
        }

        return waitingQueue.remove(1); // Remove and return the first patient in the queue (FIFO)
    }

    public void generateReports() {
        ListInterface<Patient> list = dao.getAllPatients();

        if (list.isEmpty()) {
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return;
        }

        // Header
        printReportHeader();

        // Summary Statistics
        printSummaryStatistics(list);

        // Detailed Patient Table
        printDetailedPatientTable(list);

        // Demographics Analysis
        printDemographicsAnalysis(list);

        // Graphical Representations
        printGraphicalAnalysis(list);

        // Additional Insights
        printAdditionalInsights(list);

        printReportFooter();
    }

    private void printReportHeader() {
        String separator = "=".repeat(120);
        System.out.println(separator);
        System.out.println(centerText("HOSPITAL PATIENT MANAGEMENT SYSTEM", 120));
        System.out.println(centerText("COMPREHENSIVE PATIENT REPORT", 120));
        System.out.println(centerText("Generated at: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 120));
        System.out.println(separator);
        System.out.println("HOSPITAL PATIENT MANAGEMENT SYSTEM HIGHLY CONFIDENTIAL DOCUMENT");
        System.out.println(separator);
    }

    private void printSummaryStatistics(ListInterface<Patient> list) {
        System.out.println("\n=== SUMMARY OF PATIENT STATISTICS ===");
        System.out.println("Total Number of Registered Patients: " + list.getNumberOfEntries());
        System.out.println("Waiting Queue Size: " + waitingQueue.getNumberOfEntries());
        System.out.println("Report Generation Date: " + java.time.LocalDate.now());
        System.out.println("-".repeat(50));
    }

    private void printDetailedPatientTable(ListInterface<Patient> list) {
        System.out.println("\n=== DETAILED PATIENT INFORMATION ===");
        System.out.printf("%-8s | %-15s | %-20s | %-8s | %-5s | %-12s | %-20s\n",
                "ID", "IC Number", "Name", "Gender", "Age", "Phone", "Location");
        System.out.println("-".repeat(95));

        for (Patient patient : list) {
            System.out.printf("%-8s | %-15s | %-20s | %-8s | %-5d | %-12s | %-20s\n",
                    patient.getPatientId(), patient.getIcNumber(), patient.getName(),
                    patient.getGender(), patient.getAge(), patient.getPhoneNumber(), patient.getAddress());
        }
        System.out.println("-".repeat(95));
    }

    private void printDemographicsAnalysis(ListInterface<Patient> list) {
        System.out.println("\n=== DEMOGRAPHICS ANALYSIS ===");

        final int[] genderCount = {0, 0, 0}; // male, female, other
        final int[] ageGroups = {0, 0, 0, 0, 0}; // under18, 18-30, 31-50, 51-65, over65

        ListInterface<LocationCount> locCounts = new CircularDoublyLinkedList<>();

        for (Patient patient : list) {
            // Gender counting
            String gender = (patient.getGender() == null ? "" : patient.getGender()).trim().toLowerCase();
            if (gender.contains("female")) {
                genderCount[1]++;
            } else if (gender.contains("male")) {
                genderCount[0]++;
            } else {
                genderCount[2]++;
            }

            // Age grouping
            int age = patient.getAge();
            if (age < 18) {
                ageGroups[0]++;
            } else if (age <= 30) {
                ageGroups[1]++;
            } else if (age <= 50) {
                ageGroups[2]++;
            } else if (age <= 65) {
                ageGroups[3]++;
            } else {
                ageGroups[4]++;
            }

            // Location counting
            upsertLocationCount(locCounts, safeCity(patient.getAddress()));
        }

        int n = Math.max(1, list.getNumberOfEntries());  //Avoid to divide zero

        // Print Gender Statistics
        System.out.println("\nGender Distribution:");
        System.out.printf("Male: %d (%.1f%%)\n", genderCount[0], (genderCount[0] * 100.0 / n));
        System.out.printf("Female: %d (%.1f%%)\n", genderCount[1], (genderCount[1] * 100.0 / n));
        if (genderCount[2] > 0) {
            System.out.printf("Other: %d (%.1f%%)\n", genderCount[2], (genderCount[2] * 100.0 / n));
        }

        // Print Age Statistics
        System.out.println("\nAge Distribution:");
        System.out.printf("Under 18: %d patients\n", ageGroups[0]);
        System.out.printf("18-30: %d patients\n", ageGroups[1]);
        System.out.printf("31-50: %d patients\n", ageGroups[2]);
        System.out.printf("51-65: %d patients\n", ageGroups[3]);
        System.out.printf("Over 65: %d patients\n", ageGroups[4]);

        // Print Top Locations
        System.out.println("\nTop Patient Locations:");
        printTopLocations(locCounts, 5);
    }

    //print graph
    private void printGraphicalAnalysis(ListInterface<Patient> list) {
        System.out.println("\n=== GRAPHICAL REPRESENTATION ===");

        final int[] genderCount = {0, 0}; // male, female
        final int[] ageGroups = {0, 0, 0, 0}; // <18, 18-30, 31-50, 51+

        ListInterface<LocationCount> locCounts = new CircularDoublyLinkedList<>();

        for (Patient patient : list) {
            String gender = (patient.getGender() == null ? "" : patient.getGender()).trim();
            if (gender.equalsIgnoreCase("Male")) {
                genderCount[0]++;
            } else if (gender.equalsIgnoreCase("Female")) {
                genderCount[1]++;
            }

            int age = patient.getAge();
            if (age < 18) {
                ageGroups[0]++;
            } else if (age <= 30) {
                ageGroups[1]++;
            } else if (age <= 50) {
                ageGroups[2]++;
            } else {
                ageGroups[3]++;
            }

            upsertLocationCount(locCounts, safeCity(patient.getAddress()));
        }

        final int maxHeight = 10;
        int totalGender = genderCount[0] + genderCount[1];
        int totalAge = 0;
        for (int i = 0; i < ageGroups.length; i++) {
            totalAge += ageGroups[i];
        }

        // Prepare data for both charts
        String[] genderLabels = {"Male", "Female"};
        String[] genderColors = {Messages.BLUE, Messages.MAGENTA};

        String[] ageLabels = {"<18", "18-30", "31-50", "51+"};
        String[] ageColors = {Messages.GREEN, Messages.YELLOW, Messages.CYAN, Messages.RED};

        // Print headers with proper spacing
        System.out.println("Gender Distribution (percentage)            |         Age Distribution (percentage)");
        System.out.println("                                            |");

        for (int row = maxHeight; row >= 1; row--) {
            int yPct = row * 10;

            System.out.printf("%3d |", yPct);
            for (int i = 0; i < genderCount.length; i++) {
                int scaled = (totalGender > 0) ? (int) Math.round(genderCount[i] * 1.0 * maxHeight / totalGender) : 0;
                if (scaled == 0 && genderCount[i] > 0) {
                    scaled = 1; 
                }
                System.out.print(scaled >= row ? ("   " + genderColors[i] + "**" + Messages.RESET) : "     ");
            }

            System.out.print("                             |  "); 

            System.out.printf("%3d |", yPct);
            for (int i = 0; i < ageGroups.length; i++) {
                int scaled = (totalAge > 0) ? (int) Math.round(ageGroups[i] * 1.0 * maxHeight / totalAge) : 0;
                if (scaled == 0 && ageGroups[i] > 0) {
                    scaled = 1;
                }

                System.out.print(scaled >= row ? ("         " + ageColors[i] + "**" + Messages.RESET) : "          ");
            }
            System.out.println();
        }

        System.out.println("    +---------------> Gender                |       +---------------------------------------------> Age Groups");

        System.out.print("      ");
        for (int i = 0; i < genderLabels.length; i++) {
            System.out.printf("%-8s", genderLabels[i]);
        }
        System.out.print("                      |             ");
        for (int i = 0; i < ageLabels.length; i++) {
            System.out.printf("%-12s", ageLabels[i]);
        }
        System.out.println();

        System.out.print("");
        for (int i = 0; i < genderCount.length; i++) {
            double pct = (totalGender > 0) ? (genderCount[i] * 100.0 / totalGender) : 0.0;
            System.out.printf("%-14s", "(" + genderCount[i] + " | " + String.format("%.1f%%", pct) + ")");
        }
        System.out.print("                |         ");
        for (int i = 0; i < ageGroups.length; i++) {
            double pct = (totalAge > 0) ? (ageGroups[i] * 100.0 / totalAge) : 0.0;
            System.out.printf("%-14s", "(" + ageGroups[i] + " | " + String.format("%.1f%%", pct) + ")");
        }
        System.out.println();

    }

    private void printAdditionalInsights(ListInterface<Patient> list) {
        System.out.println("\n=== ADDITIONAL INSIGHTS ===");

        // Calculate statistics using traversal
        final int[] stats = {0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0}; // totalAge, minAge, maxAge, patientsWithHistory

        for (Patient patient : list) {
            int age = patient.getAge();
            stats[0] += age; // totalAge
            if (age < stats[1]) {
                stats[1] = age;
            }
            if (age > stats[2]) {
                stats[2] = age;
            }

            String h = patient.getMedicalHistory();
            if (h != null && !h.trim().isEmpty()) {
                stats[3]++;
            }
        }

        int n = Math.max(1, list.getNumberOfEntries());
        double avg = (double) stats[0] / n;

        System.out.printf("Average Patient Age: %.1f years\n", avg);
        System.out.printf("Age Range: %d - %d years\n", stats[1] == Integer.MAX_VALUE ? 0 : stats[1], stats[2] == Integer.MIN_VALUE ? 0 : stats[2]);
        System.out.printf("Total Registered Patients: %d\n", list.getNumberOfEntries());
        System.out.printf("Patients in Waiting Queue: %d\n", waitingQueue.getNumberOfEntries());
        System.out.printf("Patients with Medical History: %d (%.1f%%)\n", stats[3], stats[3] * 100.0 / n);
    }

    private void printReportFooter() {
        System.out.println("\n" + "=".repeat(120));
        System.out.println(centerText("END OF REPORT", 120));
        System.out.println("*".repeat(120));
    }

    private static class LocationCount {

        String location;
        int count;

        LocationCount(String loc) {
            this.location = loc == null ? "Unknown" : loc;
            this.count = 1;
        }
    }

    private void upsertLocationCount(ListInterface<LocationCount> locCounts, String location) {
        int n = locCounts.getNumberOfEntries();
        for (int i = 1; i <= n; i++) {
            LocationCount lc = locCounts.getEntry(i);
            if (equalsIgnoreCase(lc.location, location)) {
                lc.count++;
                locCounts.replace(i, lc);
                return;
            }
        }
        locCounts.add(new LocationCount(location));
    }

    private void printTopLocations(ListInterface<LocationCount> locCounts, int topK) {
        locCounts.mergeSort((a, b) -> Integer.compare(b.count, a.count));
        int n = Math.min(topK, locCounts.getNumberOfEntries());
        for (int i = 1; i <= n; i++) {
            LocationCount lc = locCounts.getEntry(i);
            printBar(lc.location, lc.count, Messages.CYAN);
        }
    }

    private void printBar(String label, int count, String colour) {
        int barLength = count < 0 ? 0 : Math.min(count, 15);
        System.out.printf("%-15s | %s%s%s (%d)%n", (label == null || label.isBlank()) ? "Unknown" : label, colour, repeat('#', barLength), Messages.RESET, count);
    }

//added for centerText
    private String repeat(char c, int times) {
        if (times <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(times);
        for (int i = 0; i < times; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private boolean equalsIgnoreCase(String a, String b) {
        return (a == b) || (a != null && b != null && a.equalsIgnoreCase(b));
    }

    private String centerText(String text, int width) {
        if (text == null) {
            text = "";
        }
        int padding = (width - text.length()) / 2;
        if (padding < 0) {
            padding = 0;
        }
        return repeat(' ', padding) + text;
    }

    private String nowString(String pattern) {
        return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern(pattern));
    }

    private int maxOf(int[] arr) {
        int m = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > m) {
                m = arr[i];
            }
        }
        return m;
    }

    private String safeCity(String address) {
        if (address == null) {
            return "Unknown";
        }
        String[] parts = address.split(",");
        String city = parts.length > 0 ? parts[0].trim() : address.trim();
        return city.isEmpty() ? "Unknown" : city;
    }

    //////////////////////////////////////////////////////////////////////

    public void displayWaitingQueue() {
        System.out.println("\n=== CURRENT WAITING QUEUE ===");
        if (waitingQueue.isEmpty()) {
            System.out.println("No patients in waiting queue.");
            return;
        }

        System.out.printf("%-10s | %-15s | %-20s | %-8s | %-5s\n",
                "Queue #", "Patient ID", "Name", "Gender", "Age");
        System.out.println("-".repeat(65));

        final int[] counter = {1}; 

        for (Patient patient : waitingQueue) {
            System.out.printf("%-10d | %-15s | %-20s | %-8s | %-5d\n",
                    counter[0], patient.getPatientId(), patient.getName(),
                    patient.getGender(), patient.getAge());
            counter[0]++;
        }

        System.out.println("-".repeat(65));
        System.out.println("Total patients in queue: " + waitingQueue.getNumberOfEntries());
    }

    // Use sorting methods using mergeSort from ADT
    public void sortPatientsByName() {
        dao.getAllPatients().mergeSort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
        System.out.println(Messages.SORT_BY_NAME);
    }

    public void sortPatientsByAge() {
        dao.getAllPatients().mergeSort((p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()));
        System.out.println(Messages.SORT_BY_AGE);
    }

    public void sortPatientsById() {
        dao.getAllPatients().mergeSort((p1, p2) -> {
            int num1 = Integer.parseInt(p1.getPatientId().substring(1));
            int num2 = Integer.parseInt(p2.getPatientId().substring(1));
            return Integer.compare(num1, num2);
        });
        System.out.println("Patients sorted by ID.");
    }

    
    public boolean isPatientInQueue(String patientId) {
        Patient patient = dao.findPatientById(patientId);
        if (patient == null) {
            return false;
        }
        return waitingQueue.contains(patient);
    }

    public boolean removePatientFromQueue(String patientId) {
        Patient patient = dao.findPatientById(patientId);
        if (patient == null) {
            return false;
        }

        for (int i = 1; i <= waitingQueue.getNumberOfEntries(); i++) {
            if (waitingQueue.getEntry(i).getPatientId().equalsIgnoreCase(patientId)) {
                waitingQueue.remove(i);
                return true;
            }
        }
        return false;
    }

    public void displayPatientsReverse() {
        ListInterface<Patient> list = dao.getAllPatients();

        if (list.isEmpty()) {
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return;
        }

        System.out.println(Messages.PATIENT_TABLE_LINE);
        System.out.printf(Messages.PATIENT_TABLE_FORMAT, "ID", "IC Number", "Name", "Gender", "Age", "Phone", "Address", "Email", "Medical History");
        System.out.println(Messages.PATIENT_TABLE_LINE);

        //using reverse iterator to reverse the list
        Iterator<Patient> reverseItr = list.reverseIterator();
        while (reverseItr.hasNext()) {
            Patient patient = reverseItr.next();
            System.out.printf(Messages.PATIENT_TABLE_FORMAT,
                    patient.getPatientId(), patient.getIcNumber(), patient.getName(),
                    patient.getGender(), patient.getAge(), patient.getPhoneNumber(),
                    patient.getAddress(), patient.getEmail(), patient.getMedicalHistory());
        }

        System.out.println(Messages.PATIENT_TABLE_LINE);
    }

    public boolean replacePatientInQueue(int position, String newPatientId) {
        Patient newPatient = dao.findPatientById(newPatientId);
        if (newPatient == null) {
            return false;
        }

        return waitingQueue.replace(position, newPatient);
    }

    public void clearQueue() {
        waitingQueue.clear();
        System.out.println("Waiting queue cleared successfully.");
    }


    public boolean containsPatient(Patient patient) {
        return dao.containsPatient(patient);
    }

    public Patient getPatientAtPosition(int position) {
        return dao.getPatientAtPosition(position);
    }

    public boolean replacePatientAtPosition(int position, Patient newPatient) {
        return dao.replacePatientAtPosition(position, newPatient);
    }

    public boolean insertPatientAtPosition(int position, String ic, String name, String gender, int age,
            String phone, String address, String email, String history) {
        String newId = dao.generatePatientId();
        Patient patient = new Patient(newId, ic, name, gender, age, phone, address, email, history);
        return dao.insertPatientAtPosition(position, patient);
    }
    
       public int getPatientCount() {
        return dao.getPatientCount();
    }


    public Patient searchPatientByAge(int age) {
        return dao.searchPatientByAge(age);
    }

    public int getQueueSize() {
        return waitingQueue.getNumberOfEntries();
    }

    public Patient getPatientAtQueuePosition(int position) {
        if (position < 1 || position > waitingQueue.getNumberOfEntries()) {
            return null;
        }
        return waitingQueue.getEntry(position);
    }

 
    public void updateAllPatientsInLocation(String location, String newLocation) {
        for (Patient patient : dao.getAllPatients()) {
            if (patient.getAddress().toLowerCase().contains(location.toLowerCase())) {
                Patient updated = new Patient(
                        patient.getPatientId(),
                        patient.getIcNumber(),
                        patient.getName(),
                        patient.getGender(),
                        patient.getAge(),
                        patient.getPhoneNumber(),
                        newLocation,
                        patient.getEmail(),
                        patient.getMedicalHistory()
                );
                dao.updatePatient(updated);
            }
        }
        System.out.println("Updated all patients from " + location + " to " + newLocation);
    }

   

    public void displayQueueStatistics() {
        System.out.println("\n=== QUEUE STATISTICS ===");
        System.out.println("Queue Size: " + waitingQueue.getNumberOfEntries());

        if (!waitingQueue.isEmpty()) {
            System.out.println("Next Patient to Serve: " + waitingQueue.getEntry(1).getName());
            System.out.println("Last Patient in Queue: " + waitingQueue.getEntry(waitingQueue.getNumberOfEntries()).getName());
        }
    }

    public void displayPatientsByAgeRange(int minAge, int maxAge) {
        System.out.println("\n=== PATIENTS IN AGE RANGE: " + minAge + " - " + maxAge + " ===");

        final boolean[] found = {false};
        for (Patient patient : dao.getAllPatients()) {
            if (patient.getAge() >= minAge && patient.getAge() <= maxAge) {
                if (!found[0]) {
                    System.out.println(Messages.PATIENT_TABLE_LINE);
                    System.out.printf(Messages.PATIENT_TABLE_FORMAT, "ID", "IC Number", "Name", "Gender", "Age", "Phone", "Address", "Email", "Medical History");
                    System.out.println(Messages.PATIENT_TABLE_LINE);
                    found[0] = true;
                }
                System.out.printf(Messages.PATIENT_TABLE_FORMAT,
                        patient.getPatientId(), patient.getIcNumber(), patient.getName(),
                        patient.getGender(), patient.getAge(), patient.getPhoneNumber(),
                        patient.getAddress(), patient.getEmail(), patient.getMedicalHistory());
            }
        }

        if (!found[0]) {
            System.out.println("No patients found in age range: " + minAge + " - " + maxAge);
        } else {
            System.out.println(Messages.PATIENT_TABLE_LINE);
        }
    }

    public void displayPatientsByGender(String gender) {
        System.out.println("\n=== PATIENTS BY GENDER: " + gender.toUpperCase() + " ===");

        final boolean[] found = {false};

        for (Patient patient : dao.getAllPatients()) {
            if (patient.getGender().equalsIgnoreCase(gender)) {
                if (!found[0]) {
                    System.out.println(Messages.PATIENT_TABLE_LINE);
                    System.out.printf(Messages.PATIENT_TABLE_FORMAT, "ID", "IC Number", "Name", "Gender", "Age", "Phone", "Address", "Email", "Medical History");
                    System.out.println(Messages.PATIENT_TABLE_LINE);
                    found[0] = true;
                }
                System.out.printf(Messages.PATIENT_TABLE_FORMAT,
                        patient.getPatientId(), patient.getIcNumber(), patient.getName(),
                        patient.getGender(), patient.getAge(), patient.getPhoneNumber(),
                        patient.getAddress(), patient.getEmail(), patient.getMedicalHistory());
            }
        }

        if (!found[0]) {
            System.out.println("No patients found with gender: " + gender);
        } else {
            System.out.println(Messages.PATIENT_TABLE_LINE);
        }
    }

}
