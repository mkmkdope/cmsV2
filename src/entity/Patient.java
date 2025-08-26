package entity;

public class Patient {

    private String patientId;
    private String icNumber;
    private String name;
    private String gender;
    private int age;
    private String phoneNumber;
    private String address;
    private String email;
    private String medicalHistory;

    public Patient(String patientId, String icNumber, String name, String gender, int age,
            String phoneNumber, String address, String email, String medicalHistory) {
        this.patientId = patientId;
        this.icNumber = icNumber;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.medicalHistory = medicalHistory;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public String toString() {
        return String.format("%-6s %-12s %-20s %-8s %-4d %-12s %-20s %-25s %-20s",
                patientId, icNumber, name, gender, age, phoneNumber, address, email, medicalHistory);
    }
}
