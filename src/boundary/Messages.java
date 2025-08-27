package boundary;

/**
 *
 * @author yuhang
 */

public class Messages {

    //main menu part
    public static final String WELCOME = "Welcome to TARUMT CLINIC Management System!";
    public static final String FORMAT = "=".repeat(60);
    public static final String MAIN_MENU_HEADER = "\n" + FORMAT + "\n" + " TARUMT CLINIC MANAGEMENT SYSTEM\n" + FORMAT;

    public static final String MAIN_MENU_OPTION = 
                   "1. Patient Management \n" +
                   "2. Doctor Management \n" +
                   "3. Consultation Management \n" +
                   "4. Medical Treatment Management \n" +
                   "5. Pharmacy Management \n" +
                   "6. System Report \n" + 
                   "7. Exit\n\n" +FORMAT;

    public static final String MAIN_MENU_PROMPT = "\n Please select an option (1-7): ";
    public static final String INVALID_CHOICE = "Invalid Choice! Please enter a number between the %d - %d.";

    public static final String INVALID_NUM = "Invalid Input! Please enter a valid number";




    // Patient Menu Texts
// Patient part
// Patient part
public static final String PATIENT_MENU_HEADER = "\n" + FORMAT + "\n" + " PATIENT MANAGEMENT\n" + FORMAT;
public static final String PATIENT_MENU_OPTION =
               "1. Register New Patient\n" +
               "2. Display All Patients\n" +
               "3. Search Patient (by ID or IC)\n" +
               "4. Update Patient Details\n" +
               "5. Delete Patient\n" +
               "6. Manage Patient Queue\n" +
               "7. Generate Reports\n" +
               "8. Sort & Display Patients\n" +
               "9. Advanced Patient Operations\n" +
               "10. Back to Main Menu\n" +
               FORMAT;
public static final String PATIENT_MENU_PROMPT = "\nPlease select an option (1-10): ";

public static final String ENTER_PATIENT_ID = "Enter Patient ID: ";
public static final String ENTER_PATIENT_IC = "Enter IC Number (12 digits): ";
public static final String ENTER_PATIENT_NAME = "Enter Patient Name: ";
public static final String ENTER_PATIENT_GENDER = "Enter Gender (Male/Female): ";
public static final String ENTER_PATIENT_AGE = "Enter Age: ";
public static final String ENTER_PATIENT_PHONE = "Enter Phone Number: ";
public static final String ENTER_PATIENT_ADDRESS = "Enter Address: ";
public static final String ENTER_PATIENT_EMAIL = "Enter Email: ";
public static final String ENTER_PATIENT_HISTORY = "Enter Medical History(optional/enter - is left it blank): ";
public static final String REENTER_PATIENT_HISTORY = "Enter Medical History(optional): ";

 public static final String INVALID_IC = "Invalid IC! Please enter a 12-digit number.";
    public static final String DUPLICATE_IC = "This IC is already registered.";
    public static final String INVALID_AGE = "Invalid age! Please enter a positive number and below 120.";
    public static final String INVALID_PHONE = "Invalid phone number! Please enter digits only.";
    public static final String INVALID_EMAIL = "Invalid email format!";
    public static final String PATIENT_NOT_FOUND = "Patient not found.";
    public static final String PATIENT_UPDATED = "Patient information updated successfully.";
    public static final String PATIENT_DELETED = "Patient deleted successfully.";
    public static final String PATIENT_REGISTERED = "Patient registered successfully.";
    public static final String DUPLICATE_ID = "Duplicate patient ID!";
    public static final String CONFIRM_UPDATE = "Are you sure you want to update this patient? (Y/N): ";
    public static final String CONFIRM_DELETE = "Are you sure you want to delete this patient? (Y/N): ";
    public static final String UPDATE_CANCELLED = "Update cancelled.";
    public static final String DELETE_CANCELLED = "Delete cancelled.";
    public static final String INVALID_INPUT = "Invalid input! Please try again.";

public static final String RESET = "\u001B[0m";
public static final String RED = "\u001B[31m";
public static final String GREEN = "\u001B[32m";
public static final String YELLOW = "\u001B[33m";
public static final String BLUE = "\u001B[34m";
public static final String MAGENTA = "\u001B[35m"; // Pink/Magenta
public static final String CYAN = "\u001B[36m";

public static final String SORT_BY_NAME = "Patients sorted by name.";
public static final String SORT_BY_AGE = "Patients sorted by age.";


public static final String REPORT1 = "\n=== Report 1: Gender Distribution ===";
public static final String REPORT2 = "\n=== Report 2: Age Group Distribution ===";

public static final String PATIENT_TABLE_FORMAT =
    "| %-4s | %-14s | %-15s | %-6s | %-3s | %-11s | %-15s | %-25s | %-20s |%n";

public static final String PATIENT_TABLE_LINE =
    "---------------------------------------------------------------------------------------------"
    + "---------------------------------------------------";



// Doctor Menu Texts
public static final String DOCTOR_MENU_HEADER = "\n" + FORMAT + "\n" + " DOCTOR MANAGEMENT\n" + FORMAT;
public static final String DOCTOR_MENU_OPTION =
               "1. Register New Doctor\n" +
               "2. Display All Doctors\n" +
               "3. Search Doctor (by ID or Email)\n" +
               "4. Update Doctor Details\n" +
               "5. Delete Doctor\n" +
               "6. Display Available Doctors\n" +
               "7. Search by Specialization\n" +
               "8. Sort & Display Doctors\n" +
               "9. Back to Main Menu\n" +
               FORMAT;
public static final String DOCTOR_MENU_PROMPT = "\nPlease select an option (1-9): ";

// Doctor-specific messages
public static final String DOCTOR_NOT_FOUND = "Doctor not found.";
public static final String DOCTOR_UPDATED = "Doctor information updated successfully.";
public static final String DOCTOR_DELETED = "Doctor deleted successfully.";
public static final String DOCTOR_REGISTERED = "Doctor registered successfully.";
public static final String DUPLICATE_DOCTOR_EMAIL = "Doctor with this email already exists.";
public static final String INVALID_CONSULTATION_FEE = "Invalid consultation fee! Please enter a positive number.";
public static final String CONFIRM_DOCTOR_UPDATE = "Are you sure you want to update this doctor? (Y/N): ";
public static final String CONFIRM_DOCTOR_DELETE = "Are you sure you want to delete this doctor? (Y/N): ";
public static final String DOCTOR_UPDATE_CANCELLED = "Doctor update cancelled.";
public static final String DOCTOR_DELETE_CANCELLED = "Doctor delete cancelled.";

}