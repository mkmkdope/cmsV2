package adt;

import java.util.Comparator;
import java.util.Iterator;

public interface ListInterface<T> extends Iterable<T> {
    // Core methods
    int getNumberOfEntries();
    boolean isEmpty();
    boolean isFull(); // Added missing method
    void clear();

    boolean add(T newEntry); // Fixed: should return boolean
    boolean add(int newPosition, T newEntry);   // Fixed: should return boolean (1-based)
    T remove(int givenPosition);                // 1-based
    boolean replace(int givenPosition, T newEntry); // Fixed: should return boolean
    T getEntry(int givenPosition);

    boolean contains(T anEntry);

    //change to iterator and reverseiterator
    // Traversal helpers
    //void traverseForward(Consumer<T> action);
    //void traverseBackward(Consumer<T> action);

    //this one not suitable
    // Advanced methods
    /** In-place bubble sort by swapping payloads; stable for small/medium lists. */
    //void bubbleSort(Comparator<? super T> comparator);

    /** Returns 1-based index of key using comparator equality; -1 if not found. */
    int searchByKey(Comparator<? super T> comparator, T key);

    /** Insert respecting a priority order (e.g., triage), based on comparator. */
    boolean addWithPriority(Comparator<? super T> priorityComparator, T newEntry); // Fixed: should return boolean


     void mergeSort(Comparator<? super T> comparator);
      Iterator<T> reverseIterator();

     


}