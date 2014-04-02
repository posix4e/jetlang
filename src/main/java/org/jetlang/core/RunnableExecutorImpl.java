package org.jetlang.core;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Default implementation that queues and executes events. A dedicated thread is typically
 * used to consume events.
 */
public class RunnableExecutorImpl implements RunnableExecutor {

    private final EventQueue _commands;
    private final ArrayDeque<Disposable> _disposables = new ArrayDeque<>();

    private final BatchExecutor _commandExecutor;

    public RunnableExecutorImpl() {
        this(new BatchExecutorImpl());
    }

    public RunnableExecutorImpl(BatchExecutor executor) {
        this(executor, new RunnableBlockingQueue());
    }

    public RunnableExecutorImpl(BatchExecutor exec, EventQueue q) {
        this._commands = q;
        this._commandExecutor = exec;
    }

    public void execute(Runnable command) {
        _commands.put(command);
    }

    public void run() {
        EventBuffer buffer = new EventBuffer();
        while (_commands.isRunning()) {
            buffer = _commands.swap(buffer);
            _commandExecutor.execute(buffer);
            buffer.clear();
        }
    }

    public void dispose() {
        _commands.setRunning(false);
        execute(() -> {
            // so it wakes up and will notice that we've told it to stop
        });
        synchronized (_disposables) {
            Iterator<Disposable> iterator = _disposables.descendingIterator();
            while (iterator.hasNext()) {
                iterator.next().dispose();
            }
        }
    }

    public void add(Disposable r) {
        _disposables.add(r);
    }

    public boolean remove(Disposable disposable) {
        return _disposables.remove(disposable);
    }

    public int size() {
        return _disposables.size();
    }
}
