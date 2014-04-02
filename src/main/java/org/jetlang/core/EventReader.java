package org.jetlang.core;

public interface EventReader extends MessageReader<Runnable> {
    Runnable get(int index);
}
