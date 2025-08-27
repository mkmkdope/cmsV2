/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import boundary.ConsultationMenu;
import dao.ConsultationDAO;
import dao.PatientDAO;
import dao.waitingQueueDAO;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Ng Mei Yen
 */
public class ConsultationManager {

    private ListInterface<Patient> patientList;
    private ListInterface<Consultation> consultationList;
    private ListInterface<Doctor> doctorList;
    private ConsultationMenu consultationMenu;

    private ConsultationDAO consultationDAO;
    private PatientDAO patientDAO;

    private waitingQueueDAO waitingQueue;
    private Scanner sc = new Scanner(System.in);

    public ConsultationManager(ConsultationDAO consultationDAO, PatientDAO patientDAO, waitingQueueDAO waitingQueue) {
        this.consultationDAO = consultationDAO;
        this.patientDAO = patientDAO;
        this.consultationList = consultationDAO.getAllConsultation();
        this.patientList = patientDAO.getAllPatients();
        this.doctorList = consultationDAO.getAllDoctors();
        this.consultationMenu = new ConsultationMenu();

        this.waitingQueue = waitingQueue;
    }

    public void consultationManagement(int choice) {
        switch (choice) {
            case 1:
                walkInConsultation();
                break;
            case 2:
                viewTodayQueue();
                break;
            case 3:
                updateConsultation();
                break;
            case 4:
                searchConsultation();
                break;
            case 5:
                sortConsultation();
                break;
            case 6:
                patientVisitationReport();
                break;
            case 7:
                consultationStatusReport();
                break;
            case 8:
                completeConsultation(); //- use by doctor, after combine, remove it!
                break;
            case 9: {
                System.out.print("Enter Doctor ID to call next patient: ");
                String did = sc.nextLine().trim();
                System.out.print("Enter reason (optional): ");
                String reason = sc.nextLine();
                callNextFromWaitingQueue(did, reason);
                break;
            }
            case 0:
                System.out.println("\nReturning to Main Menu...");
                break;
        }
    }

    public void setWaitingQueue(waitingQueueDAO wq) {
        this.waitingQueue = wq;
    }

    //yh 
    public void callNextFromWaitingQueue(String doctorId, String reason) {
        if (waitingQueue == null) {
            System.out.println("Waiting queue is not initialized.");
            return;
        }

        if (waitingQueue.isEmpty()) {
            System.out.println("Waiting list is empty. No patient to call.");
            return;
        }

        Patient next = waitingQueue.serveNext();
        if (next == null) {
            System.out.println("Failed to serve next from waiting queue");
            return;
        }

        //find a doctor
        Doctor doc = consultationDAO.findDoctorById(doctorId);
        if (doc == null) {
            System.out.println("Doctor not found: " + doctorId);
            waitingQueue.addByIdWithPriority(next.getPatientId(), 1, patientDAO);
            return;
        }

        String cid = consultationDAO.generateID();
        LocalDateTime now = LocalDateTime.now();

        Consultation c = new Consultation(cid, next, doc, now, (reason == null || reason.equals("-")) ? "Walk-in" : reason);
        c.setStatus("Scheduled");
        c.setFollowUpFlag(false);
        c.setPreviousConsultationId(null);

        consultationDAO.addConsultation(c);

        if (consultationMenu != null) {
            consultationMenu.printSuccessfulSchedule(
                    cid, next.getName(), doc.getName(), now.toString()
            );
        } else {
            System.out.println("Scheduled: " + cid + " / " + next.getName() + " -> " + doc.getName() + " @ " + now);
        }
    }

    //////////////////////////////////////////
    public void runConsultationMenu() {
        int choice;
        do {
            choice = consultationMenu.consultationMenu();
            consultationManagement(choice);
        } while (choice != 0);
    }

    //check if patient has consultation more than 1 time
    private boolean duplicatePatient(Patient patient) {
        LocalDateTime today = LocalDateTime.now();

        for (Consultation consultation : consultationList) {
            if (consultation.getPatient().getPatientId().equals(patient.getPatientId())
                    && consultation.getDateTime().toLocalDate().equals(today)
                    && !consultation.getStatus().equalsIgnoreCase("Cancelled")) {
                return true; //already has consultation today
            }
        }
        return false;
    }

    //print and return
    private ListInterface<Doctor> getAvailableDoctors() {
        ListInterface<Doctor> availableDoctors = new CircularDoublyLinkedList<>();

        for (Doctor doc : doctorList) {
            if (doc.isAvailable()) {//today work or not
                availableDoctors.add(doc);
                System.out.printf("%s - %s (%s)\n", doc.getDoctorId(), doc.getName(), doc.getSpecialization());
            }
        }
        System.out.println("\n" + "=".repeat(50));
        return availableDoctors;
    }

    // Checks if doctor works today
    private boolean isDoctorWorkingToday(String dutySchedule, DayOfWeek day) {
        if (dutySchedule == null || dutySchedule.isEmpty()) {
            return false;
        }

        String[] parts = dutySchedule.split(" ");
        if (parts.length != 2) {
            return false;
        }

        String[] days = parts[0].split("-");
        if (days.length != 2) {
            return false;
        }

        DayOfWeek startDay = DayOfWeek.valueOf(days[0].toUpperCase());
        DayOfWeek endDay = DayOfWeek.valueOf(days[1].toUpperCase());

        return !(day.getValue() < startDay.getValue() || day.getValue() > endDay.getValue());
    }

    private LocalTime parseTimeToLocal(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hha", Locale.ENGLISH);
        return LocalTime.parse(timeStr.toUpperCase(), formatter);
    }

    //adt: contains - to avaid crash time with other consultation
    private boolean containsTime(ListInterface<LocalTime> timeList, LocalTime time) {
        return timeList.contains(time);
    }

    private ListInterface<LocalTime> getAvailableSlotsForDoctor(Doctor selectedDoctor, LocalDate date, Consultation originalConsultation) {
        if (!isDoctorWorkingToday(selectedDoctor.getDutySchedule(), date.getDayOfWeek())) {
            System.out.println("Doctor is not on duty on " + date + ".");
            return null;
        }

        // Check for booked times on the specific date
        ListInterface<LocalTime> bookedTimes = new CircularDoublyLinkedList<>();
        for (Consultation c : consultationList) {
            if (c.getDoctor() != null
                    && c.getDoctor().getDoctorId().equals(selectedDoctor.getDoctorId())
                    && c.getDateTime().toLocalDate().equals(date)
                    && (!c.getStatus().equalsIgnoreCase("Cancelled") || !c.getStatus().equalsIgnoreCase("Completed"))) {
                bookedTimes.add(c.getDateTime().toLocalTime());
            }
        }

        String[] parts = selectedDoctor.getDutySchedule().split(" ");
        String timeRange = parts[1];
        String[] timeParts = timeRange.split("-");
        LocalTime start = parseTimeToLocal(timeParts[0]);
        LocalTime end = parseTimeToLocal(timeParts[1]);

        ListInterface<LocalTime> availableSlots = new CircularDoublyLinkedList<>();
        LocalTime slot = start;

        boolean isToday = date.isEqual(LocalDate.now());
        LocalTime currentTime = LocalTime.now();

        while (!slot.isAfter(end.minusMinutes(15))) {
            if (isToday) {
                if (slot.isAfter(currentTime) && !containsTime(bookedTimes, slot)) {
                    availableSlots.add(slot);
                }
            } else {
                if (!containsTime(bookedTimes, slot)) {
                    availableSlots.add(slot);
                }
            }
            slot = slot.plusMinutes(15);
        }
        return availableSlots;
    }

    public void walkInConsultation() {
        System.out.println("");
        System.out.println("=== Walk-in Consultation ===");

        //check patient exist in db
        String ic = consultationMenu.inputPatientIC();
        Patient patient = patientDAO.findPatientByIC(ic);
        if (patient == null) {
            String input = consultationMenu.inputReturn();
            if (!input.isEmpty() || input.isEmpty()) {//type anything or enter to return
                return;
            }
            return;
        }

        if (duplicatePatient(patient)) {
            System.out.println("Patient " + patient.getName() + " already has a consultation scheduled for today.");
            System.out.println("Please check existing appointments or reschedule for another day.");
            return;
        }

        ListInterface<Doctor> availableDoctors = getAvailableDoctors();

        //adt: isempty check doctor available list-return true indicate doctor not available right now
        if (availableDoctors.isEmpty()) {
            System.out.println("No doctors are available right now.");
            System.out.print("Enter 0 to return to main menu: ");
            if (consultationMenu.isExit()) {//display message purpose
                return;
            }
            return; // no doctors means can't proceed
        }

        boolean anotherDoctor;//choose another doctor?
        do {
            anotherDoctor = false;
            //Choose doctor
            Doctor selectedDoctor = null;
            String doctorID;
            do {
                doctorID = consultationMenu.inputDoctorID();
                if (doctorID.equals("0")) {
                    return;
                }

                //check exist of this doctor in db
                for (int i = 1; i <= availableDoctors.getNumberOfEntries(); i++) {
                    Doctor doc = availableDoctors.getEntry(i);
                    if (doc.getDoctorId().equalsIgnoreCase(doctorID)) {
                        selectedDoctor = doc;
                        break;
                    }
                }

                if (selectedDoctor == null) {
                    System.out.println("Invalid Doctor ID. Please enter a correct ID.");
                }
            } while (selectedDoctor == null);

            LocalDate selectDate = consultationMenu.selectDate();

            ListInterface<LocalTime> availableSlots = getAvailableSlotsForDoctor(selectedDoctor, selectDate, null);

            if (availableSlots == null || availableSlots.getNumberOfEntries() == 0) {
                System.out.println("No available time slots for this doctor on " + selectDate + ".");//ask want to choose other doctor?
                String response = consultationMenu.inputOtherTime();
                if (response.equalsIgnoreCase("yes")) {
                    anotherDoctor = true;
                } else {
                    return;//go back
                }

            } else {
                System.out.println("\n=== Available Time Slots ===");
                for (LocalTime slot : availableSlots) {
                    System.out.print(slot + " | ");
                }
                System.out.println();

                //select time
                LocalTime chosenTime = null;
                do {

                    String timeInput = consultationMenu.inputConsultationTime();//after see and no time that want then ask whether want change doctor
                    if (timeInput.equals("0")) {
                        anotherDoctor = true;
                        chosenTime = null;//when choose to return
                        break;
                    }

                    try {
                        LocalTime inputTime = LocalTime.parse(timeInput);
                        if (containsTime(availableSlots, inputTime)) {
                            chosenTime = inputTime;
                        } else {
                            System.out.println("Time is not available. Try again.");
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid time format. Try again.");
                    }
                } while (chosenTime == null);

                if (chosenTime != null) {
                    String reason;
                    while (true) {
                        reason = consultationMenu.inputConsultationReason();
                        if (reason.isEmpty()) {
                            System.out.println("Reason cannot be empty. Please try again.");
                        } else {
                            break;
                        }
                    }

                    Consultation consultation = new Consultation(consultationDAO.generateID(), patient, selectedDoctor, LocalDateTime.of(selectDate, chosenTime), reason);
                    consultationDAO.addConsultation(consultation);

                    consultationMenu.printSuccessfulSchedule(
                            consultation.getConsultationID(),
                            patient.getName(),
                            selectedDoctor.getName(),
                            consultation.getDateTime().toString()
                    );

                }
            }

        } while (anotherDoctor);
    }

    public void viewConsultationRecord(ListInterface<Consultation> consultationList) {
        consultationMenu.printRecordHeader();

        if (consultationList.getNumberOfEntries() == 0) {
            System.out.println("No consultation records found.");
        } else {
            for (int i = 1; i <= consultationList.getNumberOfEntries(); i++) {
                Consultation c = consultationList.getEntry(i);
                System.out.println(c.toString());
            }
        }

        System.out.println("=".repeat(172));
    }

    public void viewTodayQueue() {
        consultationMenu.printRecordHeader();

        ListInterface<Consultation> todayConsultations = getTodayConsultations();
        if (consultationList.isEmpty()) {
            System.out.println("No consultation records found.");
        } else {
            for (Consultation c : todayConsultations) {
                System.out.println(c.toString());

            }
        }

        System.out.println("=".repeat(172));
    }

    private ListInterface<Consultation> getTodayConsultations() {
        ListInterface<Consultation> todayConsultations = new CircularDoublyLinkedList<>();
        LocalDate today = LocalDate.now();

        for (Consultation consultation : consultationList) {
            if (consultation.getDateTime().toLocalDate().equals(today)) {
                todayConsultations.add(consultation);
            }
        }

        //sort today's consultations by time
        todayConsultations.mergeSort((c1, c2) -> c1.getDateTime().compareTo(c2.getDateTime()));

        return todayConsultations;
    }

    public void updateConsultation() {
        System.out.println("");
        System.out.println("=== Update Consultation ===");
        boolean reenter;
        int position = -1;
        Consultation consultationToUpdate = null;
        do {
            reenter = false;
            String consultationId = consultationMenu.inputUpdateConsultationId();

            //adt - searchByKey
            // define a "key" object for comparison
            Consultation key = new Consultation(consultationId);

            // define the comparator to match by ID
            Comparator<Consultation> byId = Comparator.comparing(c -> c.getConsultationID());

            position = consultationList.searchByKey(byId, key);

            if (position != -1) {
                consultationToUpdate = consultationList.getEntry(position);
                if (consultationToUpdate.getStatus().equalsIgnoreCase("Completed") || consultationToUpdate.getStatus().equalsIgnoreCase("Cancelled")) {
                    System.out.println("You are not allowed to update consultation with status '" + consultationToUpdate.getStatus() + "'");
                    consultationToUpdate = null;
                }
            }

            if (consultationToUpdate == null) {
                System.out.print("Consultation with ID " + consultationId + " not found or status is not updatable. Want to reenter the Consultation ID?(yes/no): ");
                String choice = consultationMenu.inputYesNo();

                if (choice.equalsIgnoreCase("yes")) {
                    reenter = true;
                } else {
                    return;//cannot proceed to update if consultation id doesnt exist
                }
            }
        } while (reenter);

        //display information consultation for this id
        consultationMenu.printConsultationInfo(consultationToUpdate);

        //ask want to change what
        boolean continueUpdating;
        do {
            continueUpdating = false;

            String choice = consultationMenu.inputUpdateSelection();

            switch (choice) {
                case "1":
                    updateTime(consultationToUpdate, position);
                    break;
                case "2":
                    updateStatus(consultationToUpdate, position);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 or 2 only.");
                    continueUpdating = true;
            }

            if (!continueUpdating) {

                String continueChoice = consultationMenu.continueUpdateSelection();
                if (continueChoice.equalsIgnoreCase("yes")) {
                    continueUpdating = true;
                } else {
                    System.out.println("Consultation updated successfully!");
                }
            }

        } while (continueUpdating);

    }

    //update status
    public void updateStatus(Consultation consultation, int position) {
        System.out.println("");
        System.out.println("== Update Consultation Status ==");
        boolean reenterStatus;
        do {
            reenterStatus = false;

            String statusChoice = consultationMenu.inputStatusSelection(consultation);
            String newStatus = null;

            switch (statusChoice) {
                case "1":
                    newStatus = "Scheduled";
                    break;
                case "2":
                    newStatus = "Cancelled";
                    break;
                case "3":
                    newStatus = "No-show";
                    break;
                default:
                    String choice = consultationMenu.inputReenterStatus();
                    if (choice.equalsIgnoreCase("yes")) {
                        reenterStatus = true;
                    } else {
                        System.out.println("Status update cancelled.");
                    }

            }

            if (!reenterStatus && newStatus != null) {
                Consultation updatedConsultation = new Consultation(
                        consultation.getConsultationID(),
                        consultation.getPatient(),
                        consultation.getDoctor(),
                        consultation.getDateTime(),
                        consultation.getReason()
                );

                updatedConsultation.setStatus(newStatus);
                consultationList.replace(position, updatedConsultation);
                System.out.println("Status updated successfully to: " + newStatus);
            }

        } while (reenterStatus);
    }

    //update time
    public void updateTime(Consultation consultation, int position) {
        System.out.println("");
        System.out.println("== Update Consultation Time ==");

        //get doctor from existing record
        Doctor currentDoctor = consultation.getDoctor();
        //LocalDate consultationDate = consultation.getDateTime().toLocalDate();
        LocalDate newDate = null;
        while (newDate == null) {
            String dateInput = consultationMenu.inputNewDate();

            if (dateInput.equals("0")) {
                System.out.println("Update cancelled.");
                return;
            }

            try {
                newDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                if (newDate.isBefore(LocalDate.now())) {
                    System.out.println("Date cannot be in the past. Please try again.");
                    newDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use dd-MM-YYYY.");
            }
        }

        ListInterface<LocalTime> availableSlots = getAvailableSlotsForDoctor(currentDoctor, newDate, consultation);

        if (availableSlots == null || availableSlots.getNumberOfEntries() == 0) {
            System.out.println("Sorry, can't perform update time request. No other available time slots for this doctor.");
            return;
        }

        System.out.println("\n=== Available Time Slots for " + currentDoctor.getName() + " ===");
        for (int i = 1; i <= availableSlots.getNumberOfEntries(); i++) {
            System.out.print(availableSlots.getEntry(i) + " | ");
        }
        System.out.println();

        //choose new time
        LocalTime chosenTime = null;
        do {
            String timeInput = consultationMenu.inputNewTime();
            if (timeInput.equals("0")) {
                System.out.println("Update cancelled.");
                return;
            }

            try {
                LocalTime inputTime = LocalTime.parse(timeInput);
                if (containsTime(availableSlots, inputTime)) {
                    chosenTime = inputTime;
                } else {
                    System.out.println("Invalid time or time slot is not available. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid time format. Please use HH:mm.");
            }
        } while (chosenTime == null);

        //update to new time
        Consultation updatedConsultation = new Consultation(
                consultation.getConsultationID(),
                consultation.getPatient(),
                consultation.getDoctor(),
                LocalDateTime.of(newDate, chosenTime),
                consultation.getReason()
        );

        updatedConsultation.setStatus(consultation.getStatus());
        consultationList.replace(position, updatedConsultation);

        System.out.println("Consultation time updated successfully!");
        System.out.println("New Consultation Time: " + updatedConsultation.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
    }

    //search
    public void searchConsultation() {
        System.out.println("");
        System.out.println("=== Search Consultation ===");
        int opt = -1;
        do {

            opt = consultationMenu.inputSearchOption();

            switch (opt) {
                case 1:
                    searchPatientId();
                    break;
                case 2:
                    searchDate();
                    break;
                case 3:
                    searchStatus();
                    break;
                case 4:
                    viewConsultationRecord(consultationList);
                    break;
                default:
                    if (opt != 0) {
                        System.out.println("Invalid choice. Please try again");
                    }

            }

        } while (opt != 0);
    }

    //search by ic
    public void searchPatientId() {
        System.out.println("");
        System.out.println("== Search By Patient IC ==");
        String ic = consultationMenu.inputPatientIC();

        ListInterface<Consultation> searchResults = new CircularDoublyLinkedList<>();
        boolean patientFound = false;

        for (Consultation c : consultationList) {
            if (c.getPatient().getIcNumber().equalsIgnoreCase(ic)) {
                searchResults.add(c);
                patientFound = true;
            }
        }

        if (!patientFound) {
            System.out.println("No consultation records found for patient with IC: " + ic);
        } else {
            System.out.println("\nSearch Results for Patient: " + searchResults.getEntry(1).getPatient().getName());
            viewConsultationRecord(searchResults);
        }
    }

    //search by date&time
    public void searchDate() {
        System.out.println("");
        System.out.println("== Search By Consultation Date ==");
        LocalDate searchDate = consultationMenu.selectDate();

        ListInterface<Consultation> searchResults = new CircularDoublyLinkedList<>();
        boolean found = false;

        for (Consultation c : consultationList) {
            if (c.getDateTime().toLocalDate().isEqual(searchDate)) {
                searchResults.add(c);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No consultation records found for the date: " + searchDate);
        } else {
            System.out.println("\nSearch Results for Date: " + searchDate);
            viewConsultationRecord(searchResults);
        }
    }

    //search by status
    public void searchStatus() {
        System.out.println("");
        System.out.println("== Search By Consultation Status ==");
        String statusChoice;
        String searchStatus = null;
        boolean reenterStatus = false;

        do {

            statusChoice = consultationMenu.inputStatusChoice();

            switch (statusChoice) {
                case "1":
                    searchStatus = "Scheduled";
                    reenterStatus = false;//reset
                    break;
                case "2":
                    searchStatus = "Completed";
                    reenterStatus = false;
                    break;
                case "3":
                    searchStatus = "Cancelled";
                    reenterStatus = false;
                    break;
                case "4":
                    searchStatus = "No-show";
                    reenterStatus = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number from 1 to 4.");
                    reenterStatus = true;
                    break;
            }
        } while (reenterStatus);

        ListInterface<Consultation> searchResults = new CircularDoublyLinkedList<>();
        boolean found = false;

        for (Consultation c : consultationList) {
            if (c.getStatus().equalsIgnoreCase(searchStatus)) {
                searchResults.add(c);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No consultation records found with status: " + searchStatus);
        } else {
            System.out.println("\nSearch Results for Status: " + searchStatus);
            viewConsultationRecord(searchResults);
        }
    }

    //sort and display
    public void sortConsultation() {
        System.out.println("");
        System.out.println("=== Sort and Display Consultation ===");
        int opt = -1;

        do {

            opt = consultationMenu.inputSortOption();

            switch (opt) {
                case 1:
                    sortByDate();
                    break;
                case 2:
                    sortByPatientName();
                    break;
                case 3:
                    sortByDoctorName();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again");
            }
        } while (opt != 0);
    }

    //copy from original
    private ListInterface<Consultation> copyConsultationList(ListInterface<Consultation> original) {
        ListInterface<Consultation> sortResults = new CircularDoublyLinkedList<>();
        for (int i = 1; i <= original.getNumberOfEntries(); i++) {
            sortResults.add(original.getEntry(i));
        }
        return sortResults;
    }

    //sort by date&time
    public void sortByDate() {
        System.out.println("");

        ListInterface<Consultation> sortResults = copyConsultationList(consultationDAO.getAllConsultation());

        int order = consultationMenu.inputSortOrder();

        if (order == 1) {
            System.out.println("== Sort By Date (Ascending) ==");
            sortResults.mergeSort((c1, c2) -> c1.getDateTime().compareTo(c2.getDateTime()));
        } else if (order == 2) {
            System.out.println("== Sort By Date (Descending) ==");
            sortResults.mergeSort((c1, c2) -> c2.getDateTime().compareTo(c1.getDateTime()));
        }

        viewConsultationRecord(sortResults);
    }

    //sort by patient name
    public void sortByPatientName() {
        System.out.println("");
        System.out.println("== Sort By Patient Name ==");
        ListInterface<Consultation> sortResults = copyConsultationList(consultationDAO.getAllConsultation());
        sortResults.mergeSort((c1, c2) -> c1.getPatient().getName().compareTo(c2.getPatient().getName()));
        viewConsultationRecord(sortResults);
    }

    //sort by doctor name
    public void sortByDoctorName() {
        System.out.println("");
        System.out.println("== Sort By Doctor Name ==");
        ListInterface<Consultation> sortResults = copyConsultationList(consultationDAO.getAllConsultation());
        sortResults.mergeSort((c1, c2) -> c1.getDoctor().getName().compareTo(c2.getDoctor().getName()));
        viewConsultationRecord(sortResults);
    }

    public void consultationStatusReport() {
        System.out.println("");

        YearMonth yearMonth = null;

        int month = consultationMenu.inputMonth();
        int year = consultationMenu.inputYear();
        yearMonth = YearMonth.of(year, month);

        int scheduledCount = 0;
        int completedCount = 0;
        int noShowCount = 0;
        int cancelledCount = 0;
        int totalConsultations = 0;

        for (int i = 1; i <= consultationList.getNumberOfEntries(); i++) {
            Consultation c = consultationList.getEntry(i);
            YearMonth consultationYearMonth = YearMonth.from(c.getDateTime());
            if (consultationYearMonth.equals(yearMonth)) {
                totalConsultations++;
                String status = c.getStatus();
                if (status.equalsIgnoreCase("Scheduled")) {
                    scheduledCount++;
                } else if (status.equalsIgnoreCase("Completed")) {
                    completedCount++;
                } else if (status.equalsIgnoreCase("No-show")) {
                    noShowCount++;
                } else if (status.equalsIgnoreCase("Cancelled")) {
                    cancelledCount++;
                }
            }
        }

        if (totalConsultations == 0) {
            System.out.printf("No consultations found for %s.\n", yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            return;
        }

        //create list to store status info as strings
        CircularDoublyLinkedList<String> statusReportList = new CircularDoublyLinkedList<>();

        statusReportList.add("Scheduled:" + scheduledCount);
        statusReportList.add("Completed:" + completedCount);
        statusReportList.add("No-show:" + noShowCount);
        statusReportList.add("Cancelled:" + cancelledCount);

        // adt-mergesort-Sort by count (highest first)
        statusReportList.mergeSort((s1, s2) -> {
            // Extract count from string (after the colon)
            int count1 = Integer.parseInt(s1.substring(s1.indexOf(':') + 1));
            int count2 = Integer.parseInt(s2.substring(s2.indexOf(':') + 1));
            return Integer.compare(count2, count1);
        });

        String formattedDate = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        consultationMenu.consultationStatusReportHeader(formattedDate);

        for (String statusInfo : statusReportList) {
            String[] parts = statusInfo.split(":");
            String statusName = parts[0];
            int count = Integer.parseInt(parts[1]);
            double percentage = reportStatusPercentage(count, totalConsultations);
            System.out.printf("%-18s %-25d %-10.1f%%\n", statusName, count, percentage);
        }

        System.out.println("----------------------------------------------------------");
        System.out.printf("Total Consultations: %d\n", totalConsultations);
        System.out.println("=".repeat(58));
    }

    public double reportStatusPercentage(int statusCount, int totalConsultation) {
        double percentage = (double) statusCount / totalConsultation * 100;
        return percentage;
    }

    //list of patient visitation report based on date
    public void patientVisitationReport() {
        System.out.println("");
        LocalDate reportDate = consultationMenu.selectDate();
        ListInterface<Consultation> searchVisitation = new CircularDoublyLinkedList<>();
        String formattedDate = reportDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        for (int i = 1; i <= consultationList.getNumberOfEntries(); i++) {
            Consultation c = consultationList.getEntry(i);
            if (c != null && c.getDateTime().toLocalDate().isEqual(reportDate)) {
                searchVisitation.add(c);
            }
        }

        consultationMenu.visitationReportHeader(formattedDate);

        if (searchVisitation.isEmpty()) {
            System.out.println("                                                                 No visitations found for this date.");
        } else {
            for (int i = 1; i <= searchVisitation.getNumberOfEntries(); i++) {
                System.out.println(searchVisitation.getEntry(i).toString());
            }
        }
        consultationMenu.visitationReportFooter(formattedDate, searchVisitation);
    }

    //used by doctor
    //***need to do validation -> d.getDoctorId(Doctor) with c.d.getDoctorId
    public void completeConsultation() {//or directly pass consultation to mark as complete(no need ask consultation id)
        System.out.println("");
        System.out.println("Complete Consultation Information");
        System.out.println("================================");
        Consultation selectedConsultation = null;
        while (selectedConsultation == null) {
            String id = consultationMenu.inputCIDComplete();

            if (id.equals("0")) {
                System.out.println("You have cancelled the action");
                return;
            }

            boolean found = false;//id not exist

            for (int i = 1; i <= consultationList.getNumberOfEntries(); i++) {
                Consultation c = consultationList.getEntry(i);
                if (c.getConsultationID().equalsIgnoreCase(id)) {
                    found = true;

                    if (c.getStatus().equalsIgnoreCase("Scheduled")) {
                        System.out.println(c.getConsultationID() + ": Patient - " + c.getPatient().getName() + ", Doctor - " + c.getDoctor().getName() + ", Date - " + c.getDateTime());
                        selectedConsultation = c;
                    } else if (c.getStatus().equalsIgnoreCase("Completed")) {
                        System.out.println("Consultation is already completed.");
                    } else {
                        System.out.println("Cannot perform this action. Consultation status is \"Cancenlled\" or \"No-show\".");
                    }
                    break; //stop when find the consultation
                }
            }

            if (!found) {
                System.out.println("Consultation ID not found. Please try again.");
            }
        }

        boolean followUp = false;
        while (true) {
            String response = consultationMenu.inputFUoption();
            if (response.equals("yes")) {
                followUp = true;
                break;
            } else if (response.equals("no")) {
                followUp = false;
                break;
            } else {
                System.out.println("Please enter 'yes' or 'no'.");
            }
        }

        //apply updates to the ori object
        selectedConsultation.setFollowUpFlag(followUp);
        selectedConsultation.setStatus("Completed");

        System.out.println("Consultation " + selectedConsultation.getConsultationID() + " updated successfully.");

        if (followUp) {
            registerNewFollowUp(selectedConsultation);
        }
    }

    //used by doctor
    public void registerNewFollowUp(Consultation originalConsultation) {
        System.out.println("");
        System.out.println("Registering New Follow-up Appointment");
        System.out.println("Patient: " + originalConsultation.getPatient().getName() + " | Doctor: " + originalConsultation.getDoctor().getName());

        String dateTimeStr;
        LocalDateTime newDateTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        boolean validDateTime = false;

        //validation for date and time
        while (!validDateTime) {
            dateTimeStr = consultationMenu.inputFUdate();
            try {
                newDateTime = LocalDateTime.parse(dateTimeStr, formatter);

                if (newDateTime.isBefore(LocalDateTime.now())) {
                    System.out.println("Please enter a valid date. Valid date should be the day after today");
                } else {
                    validDateTime = true;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date or time format. Please use DD-MM-YYYY HH:MM.");
            }
        }

        Consultation newFollowUp = new Consultation(consultationDAO.generateID(), originalConsultation.getPatient(), originalConsultation.getDoctor(), newDateTime, "Follow-up for " + originalConsultation.getReason());

        // record down previous consultationId
        newFollowUp.setPreviousConsultationId(originalConsultation.getConsultationID());

        consultationList.add(newFollowUp);

        System.out.println("New follow-up appointment successfully registered!");
        System.out.println("New Consultation ID: " + newFollowUp.getConsultationID());
        System.out.println("Scheduled for: " + newFollowUp.getDateTime());
    }

    // get all consultations for Treatment module integration
    public ListInterface<Consultation> getAllConsultations() {
        return consultationDAO.getAllConsultation();
    }
}
