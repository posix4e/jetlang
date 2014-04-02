package org.jetlang;

import org.jetlang.core.SynchronousDisposingExecutor;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SynchronousRunnableQueueTest {

    @Test
    public void execution() {
        SynchronousDisposingExecutor queue = new SynchronousDisposingExecutor();
        final boolean[] executed = new boolean[1];
        Runnable run = () -> {
            executed[0] = true;
        };
        queue.execute(run);
        assertTrue(executed[0]);
    }
}
