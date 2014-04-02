package org.jetlang.fibers;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FiberStubTest {

    final Runnable emptyRunnable = () -> {

    };

    @Test
    public void nonRecurring() {
        FiberStub stub = new FiberStub();
        stub.schedule(emptyRunnable, 1, TimeUnit.SECONDS);
        assertEquals(1, stub.Scheduled.size());
        assertFalse(stub.Scheduled.get(0).isRecurring());
        stub.executeAllScheduled();
        assertEquals(0, stub.Scheduled.size());
    }

    @Test
    public void recurring() {
        FiberStub stub = new FiberStub();
        stub.scheduleAtFixedRate(emptyRunnable, 1, 1, TimeUnit.SECONDS);
        assertEquals(1, stub.Scheduled.size());
        assertTrue(stub.Scheduled.get(0).isRecurring());
        stub.executeAllScheduled();
        assertEquals(1, stub.Scheduled.size());
    }

    @Test
    public void recurring2() {
        FiberStub stub = new FiberStub();
        stub.scheduleWithFixedDelay(emptyRunnable, 1, 1, TimeUnit.SECONDS);
        assertEquals(1, stub.Scheduled.size());
        assertTrue(stub.Scheduled.get(0).isRecurring());
        stub.executeAllScheduled();
        assertEquals(1, stub.Scheduled.size());
    }


}
