/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author USER
 */
public class CircularDoublyLinkedList<T> implements ListInterface<T> {

    private class Node {

        T data;
        Node prev;
        Node next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node head;  //first node
    private Node tail;  //last node (tail.next == head,head.prev == tail)
    private int size;

    public CircularDoublyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public int getNumberOfEntries() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        head = tail = null;
        size = 0;
    }

    @Override
    public boolean add(T newEntry) {
        add(size + 1, newEntry);
        return true;
    }

    @Override
    public boolean add(int newPosition, T newEntry) {
        if (newPosition < 1 || newPosition > size + 1) {
            throw new IndexOutOfBoundsException("Position = " + newPosition);
        }
        Node newNode = new Node(newEntry);

        if (isEmpty()) {
            // If the list is empty, initialize head and tail
            head = tail = newNode;
            head.next = head;
            head.prev = head;
        } else if (newPosition == 1) {
            // Insert at the beginning
            newNode.next = head;
            newNode.prev = tail;
            tail.next = newNode;
            head.prev = newNode;
            head = newNode;
        } else if (newPosition == size + 1) {
            // Insert at the end
            newNode.next = head;
            newNode.prev = tail;
            tail.next = newNode;
            head.prev = newNode;
            tail = newNode;
        } else {
            // Insert in the middle
            Node current = nodeAt(newPosition);
            Node before = current.prev;
            newNode.next = current;
            newNode.prev = before;
            before.next = newNode;
            current.prev = newNode;
        }
        size++;
        return true;
    }

    @Override
    public T remove(int givenPosition) {
        if (givenPosition < 1 || givenPosition > size) {
            throw new IndexOutOfBoundsException("pos=" + givenPosition);
        }
        Node current = nodeAt(givenPosition);
        T result = current.data;

        if (size == 1) {
            head = tail = null;
        } else {
            current.prev.next = current.next;
            current.next.prev = current.prev;
            if (current == head) {
                head = current.next;
            }
            if (current == tail) {
                tail = current.prev;
            }
        }
        size--;
        return result;
    }

    @Override
    public boolean replace(int givenPosition, T newEntry) {
        try {
            nodeAt(givenPosition).data = newEntry;
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public T getEntry(int givenPosition) {
        return nodeAt(givenPosition).data;
    }

    @Override
    public boolean contains(T anEntry) {
        if (isEmpty()) {
            return false;
        }

        Node current = head;
        int i = 0;
        do {
            if ((anEntry == null && current.data == null) || (anEntry != null && anEntry.equals(current.data))) {
                return true;
            }
            current = current.next;
            i++;
        } while (current != head && i < size);
        return false;
    }

    @Override
    public boolean isFull() {
        return false; // Linked lists are never full (limited only by memory)
    }

    //== Iterator (for-each loops) ===
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node current = head;
            private int seen = 0;

            @Override
            public boolean hasNext() {
                return seen < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T val = current.data;
                current = current.next;
                seen++;
                return val;
            }
        };
    }

    @Override
    public Iterator<T> reverseIterator() {

        return new Iterator<T>() {
            private Node current = tail;
            private int seen = 0;

            @Override
            public boolean hasNext() {
                return seen < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T val = current.data;
                current = current.prev;
                seen++;
                return val;
            }
        };
    }

    // === merge sort ===
    @Override
    public void mergeSort(Comparator<T> comparator) {
        if (size <= 1) {
            return;
        }

        // 1) temporary break the circular to simple link list
        tail.next = null;
        head.prev = null;

        // 2) rearrange
        head = mergeSortOnLinear(head, comparator);

        // 3) rebuild tail
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        tail = current;

        // 4) rebuild link list
        head.prev = tail;
        tail.next = head;

    }

    private Node mergeSortOnLinear(Node h, Comparator<T> comp) {

        if (h == null || h.next == null) {
            return h;
        }

        Node mid = splitMiddle(h);
        Node left = mergeSortOnLinear(h, comp);
        Node right = mergeSortOnLinear(mid, comp);
        return mergeSorted(left, right, comp);
    }

    //same like binary search (split half)
    private Node splitMiddle(Node h) {
        Node slow = h;
        Node fast = h;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        Node mid = slow.next;
        slow.next = null;
        if (mid != null) {
            mid.prev = null;
        }
        return mid;
    }

    //compare and sort
    private Node mergeSorted(Node a, Node b, Comparator<T> comp) {
        Node dummy = new Node(null);
        Node t = dummy;

        while (a != null && b != null) {
            if (comp.compare(a.data, b.data) < 0) {
                t.next = a;
                a.prev = t;
                a = a.next;
            } else {
                t.next = b;
                b.prev = t;
                b = b.next;
            }
            t = t.next;
        }
        Node rest = a;
        if (a != null) {
            rest = a;
        } else {
            rest = b;
        }

        if (rest != null) {
            t.next = rest;
            rest.prev = t;
        }

        Node newHead = dummy.next;
        if (newHead != null) {
            newHead.prev = null;
        }
        return newHead;
    }

    // === linear search by comparator equality ===
    @Override
    public int searchByKey(Comparator<T> comparator, T key) {
        if (isEmpty()) {
            return -1;
        }
        Node current = head;
        int index = 1;
        for (int i = 0; i < size; i++) {
            if (comparator.compare(current.data, key) == 0) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    @Override
    public boolean addWithPriority(Comparator<T> priorityComparator, T newEntry) {
        if (isEmpty()) {
            add(newEntry);
            return true;
        }

        Node current = head;
        int position = 1;

        for (int i = 0; i < size; i++) {
            if (priorityComparator.compare(newEntry, current.data) < 0) {
                add(position, newEntry);
                return true;
            }
            current = current.next;
            position++;
        }
        add(size + 1, newEntry);
        return true;
    }

    private Node nodeAt(int position) {
        if (position < 1 || position > size) {
            throw new IndexOutOfBoundsException("Position = " + position);
        }
        Node current = head;
        for (int i = 1; i < position; i++) {
            current = current.next;
        }
        return current;
    }

      public int size() {
        return size;
    }
    
    public void remove(T item) {
        if (isEmpty()) return;
        
        Node current = head;
        for (int i = 0; i < size; i++) {
            if ((item == null && current.data == null) || (item != null && item.equals(current.data))) {
                if (size == 1) {
                    head = tail = null;
                } else {
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                    if (current == head) head = current.next;
                    if (current == tail) tail = current.prev;
                }
                size--;
                return;
            }
            current = current.next;
        }
    }  
    
}
