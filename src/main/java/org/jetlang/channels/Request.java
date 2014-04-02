package org.jetlang.channels;

public interface Request<R, V> {
    Session getSession();

    R getRequest();

    void reply(V i);

}
