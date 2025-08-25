/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import control.ConsultationManager;
import control.PatientManager;
import control.DoctorManager;
import dao.PatientDAO;
import dao.DoctorDAO;
import entity.Patient;
import entity.Treatment;
import java.util.Scanner;
import entity.Doctor;
/**
 *
 * @author USER
 */
public class MainMenu {

    private Scanner scanner = new Scanner(System.in);

    public MainMenu(){
    }

    public void displayMainMenu(){
        System.out.println(Messages.WELCOME);
        System.out.println(Messages.MAIN_MENU_HEADER);
    }

public void getUserChoice() {
    int choice;
    do {
        System.out.println(Messages.MAIN_MENU_OPTION);
        
        try {
            System.out.print("Enter number:");
            choice = Integer.parseInt(scanner.nextLine().trim());

            if(choice >=1 && choice <= 7){

             switch(choice){
                    case 1:
                        System.out.println("\nRedirecting to Patient Portal...");
                        PatientDAO patientDAO = new PatientDAO();
                        PatientManager patientManager = new PatientManager(patientDAO);
                        PatientMenu patientMenu = new PatientMenu(patientManager);
                        patientMenu.start();
                        break;
                    case 2:
                        System.out.println("\nRedirecting to Doctor Management...");
                        DoctorMenu doctorMenu = new DoctorMenu();
                        doctorMenu.displayMenu();
                        break;
                    case 3:
                        System.out.println("\nRedirecting to Consultation Management...");
                        ConsultationManager consultationManager = new ConsultationManager();
                        consultationManager.runConsultationMenu();
                        break;
                    case 4:
                        System.out.println("\nRedirecting to Treatment Management...");
                        TreatmentMenu treatmentMenu = new TreatmentMenu();
                        treatmentMenu.runTreatmentMenu();
                        break;
                    case 5:
                        System.out.println("\nRedirecting to Pharmacy Management...");
                        PharmacyMenu pharmacyMenu = new PharmacyMenu();
                        pharmacyMenu.runPharmacyMenu();
                        break;
                    case 6:
                        System.out.println("\nRedirecting to System Reports...");
                        // TODO: Implement System Reports functionality
                        System.out.println("System Reports feature coming soon...");
                        break;
                    case 7:
                        System.out.println("\nThank you for using TAR UMT Clinic Management System!");
                        System.out.println("System shutting down...");
                        System.exit(0);
                        break;
                    }

           }
            else{
                System.out.printf(Messages.INVALID_CHOICE, 1,7);
                choice = 0; // Force loop to continue
            }

         
        } catch (NumberFormatException e) {
            System.out.println(Messages.INVALID_INPUT);
            choice = 0; // Force loop to continue
        }
        
        // Add a small pause and clear screen effect before showing menu again
        if (choice != 7) {
            System.out.println("\n" + Messages.MAIN_MENU_HEADER);
        }

    } while (choice != 7);
}

   


    public static void main(String[] args){
        MainMenu p1 = new MainMenu();
        p1.displayMainMenu();
        p1.getUserChoice();
    }
    
}