package org.jetlang.core;

import java.util.Iterator;

public interface MessageReader<T> {

    public Iterator<T> iterator();
    public int size();

}
