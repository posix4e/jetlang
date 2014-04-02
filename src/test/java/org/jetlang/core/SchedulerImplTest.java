package org.jetlang.core;

import org.jetlang.fibers.FiberStub;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class SchedulerImplTest {

    @Test
    public void shouldQueueTaskWithZeroDelayWithoutScheduling() {
        FiberStub stub = new FiberStub();
        SchedulerImpl scheduler = new SchedulerImpl(stub, null);
        final boolean[] executed = new boolean[1];
        Runnable run = () -> {
            executed[0] = true;
        };
        scheduler.schedule(run, 0, TimeUnit.MILLISECONDS);

        assertEquals(0, stub.Scheduled.size());
        assertEquals(1, stub.Pending.size());
        stub.executeAllPending();
        assertTrue(executed[0]);
    }

    @Test
    public void shouldQueueTaskWithZeroDelayWithoutSchedulingThenDontExecuteIfCanceled() {
        FiberStub stub = new FiberStub();
        SchedulerImpl scheduler = new SchedulerImpl(stub, null);
        final boolean[] executed = new boolean[1];
        Runnable run = () -> {
            executed[0] = true;
        };
        Disposable d = scheduler.schedule(run, 0, TimeUnit.MILLISECONDS);

        assertEquals(0, stub.Scheduled.size());
        assertEquals(1, stub.Pending.size());
        d.dispose();
        stub.executeAllPending();
        assertFalse(executed[0]);
    }
}
