package org.jetlang.tests;

import org.jetlang.core.Disposable;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ThreadFiberTest extends FiberBaseTest {

    @Override
    public Fiber createFiber() {
        return new ThreadFiber(new RunnableExecutorImpl(), System.currentTimeMillis() + "", true);
    }

    @Override
    public void doSetup() {
    }

    @Override
    public void doTearDown() {
    }

    @Test
    public void ScheduleIntervalWithCancel() throws InterruptedException {
        _bus.start();
        Runnable onReset = () -> {
        };
        Disposable stopper = _bus.scheduleAtFixedRate(onReset, 15, 15, TimeUnit.MILLISECONDS);
        assertEquals(0, _bus.size());
        stopper.dispose();
        assertEquals(0, _bus.size());
    }

}
