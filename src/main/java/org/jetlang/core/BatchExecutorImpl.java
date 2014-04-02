package org.jetlang.core;

import java.util.Iterator;

/**
 * Default implementation that simply executes all events.
 *
 * @author mrettig
 */
public class BatchExecutorImpl implements BatchExecutor {
    public void execute(EventReader toExecute) {
        Iterator<Runnable> iterator = toExecute.iterator();
        while(iterator.hasNext()){
            iterator.next().run();
        }
    }
}
