package dao;

import adt.CircularDoublyLinkedList;
import entity.Doctor;

public class DutySchedule {
    private Doctor[][] weeklySchedule;
    private CircularDoublyLinkedList<Doctor> doctorList;
    private static final int DAYS = 5;
    private static final int MAX_SLOTS = 10;
    private int lastAssignedIndex = 0;
    
    public DutySchedule(CircularDoublyLinkedList<Doctor> doctorList) {
        this.doctorList = doctorList;
        weeklySchedule = new Doctor[DAYS][MAX_SLOTS];
    }
    
    public void generateBaseSchedule() {
        for (int day = 0; day < DAYS; day++) {
            for (int slot = 0; slot < MAX_SLOTS; slot++) {
                weeklySchedule[day][slot] = null;
            }
        }
        
        lastAssignedIndex = 0;
        
        for (int day = 0; day < DAYS; day++) {
            for (int slot = 0; slot < MAX_SLOTS; slot++) {
                if (doctorList.size() > 0) {
                    weeklySchedule[day][slot] = getNextAvailableDoctor();
                }
            }
        }
    }
    
    private Doctor getNextAvailableDoctor() {
        if (doctorList.size() == 0) return null;
        
        int startIndex = lastAssignedIndex;
        int attempts = 0;
        
        do {
            lastAssignedIndex = (lastAssignedIndex % doctorList.size()) + 1;
            Doctor doctor = doctorList.getEntry(lastAssignedIndex);
            
            if (doctor.isAvailable()) {
                return doctor;
            }
            
            attempts++;
        } while (attempts < doctorList.size());
        
        return null;
    }
    
    public boolean addDoctorToDay(int dayIndex, Doctor doctor) {
        if (dayIndex < 0 || dayIndex >= DAYS) {
            System.out.println("Error: Invalid day index. Must be between 0-4.");
            return false;
        }
        
        if (doctor == null) {
            System.out.println("Error: Doctor cannot be null.");
            return false;
        }
        
        if (isDoctorScheduledOnDay(doctor, dayIndex)) {
            System.out.println("Doctor " + doctor.getName() + " is already scheduled on " + getDayName(dayIndex));
            return false;
        }
        
        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            if (weeklySchedule[dayIndex][slot] == null) {
                weeklySchedule[dayIndex][slot] = doctor;
                System.out.println("Added Dr. " + doctor.getName() + " to " + getDayName(dayIndex) + ", Slot " + (slot + 1));
                return true;
            }
        }
        
        System.out.println("Error: No available slots on " + getDayName(dayIndex));
        return false;
    }
    
    public boolean removeDoctorFromDay(int dayIndex, String doctorId) {
        if (dayIndex < 0 || dayIndex >= DAYS) {
            System.out.println("Error: Invalid day index. Must be between 0-4.");
            return false;
        }
        
        if (doctorId == null || doctorId.trim().isEmpty()) {
            System.out.println("Error: Doctor ID cannot be empty.");
            return false;
        }
        
        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            Doctor doctor = weeklySchedule[dayIndex][slot];
            if (doctor != null && doctor.getDoctorId().equals(doctorId)) {
                weeklySchedule[dayIndex][slot] = null;
                System.out.println("Removed Dr. " + doctor.getName() + " from " + getDayName(dayIndex) + ", Slot " + (slot + 1));
                return true;
            }
        }
        
        System.out.println("Error: Doctor " + doctorId + " not found on " + getDayName(dayIndex));
        return false;
    }
    
    public Doctor[] getDoctorsForDay(int dayIndex) {
        if (dayIndex < 0 || dayIndex >= DAYS) return new Doctor[0];
        
        int count = 0;
        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            if (weeklySchedule[dayIndex][slot] != null) count++;
        }
        
        Doctor[] doctors = new Doctor[count];
        int index = 0;
        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            if (weeklySchedule[dayIndex][slot] != null) {
                doctors[index++] = weeklySchedule[dayIndex][slot];
            }
        }
        return doctors;
    }
    
    public void displayWeeklySchedule() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("WEEKLY DUTY SCHEDULE (SEQUENTIAL ASSIGNMENT)");
        System.out.println("=".repeat(60));
        
        for (int day = 0; day < DAYS; day++) {
            System.out.println("\n--- " + days[day] + " ---");
            boolean hasDoctors = false;
            
            for (int slot = 0; slot < MAX_SLOTS; slot++) {
                Doctor doctor = weeklySchedule[day][slot];
                if (doctor != null) {
                    String status = doctor.isAvailable() ? "✓ AVAILABLE" : "✗ ON LEAVE";
                    String indicator = doctor.isAvailable() ? "•" : "✗";
                    
                    System.out.printf("%s %s (%s) - %s%n", 
                        indicator,
                        doctor.getName(), 
                        doctor.getDoctorId(), 
                        status);
                    hasDoctors = true;
                }
            }
            
            if (!hasDoctors) {
                System.out.println("No doctors scheduled");
            }
        }
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✓ = Available, ✗ = On Leave");
        System.out.println("=".repeat(60));
    }
    
    public void rearrangeSchedule() {
        int changesMade = 0;
        
        for (int day = 0; day < DAYS; day++) {
            for (int slot = 0; slot < MAX_SLOTS; slot++) {
                Doctor doctor = weeklySchedule[day][slot];
                if (doctor == null) continue;
                
                boolean shouldRemove = !isDoctorInPool(doctor) || !doctor.isAvailable();
                if (shouldRemove) {
                    weeklySchedule[day][slot] = null;
                    changesMade++;
                    System.out.println("Removed Dr. " + doctor.getName() + " from " + 
                                     getDayName(day) + " (" + 
                                     (!isDoctorInPool(doctor) ? "Removed from system" : "On Leave") + ")");
                }
            }
        }
        
        changesMade += fillEmptySlotsSequentially();
        
        if (changesMade > 0) {
            //System.out.println("✓ Schedule auto-rearranged: " + changesMade + " changes made"); //for checking
        }
    }
    
    private int fillEmptySlotsSequentially() {
        int filledSlots = 0;
        lastAssignedIndex = 0;
        
        for (int day = 0; day < DAYS; day++) {
            for (int slot = 0; slot < MAX_SLOTS; slot++) {
                if (weeklySchedule[day][slot] == null) {
                    Doctor availableDoctor = getNextAvailableDoctor();
                    if (availableDoctor != null && !isDoctorScheduledOnDay(availableDoctor, day)) {
                        weeklySchedule[day][slot] = availableDoctor;
                        filledSlots++;
                        //System.out.println("Auto-assigned Dr. " + availableDoctor.getName() + " to " + getDayName(day)); //for checking
                    }
                }
            }
        }
        return filledSlots;
    }
    
    private boolean isDoctorInPool(Doctor doctor) {
        for (int i = 1; i <= doctorList.size(); i++) {
            Doctor poolDoctor = doctorList.getEntry(i);
            if (poolDoctor.equals(doctor)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isDoctorScheduledOnDay(Doctor doctor, int dayIndex) {
        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            if (weeklySchedule[dayIndex][slot] != null && 
                weeklySchedule[dayIndex][slot].equals(doctor)) {
                return true;
            }
        }
        return false;
    }
    
    private String getDayName(int dayIndex) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        return days[dayIndex];
    }
    
    public int getTotalScheduledDoctors() {
        int count = 0;
        for (int day = 0; day < DAYS; day++) {
            for (int slot = 0; slot < MAX_SLOTS; slot++) {
                if (weeklySchedule[day][slot] != null) {
                    count++;
                }
            }
        }
        return count;
    }
    
    public Doctor[][] getWeeklySchedule() {
        return weeklySchedule;
    }
}