/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import control.PatientManager;
import entity.Patient;
import java.util.Scanner;

/**
 *
 * @author yuhang
 */
public class PatientMenu {

    private PatientManager manager;
    private Scanner sc = new Scanner(System.in);
    
    public PatientMenu(){

    }
    
    public PatientMenu(PatientManager manager){
       this.manager = manager;
    }

    public void start(){
        int choice;

        do{
            System.out.println(Messages.PATIENT_MENU_HEADER);
            System.out.println(Messages.PATIENT_MENU_OPTION);
            System.out.print(Messages.PATIENT_MENU_PROMPT);

            //detect if got buffer
            while(!sc.hasNextInt()){
                System.out.println(Messages.INVALID_NUM);
                sc.nextLine();
        }
        choice = sc.nextInt();
        sc.nextLine();  //clear buffer

        switch (choice) {
    case 1: 
           registerPatient();
           break;          
    case 2:
           manager.displayAllPatients();
           break;
    case 3:
           searchPatient();
           break;
    case 4:
            updatePatient();
            break;
    case 5:
            deletePatient();
            break;
    case 6:
            manageQueue();
            break;
    case 7:
            manager.generateReports();
            break;
    case 8:
            sortPatientsMenu();
            break;
    case 9:
            advancedPatientOperations();
            break;
    case 10:
            System.out.println("Returning to Main Menu...");
            break;
    default:
            System.out.printf(Messages.INVALID_CHOICE,1,10);
        }

    }while(choice != 10);
    
}

private void registerPatient(){

    String ic;
    String name;
    String gender;
    int age;
    String phone;
    String address;
    String email;
    String history;
    
    while(true){
        System.out.println(Messages.ENTER_PATIENT_IC);
        ic = sc.nextLine().trim();
        if (!ic.matches("\\d{12}")) {
            System.out.println(Messages.INVALID_IC);
            continue;
        }
        if (manager.getPatientByIC(ic) != null) {
            System.out.println(Messages.DUPLICATE_IC);
            continue;
        }
        break;
    }

        System.out.print(Messages.ENTER_PATIENT_NAME);
         name = sc.nextLine();

         while(true){
        System.out.print(Messages.ENTER_PATIENT_GENDER);
         gender = sc.nextLine();
        
         if(!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female")){
               System.out.println(Messages.INVALID_INPUT);           
            continue;
         }
        
         break; 
        }
         
        while(true){
        System.out.print(Messages.ENTER_PATIENT_AGE);
        String ageStr = sc.nextLine().trim();

        try{
            age = Integer.parseInt(ageStr);
            if(age <= 0 || age > 120){
                System.out.println(Messages.INVALID_AGE);
                continue;
            }
            break;
        }catch(NumberFormatException e){
            System.out.println(Messages.INVALID_AGE);
        }
      }


    while(true){
        System.out.print(Messages.ENTER_PATIENT_PHONE);
         phone = sc.nextLine().trim();
         if(!phone.matches("\\d{12}")){
            System.out.println(Messages.INVALID_PHONE);
            continue;
         }
         break;
    }

        System.out.print(Messages.ENTER_PATIENT_ADDRESS);
         address = sc.nextLine();

        

    while(true){
        System.out.print(Messages.ENTER_PATIENT_EMAIL);
         email = sc.nextLine().trim();
        if(email.equals("-")){
            email="";
            break;
        }else if(!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")){
        System.out.println(Messages.INVALID_EMAIL);
        continue;
        }
        break;
        }
 
        System.out.print(Messages.ENTER_PATIENT_HISTORY);
         history = sc.nextLine().trim();


        boolean isSuccessfull = manager.registerPatient(ic,name, gender, age, phone, address, email, history);
        if(isSuccessfull){
            System.out.println(Messages.PATIENT_REGISTERED);
        }
    }

private void searchPatient() {
  System.out.println("Enter Patient ID or IC");
  String keyword = sc.nextLine();
  manager.searchPatient(keyword);

}

//now change the validation
private void updatePatient() {
    System.out.print("Enter Patient ID to update: ");
    String id = sc.nextLine().trim();

    // check whether the id exists
    Patient existing = manager.getPatientById(id);
    if (existing == null) {
        System.out.println(Messages.PATIENT_NOT_FOUND);
        return;
    }

    System.out.println("Current patient data: ");
    System.out.println(existing);

    // Input new details (press '-' to keep old value)
    System.out.print(Messages.ENTER_PATIENT_NAME + " (Current: " + existing.getName() + ", '-' to keep): ");
    String name = sc.nextLine().trim();
    if (name.equals("-") || name.isEmpty()) name = existing.getName();

    String gender;

        while (true) {
            System.out.print(Messages.ENTER_PATIENT_GENDER + " (Current: " + existing.getGender() + ", '-' to keep): ");
            gender = sc.nextLine().trim();
            if (gender.equals("-")) {
                gender = existing.getGender();
                break;
            }
            if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female")) {
                System.out.println(Messages.INVALID_INPUT);
                continue;
            }
            break;
        }

      int age;
    while(true){
    System.out.print(Messages.ENTER_PATIENT_AGE + " (Current: " + existing.getAge() + ", '-' to keep): ");
    String ageStr = sc.nextLine().trim();
    if (ageStr.equals("-")) {
        age = existing.getAge();
        break;
    } else {
        try {
            age = Integer.parseInt(ageStr);
            if(age <=0 || age > 120){
                System.out.println(Messages.INVALID_INPUT);
                    continue;
            }
            break;
        } catch (NumberFormatException e) {
            System.out.println(Messages.INVALID_AGE);
        }
    }
}

   String phone;

   while(true){
    System.out.print(Messages.ENTER_PATIENT_PHONE + " (Current: " + existing.getPhoneNumber() + ", '-' to keep): ");
     phone = sc.nextLine().trim();
    if (phone.equals("-") ) 
    {
    phone = existing.getPhoneNumber();
    break;
    }else if(!phone.matches("\\d{12}")){
        System.out.println(Messages.INVALID_PHONE);
        continue;
    }
    break;
   }
   

    System.out.print(Messages.ENTER_PATIENT_ADDRESS + " (Current: " + existing.getAddress() + ", '-' to keep): ");
    String address = sc.nextLine().trim();
    if (address.equals("-")) address = existing.getAddress();

    String email;
    while(true){
    System.out.print(Messages.ENTER_PATIENT_EMAIL + " (Current: " + existing.getEmail() + ", '-' to keep): ");
    email = sc.nextLine().trim();
    if (email.equals("-")) 
    {
    email = existing.getEmail();
    break;
    }
    if(!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")){
        System.out.println(Messages.INVALID_EMAIL);
        continue;
    }
    break;
}

    System.out.print(Messages.REENTER_PATIENT_HISTORY + " (Current: " + existing.getMedicalHistory() + ", '-' to keep): ");
    String history = sc.nextLine().trim();
    if (history.equals("-")) 
    {
        history = existing.getMedicalHistory();
    }

    System.out.print(Messages.CONFIRM_UPDATE);
    String confirm = sc.nextLine().trim();

    if (confirm.equalsIgnoreCase("Y")) {
        manager.updatePatient(id, existing.getIcNumber(), name, gender, age, phone, address, email, history);
        System.out.println("Patient updated successfully.");
    } else {
        System.out.println(Messages.UPDATE_CANCELLED);
    }
}


private void deletePatient(){
 System.out.print("Enter Patient ID to delete:");
 String id = sc.nextLine().trim();

 Patient existing = manager.getPatientById(id);
    if (existing == null) {
        System.out.println(Messages.PATIENT_NOT_FOUND);
        return;
    }

    System.out.println("Patient to be deleted:");
    System.out.println(existing);

    System.out.print(Messages.CONFIRM_DELETE);
    String confirm = sc.nextLine().trim();
    if(confirm.equalsIgnoreCase("Y")){
        manager.deletePatient(id);
    }else{
        System.out.println(Messages.DELETE_CANCELLED);
    }
 
}

private void manageQueue(){
 int choice;

 do{
    System.out.println("\n" + Messages.FORMAT);
        System.out.println(" PATIENT QUEUE MANAGEMENT");
        System.out.println(Messages.FORMAT);
        System.out.println("1. Add patient to queue (normal priority)");
        System.out.println("2. Add patient to queue (with priority (1-highest --- 5-lowest))");
        System.out.println("3. Peek next patient (use for Consultation Menu to serve)");
        System.out.println("4. View queue");
        System.out.println("5. Remove patient from queue");
        System.out.println("6. Check if patient is in queue");
        System.out.println("7. Replace patient in queue");
        System.out.println("8. Clear entire queue");
        System.out.println("9. Show queue size");
        System.out.println("10. Get patient at queue position");
        System.out.println("11. Display queue statistics");
        System.out.println("12. Back to Patient Menu");
        System.out.print("Enter choice: ");

        while(!sc.hasNextInt()){
            System.out.println(Messages.INVALID_INPUT);
            sc.nextLine();
        }
        choice = sc.nextInt();
        sc.nextLine(); //clear buffer

        switch(choice) {
            case 1:
                System.out.print(Messages.ENTER_PATIENT_ID);
                String id = sc.nextLine();
                if(manager.addPatientToQueue(id)){
                    System.out.println("Patient added to queue.");
                }else{
                    System.out.println(Messages.PATIENT_NOT_FOUND);
                }
                break;

            case 2:
                System.out.print(Messages.ENTER_PATIENT_ID);
                String priorityId = sc.nextLine();
                System.out.print("Enter priority (1=highest, 5=lowest): ");
                while(!sc.hasNextInt()){
                    System.out.println(Messages.INVALID_INPUT);
                    sc.nextLine();
                }
                int priority = sc.nextInt();
                sc.nextLine();

                if(manager.addPatientToQueueWithPriority(priorityId, priority)){
                    System.out.println("Patient added to queue with priority " + priority + ".");
                }else{
                    System.out.println("Failed to add patient. Check patient ID and priority.");
                }
                break;

            case 3:{
                 Patient head = manager.peekNextPatient();
             if (head != null) {
             System.out.println("Next in queue (not removed): " + head);
             } else {
             System.out.println("Queue is empty.");
                 }
                break;
                }

            case 4:
                manager.displayWaitingQueue();
                break;

            case 5:
                System.out.print("Enter Patient ID to remove from queue: ");
                String removeId = sc.nextLine();
                if(manager.removePatientFromQueue(removeId)){
                    System.out.println("Patient removed from queue.");
                }else{
                    System.out.println("Patient not found in queue.");
                }
                break;

            case 6:
                System.out.print("Enter Patient ID to check: ");
                String checkId = sc.nextLine();
                if(manager.isPatientInQueue(checkId) > 0){
                    System.out.println("Patient is in the queue.");
                }else{
                    System.out.println("Patient is not in the queue.");
                }
                break;

            case 7:
                System.out.print("Enter queue position to replace: ");
                while(!sc.hasNextInt()){
                    System.out.println(Messages.INVALID_INPUT);
                    sc.nextLine();
                }
                int position = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter new Patient ID: ");
                String newId = sc.nextLine();

                if(manager.replacePatientInQueue(position, newId)){
                    System.out.println("Patient replaced successfully.");
                }else{
                    System.out.println("Failed to replace patient. Check position and patient ID.");
                }
                break;

            case 8:
                System.out.print("Are you sure you want to clear the entire queue? (Y/N): ");
                String confirm = sc.nextLine();
                if(confirm.equalsIgnoreCase("Y")){
                    manager.clearQueue();
                }else{
                    System.out.println("Queue clear cancelled.");
                }
                break;
            case 9:
                System.out.println("Queue size: " + manager.getQueueSize());
                break;
            case 10:
                getthePosition();
                break;
            case 11:
                manager.displayQueueStatistics();
                break;
            case 12:
                System.out.println("Returning to Patient Menu...");
                start();
                break;
            default:
                System.out.printf(Messages.INVALID_CHOICE, 1, 12);
        }
 }while(choice != 12);

}

private void getthePosition(){
System.out.print("Enter queue position: ");
while(!sc.hasNextInt()){
    System.out.println(Messages.INVALID_INPUT);
    sc.nextLine();
}
    int pos = sc.nextInt();
    sc.nextLine();
    
    Patient p = manager.getPatientAtQueuePosition(pos);
    
    if(p != null){
    System.out.println("\nPatient at position " + pos + ":");
    System.out.println(p);
    }else{
    System.out.println("No patient at that position.");
    }
    

}


private void sortPatientsMenu(){
     System.out.println("\n" + Messages.FORMAT);
    System.out.println(" SORT & DISPLAY PATIENTS");
    System.out.println(Messages.FORMAT);
    System.out.println("1. Sort by Name (Forward)");
    System.out.println("2. Sort by Age (Forward)");
    System.out.println("3. Sort by ID (Forward)");
    System.out.println("4. Display Patients in Reverse Order");
    System.out.println("5. Back to Patient Menu");
    System.out.print("Enter choice: ");

    //clear buffer
    while (!sc.hasNextInt()) {
        System.out.println(Messages.INVALID_INPUT);
        sc.nextLine();
    }

    int choice = sc.nextInt();
    sc.nextLine();

    switch (choice) {
        case 1:
            manager.sortPatientsByName();
            manager.displayAllPatients();
            break;
        case 2:
            manager.sortPatientsByAge();
            manager.displayAllPatients();
            break;
        case 3:
           manager.sortPatientsById();
           manager.displayAllPatients();
           break;
        case 4:
            manager.displayPatientsReverse();
            break;
        case 5:
         System.out.println("Returning to Patient Menu...");
            break;
        default:
            System.out.println("Invalid choice.");
    }
}

// New method for advanced patient operations
private void advancedPatientOperations() {
    
    System.out.println("\n" + Messages.FORMAT);
    System.out.println(" ADVANCED PATIENT OPERATIONS");
    System.out.println(Messages.FORMAT);
    System.out.println("1. Insert Patient at Specific Position");
    System.out.println("2. Replace Patient at Position");
    System.out.println("3. Check if Patient Exists");
    System.out.println("4. Get Patient Count");
    System.out.println("5.Search patient by AGE");
    System.out.println("6.Display patients by Gender");
    System.out.println("7.Display patients by AGE RANGE");
    System.out.println("8.Batch UPDATE location (from -> to)");
    System.out.println("9. Back to Patient Menu");
    System.out.print("Enter choice: ");

    while (!sc.hasNextInt()) {
        System.out.println(Messages.INVALID_INPUT);
        sc.nextLine();
    }

    int choice = sc.nextInt();
    sc.nextLine();

    switch (choice) {
        
        case 1:
            insertPatientAtPosition();
            break;
        case 2:
            replacePatientAtPosition();
            break;
        case 3:
            checkPatientExists();
            break;
        case 4:
            System.out.println("Total patients: " + manager.getPatientCount());
            break;
        case 5:
            searchPatientByAge();
            break;
        case 6:
            displayPatientByGender();
            break;
        case 7:
            displayPatientByAgeRange();
            break;
        case 8:
            batchChangeLocation();
            break;
        case 9:
            System.out.println("Returning to Patient Menu...");
            break;
        default:
            System.out.println("Invalid choice.");
    }

}



private void batchChangeLocation(){
System.out.print("From location (city prefix): ");
    String fromLoc = sc.nextLine().trim();
    System.out.print("To   location (new city): ");
    String toLoc = sc.nextLine().trim();
    if (fromLoc.isEmpty() || toLoc.isEmpty()) {
        System.out.println("Location cannot be empty.");
    } else {
        manager.updateAllPatientsInLocation(fromLoc, toLoc);
    }
    
    }

private void displayPatientByAgeRange(){
     System.out.print("Min age: ");
    while (!sc.hasNextInt()) { System.out.println(Messages.INVALID_INPUT); sc.nextLine(); }
    int minA = sc.nextInt(); sc.nextLine();
    System.out.print("Max age: ");
    while (!sc.hasNextInt()) { System.out.println(Messages.INVALID_INPUT); sc.nextLine(); }
    int maxA = sc.nextInt(); sc.nextLine();
    if (minA > maxA) { int t = minA; minA = maxA; maxA = t; }
    manager.displayPatientsByAgeRange(minA, maxA);
}

private void displayPatientByGender(){
   System.out.print("Enter gender (Male/Female/Other): ");
   String g = sc.nextLine().trim();
   manager.displayPatientsByGender(g);
}

private void searchPatientByAge(){
   System.out.print("Enter exact age: ");
        while(!sc.hasNextInt()){
        System.out.println(Messages.INVALID_INPUT);
        sc.nextLine();
        }
        int age = sc.nextInt();
        sc.nextLine();
        Patient p = manager.searchPatientByAge(age);
        if(p!= null){
        System.out.println("\nFound: ");
        System.out.println(p);
        }else{
             System.out.println("No patient with age = " + age);
         }
        
}



private void insertPatientAtPosition() {
    System.out.print("Enter position to insert patient: ");
    while(!sc.hasNextInt()){
        System.out.println(Messages.INVALID_INPUT);
        sc.nextLine();
    }
    int position = sc.nextInt();
    sc.nextLine();

    // Get patient details
    String ic, name, gender, phone, address, email, history;
    int age;

    while(true){
        System.out.println(Messages.ENTER_PATIENT_IC);
        ic = sc.nextLine().trim();
        if (!ic.matches("\\d{12}")) {
            System.out.println(Messages.INVALID_IC);
            continue;
        }
        if (manager.getPatientByIC(ic) != null) {
            System.out.println(Messages.DUPLICATE_IC);
            continue;
        }
        break;
    }

    System.out.print(Messages.ENTER_PATIENT_NAME);
    name = sc.nextLine();

    while(true){
        System.out.print(Messages.ENTER_PATIENT_GENDER);
        gender = sc.nextLine();
        if(!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female")){
            System.out.println(Messages.INVALID_INPUT);
            continue;
        }
        break;
    }

    while(true){
        System.out.print(Messages.ENTER_PATIENT_AGE);
        String ageStr = sc.nextLine().trim();
        try{
            age = Integer.parseInt(ageStr);
            if(age <= 0 || age > 120){
                System.out.println(Messages.INVALID_AGE);
                continue;
            }
            break;
        }catch(NumberFormatException e){
            System.out.println(Messages.INVALID_AGE);
        }
    }

    while(true){
        System.out.print(Messages.ENTER_PATIENT_PHONE);
        phone = sc.nextLine().trim();
        if(!phone.matches("\\d{12}")){
            System.out.println(Messages.INVALID_PHONE);
            continue;
        }
        break;
    }

    System.out.print(Messages.ENTER_PATIENT_ADDRESS);
    address = sc.nextLine();

    while(true){
        System.out.print(Messages.ENTER_PATIENT_EMAIL);
        email = sc.nextLine().trim();
        if(email.equals("-")){
            email="";
            break;
        }else if(!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")){
            System.out.println(Messages.INVALID_EMAIL);
            continue;
        }
        break;
    }

    System.out.print(Messages.ENTER_PATIENT_HISTORY);
    history = sc.nextLine().trim();

    boolean success = manager.insertPatientAtPosition(position, ic, name, gender, age, phone, address, email, history);
    if(success){
        System.out.println("Patient inserted successfully at position " + position + ".");
    } else {
        System.out.println("Failed to insert patient. Check position or duplicate data.");
    }
}

private void replacePatientAtPosition() {
    System.out.print("Enter position to replace: ");
    while(!sc.hasNextInt()){
        System.out.println(Messages.INVALID_INPUT);
        sc.nextLine();
    }
    int position = sc.nextInt();
    sc.nextLine();

    Patient currentPatient = manager.getPatientAtPosition(position);
    if(currentPatient == null) {
        System.out.println("No patient found at position " + position);
        return;
    }

    System.out.println("Current patient at position " + position + ": " + currentPatient.getName());

    // Get new patient details
    String ic, name, gender, phone, address, email, history;
    int age;

    while(true){
        System.out.println(Messages.ENTER_PATIENT_IC);
        ic = sc.nextLine().trim();
        if (!ic.matches("\\d{12}")) {
            System.out.println(Messages.INVALID_IC);
            continue;
        }
        break;
    }

    System.out.print(Messages.ENTER_PATIENT_NAME);
    name = sc.nextLine();

    while(true){
        System.out.print(Messages.ENTER_PATIENT_GENDER);
        gender = sc.nextLine();
        if(!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female")){
            System.out.println(Messages.INVALID_INPUT);
            continue;
        }
        break;
    }

    while(true){
        System.out.print(Messages.ENTER_PATIENT_AGE);
        String ageStr = sc.nextLine().trim();
        try{
            age = Integer.parseInt(ageStr);
            if(age <= 0 || age > 120){
                System.out.println(Messages.INVALID_AGE);
                continue;
            }
            break;
        }catch(NumberFormatException e){
            System.out.println(Messages.INVALID_AGE);
        }
    }

    while(true){
        System.out.print(Messages.ENTER_PATIENT_PHONE);
        phone = sc.nextLine().trim();
        if(!phone.matches("\\d{12}")){
            System.out.println(Messages.INVALID_PHONE);
            continue;
        }
        break;
    }

    System.out.print(Messages.ENTER_PATIENT_ADDRESS);
    address = sc.nextLine();

    while(true){
        System.out.print(Messages.ENTER_PATIENT_EMAIL);
        email = sc.nextLine().trim();
        if(email.equals("-")){
            email="";
            break;
        }else if(!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")){
            System.out.println(Messages.INVALID_EMAIL);
            continue;
        }
        break;
    }

    System.out.print(Messages.ENTER_PATIENT_HISTORY);
    history = sc.nextLine().trim();

    Patient newPatient = new Patient(currentPatient.getPatientId(), ic, name, gender, age, phone, address, email, history);
    boolean success = manager.replacePatientAtPosition(position, newPatient);
    if(success){
        System.out.println("Patient replaced successfully at position " + position + ".");
    } else {
        System.out.println("Failed to replace patient.");
    }
}


private void checkPatientExists() {
    System.out.print("Enter Patient ID to check: ");
    String id = sc.nextLine().trim();

    Patient patient = manager.getPatientById(id);
    if(patient != null) {
        System.out.println("Patient exists: " + patient.getName());
        if(manager.containsId(id)>0) {
            System.out.println("Patient is confirmed to be in the system.");
        }
    } else {
        System.out.println("Patient does not exist.");
    }
}




}