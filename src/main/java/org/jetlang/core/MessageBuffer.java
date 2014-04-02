package org.jetlang.core;

import java.util.ArrayList;

public class MessageBuffer<T> implements MessageReader<T> {
    private final ArrayList<T> events = new ArrayList<>();

    public int size() {
        return events.size();
    }

    public T get(int index) {
        return events.get(index);
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

}
