package entity;

/**
 *
 * @author Yap Ming Kang
 */

public class Doctor {

    private String doctorId;
    private String name;
    private String specialization;
    private boolean isAvailable;
    private String dutySchedule;

    public Doctor(String doctorId, String name, String specialization, String dutySchedule) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.isAvailable = true;
        this.dutySchedule = dutySchedule;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getDutySchedule() {
        return dutySchedule;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setDutySchedule(String dutySchedule) {
        this.dutySchedule = dutySchedule;
    }

    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s, Specialization: %s, Available: %s, Duty Schedule: %s",
                doctorId, name, specialization, isAvailable ? "Yes" : "No", dutySchedule);
    }

}
