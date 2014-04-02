package org.jetlang.core;

import java.util.ArrayDeque;

import java.util.Iterator;

public class MessageBuffer<T> implements MessageReader<T> {
    private final ArrayDeque<T> events = new ArrayDeque<>();

    public int size() {
        return events.size();
    }

    public void add(T r) {
        events.add(r);
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }

    public void clear() {
        events.clear();
    }

    public Iterator<T> iterator(){
        return events.descendingIterator();
    }
}
