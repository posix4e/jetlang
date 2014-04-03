package org.jetlang;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertTrue;

public class FibersRunInOrderAndSequentiallyTest {

    SortedMap<String, Integer> orderInsurerMap = new ConcurrentSkipListMap<String, Integer>();

    @Before
    public void before() {
        for (int i = 1; i != 9; i++) {
            orderInsurerMap.put(String.valueOf(i), i);
        }
    }

    @Test
    public void testPoolFiberSequentiality() throws InterruptedException {

        ExecutorService service = Executors.newCachedThreadPool();
        PoolFiberFactory fact = new PoolFiberFactory(service);
        Fiber fiber = fact.create();
        fiber.start();
        Channel<String> channel = new MemoryChannel<String>();

        final CountDownLatch reset = new CountDownLatch(1);

        final AtomicInteger count = new AtomicInteger(1);
        Callback<String> runnable = new Callback<String>() {
            public void onMessage(String msg) {
                assertTrue(count.getAndIncrement() == orderInsurerMap.get(msg));
                reset.countDown();
            }
        };
        channel.subscribe(fiber, runnable);

        for (String value : orderInsurerMap.keySet()) {
            channel.publish(value);
        }

        Assert.assertTrue(reset.await(5000, TimeUnit.MILLISECONDS));
        fiber.dispose();
        fact.dispose();
        service.shutdown();
    }

    @Test
    public void testThreadFiberSequentiality() throws InterruptedException {

        Fiber fiber = new ThreadFiber();
        fiber.start();
        Channel<String> channel = new MemoryChannel<String>();

        final CountDownLatch reset = new CountDownLatch(1);

        final AtomicInteger count = new AtomicInteger(1);

        Callback<String> runnable = new Callback<String>() {
            public void onMessage(String msg) {
                assertTrue(count.getAndIncrement() == orderInsurerMap.get(msg));
                reset.countDown();
            }
        };

        channel.subscribe(fiber, runnable);
        for (String value : orderInsurerMap.keySet()) {
            channel.publish(value);
        }

        Assert.assertTrue(reset.await(5000, TimeUnit.MILLISECONDS));
        fiber.dispose();
    }
}
