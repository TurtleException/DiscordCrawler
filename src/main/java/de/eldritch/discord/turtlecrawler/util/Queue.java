package de.eldritch.discord.turtlecrawler.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A simple <code>FIFO</code> (first-in-first-out) queue. Similar to a {@link LinkedList}, but simpler and with some
 * additional functionality.
 * <p>When the capacity of a Queue is exceeded elements will be dropped in the order they were initially added.
 * @param <E> the type of elements held in this collection
 */
@SuppressWarnings("unused")
public class Queue<E> implements Collection<E> {
    private final LinkedList<E> linkedList = new LinkedList<>();

    /**
     * Maximum amount of elements in this queue.
     */
    private int capacity;

    public Queue(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int size() {
        return linkedList.size();
    }

    @Override
    public boolean isEmpty() {
        return linkedList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return linkedList.contains(o);
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return linkedList.iterator();
    }

    @Override
    public Object[] toArray() {
        return linkedList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return linkedList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        boolean b = linkedList.add(e);
        if (linkedList.size() > capacity)
            linkedList.removeFirst();
        return b;
    }

    public void addFirst(E e) {
        linkedList.addFirst(e);
    }

    @Override
    public boolean remove(Object o) {
        return linkedList.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return linkedList.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        boolean b = false;
        for (E e : c) {
            if (add(e)) {
                b = true;
            }
        }
        return b;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return linkedList.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return linkedList.retainAll(c);
    }

    @Override
    public void clear() {
        linkedList.clear();
    }

    public E get() {
        return linkedList.removeFirst();
    }

    public void setCapacity(int capacity) throws IllegalArgumentException {
        if (capacity < 0)
            throw new IllegalArgumentException("Capacity may not be null");
        this.capacity = capacity;
    }

    public int capacity() {
        return capacity;
    }

    public int available() {
        return capacity - size();
    }
}
