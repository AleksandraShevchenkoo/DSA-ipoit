package by.it.group410971.shevchenko.lesson11;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Node<E>[] table;
    private Node<E> head; // первый добавленный элемент
    private Node<E> tail; // последний добавленный элемент
    private int size;

    private static class Node<E> {
        E data;
        Node<E> next; // следующий в цепочке коллизий
        Node<E> before; // предыдущий в порядке добавления
        Node<E> after; // следующий в порядке добавления

        Node(E data) {
            this.data = data;
            this.next = null;
            this.before = null;
            this.after = null;
        }
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        this.table = new Node[DEFAULT_CAPACITY];
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        this.table = new Node[initialCapacity];
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<E> current = head;
        int count = 0;
        while (current != null) {
            sb.append(current.data);
            count++;
            if (count < size) {
                sb.append(", ");
            }
            current = current.after;
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }

        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }

        int index = getIndex(element);
        Node<E> current = table[index];

        // Проверяем, нет ли уже такого элемента
        while (current != null) {
            if (element.equals(current.data)) {
                return false; // Элемент уже существует
            }
            current = current.next;
        }

        // Добавляем новый элемент
        Node<E> newNode = new Node<>(element);

        // Добавляем в хеш-таблицу
        newNode.next = table[index];
        table[index] = newNode;

        // Добавляем в связный список порядка добавления
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.after = newNode;
            newNode.before = tail;
            tail = newNode;
        }

        size++;
        return true;
    }

    @Override
    public boolean remove(Object element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }

        int index = getIndex(element);
        Node<E> current = table[index];
        Node<E> prev = null;

        while (current != null) {
            if (element.equals(current.data)) {
                // Удаляем из хеш-таблицы
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                // Удаляем из связного списка порядка добавления
                removeFromLinkedList(current);

                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean contains(Object element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }

        int index = getIndex(element);
        Node<E> current = table[index];

        while (current != null) {
            if (element.equals(current.data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Node<E> current = head;
        while (current != null) {
            Node<E> next = current.after;
            if (!c.contains(current.data)) {
                remove(current.data);
                modified = true;
            }
            current = next;
        }
        return modified;
    }

    // Вспомогательные методы

    private int getIndex(Object element) {
        return Math.abs(element.hashCode()) % table.length;
    }

    private void removeFromLinkedList(Node<E> node) {
        if (node.before != null) {
            node.before.after = node.after;
        } else {
            head = node.after;
        }

        if (node.after != null) {
            node.after.before = node.before;
        } else {
            tail = node.before;
        }

        node.before = null;
        node.after = null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        table = new Node[oldTable.length * 2];
        size = 0;

        // Сохраняем порядок элементов
        Node<E> current = head;
        head = null;
        tail = null;

        while (current != null) {
            // Перехешируем элемент
            int index = getIndex(current.data);
            Node<E> newNode = new Node<>(current.data);

            // Добавляем в хеш-таблицу
            newNode.next = table[index];
            table[index] = newNode;

            // Добавляем в связный список порядка добавления
            if (tail == null) {
                head = newNode;
                tail = newNode;
            } else {
                tail.after = newNode;
                newNode.before = tail;
                tail = newNode;
            }

            size++;
            current = current.after;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////        Остальные методы Set - можно оставить пустыми       ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
}