/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import control.ConsultationManager;
import control.PatientManager;
import control.PharmacyManager;
import control.TreatmentManager;
import dao.ConsultationDAO;
import dao.PatientDAO;
import dao.TreatmentDAO;
import dao.waitingQueueDAO;
import java.util.Scanner;

/**
 *
 * @author USER
 */
public class MainMenu {
    
    PatientDAO sharedPatientDAO = new PatientDAO();
    waitingQueueDAO sharedWaitingQueue = new waitingQueueDAO();
     ConsultationDAO sharedConsultationDAO = new ConsultationDAO();
    ConsultationManager sharedConsultationManager = new ConsultationManager(sharedConsultationDAO, sharedPatientDAO, sharedWaitingQueue);

//    ConsultationDAO sharedConsultationDAO = new ConsultationDAO();
//    ConsultationManager sharedConsultationManager = new ConsultationManager(sharedConsultationDAO, sharedPatientDAO, sharedWaitingQueue);
//
//    private PatientDAO patientDAO = new PatientDAO();
    private ConsultationDAO consultationDAO = new ConsultationDAO();
    private TreatmentDAO treatmentDAO = new TreatmentDAO(consultationDAO);

    //private ConsultationManager consultationManager = new ConsultationManager(consultationDAO, sharedPatientDAO);
    private PharmacyManager pharmacyManager = new PharmacyManager(treatmentDAO);

    TreatmentManager treatmentManager = new TreatmentManager(treatmentDAO, consultationDAO, pharmacyManager, sharedConsultationManager);

    private Scanner scanner = new Scanner(System.in);

    public MainMenu() {
    }

    public void displayMainMenu() {
        System.out.println(Messages.WELCOME);
        System.out.println(Messages.MAIN_MENU_HEADER);
    }

    public void getUserChoice() {
        int choice;

     
        do {
            System.out.println(Messages.MAIN_MENU_OPTION);

            try {
                System.out.print("Enter the option (1-7): ");
                choice = Integer.parseInt(scanner.nextLine().trim());

                if (choice >= 1 && choice <= 7) {

                    switch (choice) {
                        case 1:
                            System.out.println("\nRedirecting to Patient Portal...");
                            PatientManager patientManager = new PatientManager(sharedPatientDAO, sharedWaitingQueue);
                            new PatientMenu(patientManager).start();
                            break;
                        case 2:
                            System.out.println("\nRedirecting to Doctor Management...");
                            DoctorMenu doctorMenu = new DoctorMenu();
                            doctorMenu.displayMenu();
                            break;
                        case 3:
                            System.out.println("\nRedirecting to Consultation Management...");
                            //ConsultationManager consultationManager = new ConsultationManager(consultationDAO, patientDAO);
                            sharedConsultationManager.runConsultationMenu();
                            break;
                        case 4:
                            System.out.println("\nRedirecting to Treatment Management...");
                            TreatmentMenu treatmentMenu = new TreatmentMenu(treatmentManager);
                            treatmentMenu.runTreatmentMenu();
                            break;
                        case 5:
                            System.out.println("\nRedirecting to Pharmacy Management...");
                            PharmacyMenu pharmacyMenu = new PharmacyMenu(pharmacyManager);
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

                } else {
                    System.out.printf(Messages.INVALID_CHOICE, 1, 7);
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

    public static void main(String[] args) {
        MainMenu p1 = new MainMenu();
        p1.displayMainMenu();
        p1.getUserChoice();
    }

}
