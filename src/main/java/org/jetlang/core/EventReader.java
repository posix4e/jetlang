package org.jetlang.core;

import java.util.Iterator;

public interface EventReader extends MessageReader<Runnable> {
    public Iterator<Runnable> iterator();
}
