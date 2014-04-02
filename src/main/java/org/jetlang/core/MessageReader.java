package org.jetlang.core;

public interface MessageReader<T> {

    int size();

    T get(int index);

}
