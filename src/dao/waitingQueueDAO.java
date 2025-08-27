package dao;

import adt.CircularDoublyLinkedList;
import adt.ListInterface;
import entity.Patient;
import java.util.Comparator;

/**
 *
 * @author yuhang
 */

public class waitingQueueDAO {

    private ListInterface<Patient> queue = new CircularDoublyLinkedList<>();

    public waitingQueueDAO() { 
        
    }

    public boolean isEmpty() { 
        return queue.isEmpty(); 
    }

    ///not using yet
    public ListInterface<Patient> getAll(){
        return queue;
    }

    public void clear() { 
        queue.clear(); 
    }

    public Patient getEntry(int position) { 
        if(position < 1 || position > queue.getNumberOfEntries()){
            return null;
        }
        return queue.getEntry(position); 
    }

    // enqueue fifo 
    public Patient serveNext() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.remove(1);
    }

    public Patient removeAtPosition(int index){
        int n = queue.getNumberOfEntries();
        if(index < 1||index > n){
            return null;
        }
        return queue.remove(index);
        
    }

    
    public boolean add(Patient p) {
        return queue.add(p);
    }


    public boolean addByIdWithPriority(String patientId, int priority, PatientDAO dao) {
    Patient p = dao.findPatientById(patientId);
    if (p == null){
         return false;
    }
    if (priority < 1 || priority > 5){
        return false;
    }

    PatientWithPriority incoming = new PatientWithPriority(p, priority);

    ListInterface<PatientWithPriority> list = new CircularDoublyLinkedList<>();
    for (int i = 1; i <= queue.getNumberOfEntries(); i++) {
        Patient existing = queue.getEntry(i);
        list.add(new PatientWithPriority(existing, 5));
    }

    Comparator<PatientWithPriority> comp = (a, b) -> Integer.compare(a.priority, b.priority);
    list.addWithPriority(comp, incoming);

    queue.clear();
    for (PatientWithPriority pwp : list) {
        queue.add(pwp.patient);
    }

    return true;
}


    public boolean removeById(String patientId) {
        if (queue.isEmpty()) return false;
        for (int i = 1; i <= queue.getNumberOfEntries(); i++) {
            if (queue.getEntry(i).getPatientId().equalsIgnoreCase(patientId)) {
                queue.remove(i);
                return true;
            }
        }
        return false;
    }

    
    public int containsId(String patientId) {
        if(patientId == null || patientId.isBlank()){
            return -1;
        }
        int n = queue.getNumberOfEntries();
        for(int i = 1; i <= n; i++){
            Patient p = queue.getEntry(i);
            if(p != null && patientId.equalsIgnoreCase(p.getPatientId())){
                return i;
            }
        }
        return -1;
       
    }

    
    public boolean replaceAtPosition(int position, Patient newPatient) {
        return queue.replace(position, newPatient);
    }

    public int getPatientCount() {
        return queue.getNumberOfEntries();
    }



    //inner class
    private static class PatientWithPriority {
         Patient patient;
         int priority;
        PatientWithPriority(Patient patient, int priority) {
            this.patient = patient; this.priority = priority;
        }
    }
}
