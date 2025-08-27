package adt;

import java.util.Comparator;
import java.util.Iterator;

public interface ListInterface<T> extends Iterable<T> {

    int getNumberOfEntries();

    boolean isEmpty();

    boolean isFull();

    void clear();

    boolean add(T newEntry);

    boolean add(int newPosition, T newEntry);

    T remove(int givenPosition);

    boolean replace(int givenPosition, T newEntry);

    T getEntry(int givenPosition);

    boolean contains(T anEntry);

    int searchByKey(Comparator<T> comparator, T key);

    boolean addWithPriority(Comparator<T> priorityComparator, T newEntry);

    void mergeSort(Comparator<T> comparator);

    Iterator<T> reverseIterator();

    void rotate(int steps);

    void swap(int position1, int position2);

    void remove(T item);
}
