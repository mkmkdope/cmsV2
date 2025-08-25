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

    public PatientManager(PatientDAO dAO){
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

    // Fixed method name typo and updated to use proper priority system
    public boolean registerPatientWithPriority(String ic, String name, String gender, int age,
                                               String phone, String address, String email, String history, int priority){
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

        if(priority < 1 || priority > 5){
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

    public void displayAllPatients(){
        ListInterface<Patient> list = dao.getAllPatients();

        if(list.isEmpty()){
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return;
        }
        System.out.println(Messages.PATIENT_TABLE_LINE);
        System.out.printf(Messages.PATIENT_TABLE_FORMAT, "ID", "IC Number", "Name", "Gender", "Age", "Phone", "Address", "Email", "Medical History");
        System.out.println(Messages.PATIENT_TABLE_LINE);

        // Using traverseForward from adt for better performance
        for(Patient patient : list){
            System.out.printf(Messages.PATIENT_TABLE_FORMAT,
                    patient.getPatientId(), patient.getIcNumber(), patient.getName(),
                    patient.getGender(), patient.getAge(), patient.getPhoneNumber(),
                    patient.getAddress(), patient.getEmail(), patient.getMedicalHistory());
        }

        System.out.println(Messages.PATIENT_TABLE_LINE);
    }

    //search patient by ID or IC
    public void searchPatient(String keyword){
        Patient patient = dao.findPatientById(keyword);
        if(patient == null){
            patient = dao.findPatientByIC(keyword);
        }
        if(patient == null){
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

    public Patient getPatientById(String id){
        return dao.findPatientById(id);
    }
    
    public Patient getPatientByIC(String ic){
        return dao.findPatientByIC(ic);
    }

    public boolean updatePatient(String id, String ic, String name, String gender, int age, String phone, String address, String email, String history){
        Patient existing = dao.findPatientById(id);
        if(existing == null){
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return false;
        }

        Patient updated = new Patient(id, ic, name, gender, age, phone, address, email, history);
        boolean success = dao.updatePatient(updated);
        if(success){
            System.out.println(Messages.PATIENT_UPDATED);
            return true;
        }else{
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return false;
        }
    }

    public boolean deletePatient(String id){
        boolean success = dao.deletePatient(id);

        if(success){
            System.out.println(Messages.PATIENT_DELETED);
        }else{
            System.out.println(Messages.PATIENT_NOT_FOUND);
        }
        return success;
    }

    public boolean addPatientToQueue(String patientId){
        Patient p = dao.findPatientById(patientId);
        
        if (p == null) {
            return false;
        }
        return waitingQueue.add(p);
    }

    // Fixed: Updated to use proper priority queue system
    public boolean addPatientToQueueWithPriority(String patientId, int priority){
        Patient p = dao.findPatientById(patientId);

        if (p == null) {
            return false;
        }
        if(priority < 1 || priority > 5){
            System.out.println("Invalid priority. Priority must be between 1 (highest) and 5 (lowest).");
            return false;
        }

        // Create priority wrapper for queue
        PatientWithPriority patientWithPriority = new PatientWithPriority(p, priority);
        
        // Priority comparator (lower number = higher priority)
        Comparator<PatientWithPriority> priorityComparator = (p1, p2) -> 
            Integer.compare(p1.priority, p2.priority);
        
        // Convert current queue to priority queue
        ListInterface<PatientWithPriority> priorityQueue = new CircularDoublyLinkedList<>();
        
        // Add existing patients with default priority of 5
        for(Patient patient : waitingQueue) {
            priorityQueue.add(new PatientWithPriority(patient, 5));
        }
        
        // Add new patient with specified priority
        priorityQueue.addWithPriority(priorityComparator, patientWithPriority);
        
        // Update waiting queue
        waitingQueue.clear();
       for(PatientWithPriority pwp : priorityQueue){
            waitingQueue.add(pwp.patient);
        }
        
        return true;
    }

    // Inner class to handle priority in queue
    private static class PatientWithPriority {
        Patient patient;
        int priority;
        
        PatientWithPriority(Patient patient, int priority) {
            this.patient = patient;
            this.priority = priority;
        }
    }

    public Patient serveNextPatient(){
        if(waitingQueue.isEmpty()){
            return null;
        }

        return waitingQueue.remove(1); // Remove and return the first patient in the queue (FIFO)
    }

    public ListInterface<Patient> getQueueList(){
        return waitingQueue;
    }

    // public boolean setExistingPatientPriority(String idOrIc, int priority){
    //     if(priority < 1 || priority > 5){
    //         System.out.println("Invalid priority. Priority must between 1 (highest) and 5 (lowest). ");
    //         return false;
    //     }
    //     boolean isSuccessful = dao.setPriorityForExistingPatient(idOrIc, priority);
    //     if(!isSuccessful){
    //         System.out.println(Messages.PATIENT_NOT_FOUND);
    //     }
    //     return isSuccessful;
    // }



    public void generateReports(){
        ListInterface<Patient> list = dao.getAllPatients();

        if(list.isEmpty()){
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
        
        for(Patient patient : list){
            System.out.printf("%-8s | %-15s | %-20s | %-8s | %-5d | %-12s | %-20s\n",
                             patient.getPatientId(), patient.getIcNumber(), patient.getName(), 
                             patient.getGender(), patient.getAge(), patient.getPhoneNumber(), patient.getAddress());
        }
        System.out.println("-".repeat(95));
    }

    private void printDemographicsAnalysis(ListInterface<Patient> list) {
        System.out.println("\n=== DEMOGRAPHICS ANALYSIS ===");
        
        // Using traversal to collect statistics
        final int[] genderCount = {0, 0, 0}; // male, female, other
        final int[] ageGroups = {0, 0, 0, 0, 0}; // under18, 18-30, 31-50, 51-65, over65
        final java.util.Map<String, Integer> locationCount = new java.util.HashMap<>();
        
       for(Patient patient : list){
            // Gender counting
            String gender = patient.getGender().toLowerCase();
            if(gender.contains("male") && !gender.contains("female")) {
                genderCount[0]++;
            } else if(gender.contains("female")) {
                genderCount[1]++;
            } else {
                genderCount[2]++;
            }
            
            // Age grouping
            int age = patient.getAge();
            if(age < 18) ageGroups[0]++;
            else if(age <= 30) ageGroups[1]++;
            else if(age <= 50) ageGroups[2]++;
            else if(age <= 65) ageGroups[3]++;
            else ageGroups[4]++;
            
            // Location counting
            String location = patient.getAddress().split(",")[0].trim();
            locationCount.put(location, locationCount.getOrDefault(location, 0) + 1);
        }
        
        // Print Gender Statistics
        System.out.println("\nGender Distribution:");
        System.out.printf("Male: %d (%.1f%%)\n", genderCount[0], (genderCount[0] * 100.0 / list.getNumberOfEntries()));
        System.out.printf("Female: %d (%.1f%%)\n", genderCount[1], (genderCount[1] * 100.0 / list.getNumberOfEntries()));
        if(genderCount[2] > 0) System.out.printf("Other: %d (%.1f%%)\n", genderCount[2], (genderCount[2] * 100.0 / list.getNumberOfEntries()));
        
        // Print Age Statistics
        System.out.println("\nAge Distribution:");
        System.out.printf("Under 18: %d patients\n", ageGroups[0]);
        System.out.printf("18-30: %d patients\n", ageGroups[1]);
        System.out.printf("31-50: %d patients\n", ageGroups[2]);
        System.out.printf("51-65: %d patients\n", ageGroups[3]);
        System.out.printf("Over 65: %d patients\n", ageGroups[4]);
        
        // Print Top Locations
        System.out.println("\nTop Patient Locations:");
        locationCount.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(5)
            .forEach(entry -> System.out.printf("%-20s: %d patients\n", entry.getKey(), entry.getValue()));
    }

    private void printGraphicalAnalysis(ListInterface<Patient> list) {
        System.out.println("\n=== GRAPHICAL REPRESENTATION ===");
        
        // Calculate statistics using traversal
        final int[] genderCount = {0, 0}; // male, female
        final int[] ageGroups = {0, 0, 0, 0}; // under18, 18-30, 31-50, over50
        final java.util.Map<String, Integer> locationCount = new java.util.HashMap<>();
        
        for(Patient patient : list){
            if(patient.getGender().equalsIgnoreCase("Male")) genderCount[0]++;
            else if(patient.getGender().equalsIgnoreCase("Female")) genderCount[1]++;
            
            int age = patient.getAge();
            if(age < 18) ageGroups[0]++;
            else if(age <= 30) ageGroups[1]++;
            else if(age <= 50) ageGroups[2]++;
            else ageGroups[3]++;
            
            String location = patient.getAddress().split(",")[0].trim();
            locationCount.put(location, locationCount.getOrDefault(location, 0) + 1);
        }
        
        // Print side-by-side vertical charts
        System.out.println("Gender Distribution                    |  Age Distribution");
        System.out.println("                                       |");
        
        // Prepare data for both charts
        String[] genderLabels = {"Male", "Female"};
        String[] genderColors = {Messages.BLUE, Messages.MAGENTA};
        
        String[] ageLabels = {"<18", "18-30", "31-50", "51+"};
        String[] ageColors = {Messages.GREEN, Messages.YELLOW, Messages.CYAN, Messages.RED};
        
        // Find max values for scaling
        int maxGender = Math.max(genderCount[0], genderCount[1]);
        int maxAge = java.util.Arrays.stream(ageGroups).max().orElse(1);
        int maxHeight = Math.max(Math.min(maxGender, 10), Math.min(maxAge, 10));
        
        // Print charts side by side
        for(int row = maxHeight; row >= 1; row--) {
            // Print gender chart row
            System.out.printf("%3d |", row);
            for(int i = 0; i < genderCount.length; i++) {
                int scaledValue = (genderCount[i] * maxHeight) / Math.max(maxGender, 1);
                if(scaledValue >= row) {
                    System.out.print(" " + genderColors[i] + "██" + Messages.RESET);
                } else {
                    System.out.print("   ");
                }
            }
            
            // Print separator and age chart row (fixed spacing)
            System.out.print("                 |  ");
            System.out.printf("%3d |", row);
            for(int i = 0; i < ageGroups.length; i++) {
                int scaledValue = (ageGroups[i] * maxHeight) / Math.max(maxAge, 1);
                if(scaledValue >= row) {
                    System.out.print(" " + ageColors[i] + "██" + Messages.RESET);
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
        
        // Print bottom lines (fixed alignment)
        System.out.println("    +-------> Gender                   |      +-------------> Demographics");
        
        // Print labels with proper spacing (fixed alignment)
        System.out.print("      ");
        for(String label : genderLabels) {
            System.out.printf("%-6s", label);
        }
        System.out.print("         |        ");
        for(String label : ageLabels) {
            System.out.printf("%-6s", label);
        }
        System.out.println();
        
        // Print values with proper spacing (fixed alignment)
        System.out.print("      ");
        for(int value : genderCount) {
            System.out.printf("%-6s", "(" + value + ")");
        }
        System.out.print("         |        ");
        for(int value : ageGroups) {
            System.out.printf("%-6s", "(" + value + ")");
        }
        System.out.println();
        
        // Top locations chart (horizontal bars)
        System.out.println("\nTop Patient Locations:");
        locationCount.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(5)
            .forEach(entry -> printBar(entry.getKey(), entry.getValue(), Messages.CYAN));
    }

    private void printAdditionalInsights(ListInterface<Patient> list) {
        System.out.println("\n=== ADDITIONAL INSIGHTS ===");
        
        // Calculate statistics using traversal
        final int[] stats = {0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0}; // totalAge, minAge, maxAge, patientsWithHistory
        
        for(Patient patient : list){
            int age = patient.getAge();
            stats[0] += age; // totalAge
            stats[1] = Math.min(stats[1], age); // minAge
            stats[2] = Math.max(stats[2], age); // maxAge
            
            if(patient.getMedicalHistory() != null && 
               !patient.getMedicalHistory().trim().isEmpty()) {
                stats[3]++; // patientsWithHistory
            }
        }
        
        double averageAge = (double) stats[0] / list.getNumberOfEntries();
        
        System.out.printf("Average Patient Age: %.1f years\n", averageAge);
        System.out.printf("Age Range: %d - %d years\n", stats[1], stats[2]);
        System.out.printf("Total Registered Patients: %d\n", list.getNumberOfEntries());
        System.out.printf("Patients in Waiting Queue: %d\n", waitingQueue.getNumberOfEntries());
        System.out.printf("Patients with Medical History: %d (%.1f%%)\n", 
                         stats[3], (stats[3] * 100.0 / list.getNumberOfEntries()));
    }

    private void printBar(String label, int count, String colour) {
        int barLength = Math.min(count, 15); // Limit bar length to 15 characters
        System.out.printf("%-15s | %s%s%s (%d)%n", 
                         label, colour, "█".repeat(barLength), Messages.RESET, count);
    }

    private void printReportFooter() {
        System.out.println("\n" + "=".repeat(120));
        System.out.println(centerText("END OF REPORT", 120));
        System.out.println("*".repeat(120));
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    // Enhanced queue management methods
    public void displayWaitingQueue() {
        System.out.println("\n=== CURRENT WAITING QUEUE ===");
        if(waitingQueue.isEmpty()) {
            System.out.println("No patients in waiting queue.");
            return;
        }
        
        System.out.printf("%-10s | %-15s | %-20s | %-8s | %-5s\n", 
                         "Queue #", "Patient ID", "Name", "Gender", "Age");
        System.out.println("-".repeat(65));
        
        final int[] counter = {1}; // Using array for mutable counter in lambda
        
        for(Patient patient : waitingQueue){
            System.out.printf("%-10d | %-15s | %-20s | %-8s | %-5d\n",
                             counter[0], patient.getPatientId(), patient.getName(), 
                             patient.getGender(), patient.getAge());
            counter[0]++;
        }
        
        System.out.println("-".repeat(65));
        System.out.println("Total patients in queue: " + waitingQueue.getNumberOfEntries());
    }

    // Enhanced sorting methods using mergeSort from adt
    public void sortPatientsByName(){
        dao.getAllPatients().mergeSort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
        System.out.println(Messages.SORT_BY_NAME);
    }

    public void sortPatientsByAge(){
        dao.getAllPatients().mergeSort((p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()));
        System.out.println(Messages.SORT_BY_AGE); 
    }

    public void sortPatientsById(){
        dao.getAllPatients().mergeSort((p1, p2) -> {
            int num1 = Integer.parseInt(p1.getPatientId().substring(1));
            int num2 = Integer.parseInt(p2.getPatientId().substring(1));
            return Integer.compare(num1, num2);
        });
        System.out.println("Patients sorted by ID.");
    }

    // New sorting methods utilizing adt functionality
    public void sortPatientsByIC(){
        dao.getAllPatients().mergeSort((p1, p2) -> p1.getIcNumber().compareTo(p2.getIcNumber()));
        System.out.println("Patients sorted by IC Number.");
    }

    public void sortPatientsByGender(){
        dao.getAllPatients().mergeSort((p1, p2) -> p1.getGender().compareToIgnoreCase(p2.getGender()));
        System.out.println("Patients sorted by Gender.");
    }

    // Enhanced search methods using searchByKey from adt
    public Patient searchPatientByName(String name) {
        return dao.searchPatientByName(name);
    }

    public Patient searchPatientByAge(int age) {
        return dao.searchPatientByAge(age);
    }

    // New methods utilizing adt functions
    public boolean isPatientInQueue(String patientId) {
        Patient patient = dao.findPatientById(patientId);
        if (patient == null){
            return false;
        }
        return waitingQueue.contains(patient);
    }

    public boolean removePatientFromQueue(String patientId) {
        Patient patient = dao.findPatientById(patientId);
        if (patient == null){
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

        if(list.isEmpty()){
            System.out.println(Messages.PATIENT_NOT_FOUND);
            return;
        }

        System.out.println(Messages.PATIENT_TABLE_LINE);
        System.out.printf(Messages.PATIENT_TABLE_FORMAT, "ID", "IC Number", "Name", "Gender", "Age", "Phone", "Address", "Email", "Medical History");
        System.out.println(Messages.PATIENT_TABLE_LINE);


        // Using traverseBackward from adt
        Iterator<Patient> reverseItr = list.reverseIterator();
     while (reverseItr.hasNext()){
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
        if (newPatient == null) return false;

        return waitingQueue.replace(position, newPatient);
    }

    public void clearQueue() {
        waitingQueue.clear();
        System.out.println("Waiting queue cleared successfully.");
    }

    public int getQueueSize() {
        return waitingQueue.getNumberOfEntries();
    }

    public boolean isQueueFull() {
        return waitingQueue.isFull();
    }

    public Patient getPatientAtQueuePosition(int position) {
        if (position < 1 || position > waitingQueue.getNumberOfEntries()) {
            return null;
        }
        return waitingQueue.getEntry(position);
    }

    // Additional methods for PatientMenu
    public int getPatientCount() {
        return dao.getPatientCount();
    }

    //Linked List never get full
    public boolean isPatientListFull() {
        return dao.isPatientListFull();
    }

    public boolean isPatientListEmpty() {
        return dao.isPatientListEmpty();
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

    // New enhanced methods utilizing adt capabilities
    public void displayPatientsByGender(String gender) {
        System.out.println("\n=== PATIENTS BY GENDER: " + gender.toUpperCase() + " ===");
        
        final boolean[] found = {false};
       
        for(Patient patient : dao.getAllPatients()){
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

    public void displayPatientsByAgeRange(int minAge, int maxAge) {
        System.out.println("\n=== PATIENTS IN AGE RANGE: " + minAge + " - " + maxAge + " ===");
        
        final boolean[] found = {false};
        for(Patient patient : dao.getAllPatients()){
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

    public void displayQueueStatistics() {
        System.out.println("\n=== QUEUE STATISTICS ===");
        System.out.println("Queue Size: " + waitingQueue.getNumberOfEntries());
        System.out.println("Queue Empty: " + waitingQueue.isEmpty());
        System.out.println("Queue Full: " + waitingQueue.isFull());
        
        if (!waitingQueue.isEmpty()) {
            System.out.println("Next Patient to Serve: " + waitingQueue.getEntry(1).getName());
            System.out.println("Last Patient in Queue: " + waitingQueue.getEntry(waitingQueue.getNumberOfEntries()).getName());
        }
    }

    // Bulk operations using traversal
    public int countPatientsByCondition(java.util.function.Predicate<Patient> condition) {
        final int[] count = {0};
        for(Patient patient : dao.getAllPatients()){
            if (condition.test(patient)) {
                count[0]++;
            }
        }
        return count[0];
    }

    public void updateAllPatientsInLocation(String location, String newLocation) {
        for(Patient patient : dao.getAllPatients()){
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
}