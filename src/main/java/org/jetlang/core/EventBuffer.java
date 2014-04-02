package org.jetlang.core;

public class EventBuffer extends MessageBuffer<Runnable> implements EventReader {
    public void add(Runnable runnable) {
        super.add(runnable);
    }
}
