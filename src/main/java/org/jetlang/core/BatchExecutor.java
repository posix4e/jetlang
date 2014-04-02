package org.jetlang.core;

/**
 * Event executor. Implementations execute all Runnables. Typically, custom implementations
 * add logging and exception handling.
 *
 * @author mrettig
 */
public interface BatchExecutor {
    void execute(EventReader toExecute);
}
