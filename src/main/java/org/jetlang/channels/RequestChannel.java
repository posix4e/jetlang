package org.jetlang.channels;

import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.core.DisposingExecutor;

public interface RequestChannel<R, V> {

    Disposable subscribe(DisposingExecutor fiber, Callback<Request<R, V>> onRequest);

    Disposable publish(DisposingExecutor fiber, R request, Callback<V> reply);

    Disposable subscribe(DisposingExecutor fiber, Callback<Request<R, V>> onRequest,
                         Callback<SessionClosed<R>> onRequestEnd);
}
