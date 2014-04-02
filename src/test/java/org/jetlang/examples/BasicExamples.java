package org.jetlang.examples;


import org.jetlang.channels.AsyncRequest;
import org.jetlang.channels.BatchSubscriber;
import org.jetlang.channels.Channel;
import org.jetlang.channels.ChannelSubscription;
import org.jetlang.channels.CompositeChannel;
import org.jetlang.channels.Converter;
import org.jetlang.channels.KeyedBatchSubscriber;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.channels.MemoryRequestChannel;
import org.jetlang.channels.Request;
import org.jetlang.channels.RequestChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.core.Filter;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Some contrived examples that demonstrate the basic publish and
 * subscribe features in jetlang.
 */
public class BasicExamples {

    @Test
    public void basicPubSubWithThreadPool() throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        PoolFiberFactory fact = new PoolFiberFactory(service);
        Fiber fiber = fact.create();
        fiber.start();
        Channel<String> channel = new MemoryChannel<>();

        final CountDownLatch reset = new CountDownLatch(1);
        Callback<String> runnable = msg -> reset.countDown();
        channel.subscribe(fiber, runnable);
        channel.publish("hello");

        Assert.assertTrue(reset.await(5000, TimeUnit.MILLISECONDS));
        fiber.dispose();
        fact.dispose();
        service.shutdown();
    }

    @Test
    public void pubSubWithDedicatedThread() throws InterruptedException {
        Fiber fiber = new ThreadFiber();
        fiber.start();
        Channel<String> channel = new MemoryChannel<>();

        final CountDownLatch reset = new CountDownLatch(1);
        Callback<String> runnable = msg -> reset.countDown();

        channel.subscribe(fiber, runnable);
        channel.publish("hello");

        Assert.assertTrue(reset.await(5000, TimeUnit.MILLISECONDS));
        fiber.dispose();
    }

    @Test
    public void singleEventScheduling() throws InterruptedException {
        Fiber fiber = new ThreadFiber();
        fiber.start();
        final CountDownLatch reset = new CountDownLatch(1);
        Runnable runnable = reset::countDown;
        fiber.schedule(runnable, 1, TimeUnit.MILLISECONDS);
        Assert.assertTrue(reset.await(5000, TimeUnit.MILLISECONDS));
        fiber.dispose();
    }

    @Test
    public void recurringEventScheduling() throws InterruptedException {
        Fiber fiber = new ThreadFiber();
        fiber.start();
        final CountDownLatch reset = new CountDownLatch(5);
        Runnable runnable = reset::countDown;
        fiber.scheduleAtFixedRate(runnable, 1, 2, TimeUnit.MILLISECONDS);
        Assert.assertTrue(reset.await(5000, TimeUnit.MILLISECONDS));
        fiber.dispose();
    }

    @Test
    public void unsubscribe() throws InterruptedException {
        Fiber fiber = new ThreadFiber();
        fiber.start();
        final CountDownLatch reset = new CountDownLatch(1);
        final CountDownLatch two = new CountDownLatch(2);
        Callback<String> runnable = message -> {
            reset.countDown();
            two.countDown();
        };
        Channel<String> channel = new MemoryChannel<>();
        Disposable unsub = channel.subscribe(fiber, runnable);
        channel.publish("one");
        Assert.assertTrue(reset.await(5000, TimeUnit.MILLISECONDS));
        unsub.dispose();
        channel.publish("two");
        Assert.assertFalse(two.await(10, TimeUnit.MILLISECONDS));
        fiber.dispose();
    }

    @Test
    public void pubSubWithDedicatedThreadWithFilter() throws InterruptedException {
        Fiber fiber = new ThreadFiber();
        fiber.start();
        Channel<Integer> channel = new MemoryChannel<>();

        final CountDownLatch reset = new CountDownLatch(1);
        Callback<Integer> onMsg = x -> {
            Assert.assertTrue(x % 2 == 0);
            if (x == 4) {
                reset.countDown();
            }
        };
        Filter<Integer> filter = msg -> msg % 2 == 0;
        ChannelSubscription<Integer> sub = new ChannelSubscription<>(fiber, onMsg, filter);
        channel.subscribe(sub);
        channel.publish(1);
        channel.publish(2);
        channel.publish(3);
        channel.publish(4);

        Assert.assertTrue(reset.await(5000, TimeUnit.MILLISECONDS));
        fiber.dispose();
    }


    @Test
    public void batching() throws InterruptedException {
        Fiber fiber = new ThreadFiber();
        fiber.start();
        MemoryChannel<Integer> counter = new MemoryChannel<>();
        final CountDownLatch reset = new CountDownLatch(1);
        Callback<List<Integer>> cb = new Callback<List<Integer>>() {
            int total = 0;

            public void onMessage(List<Integer> batch) {
                total += batch.size();
                if (total == 10) {
                    reset.countDown();
                }
            }
        };

        BatchSubscriber<Integer> batch = new BatchSubscriber<>(fiber, cb, 0, TimeUnit.MILLISECONDS);
        counter.subscribe(batch);

        for (int i = 0; i < 10; i++) {
            counter.publish(i);
        }

        Assert.assertTrue(reset.await(10000, TimeUnit.MILLISECONDS));
        fiber.dispose();
    }

    @Test
    public void batchingWithKey() throws InterruptedException {
        Fiber fiber = new ThreadFiber();
        fiber.start();
        Channel<Integer> counter = new MemoryChannel<>();
        final CountDownLatch reset = new CountDownLatch(1);
        Callback<Map<String, Integer>> cb = new Callback<Map<String, Integer>>() {
            public void onMessage(Map<String, Integer> batch) {
                if (batch.containsKey("9")) {
                    reset.countDown();
                }
            }
        };

        Converter<Integer, String> keyResolver = Object::toString;
        KeyedBatchSubscriber<String, Integer> batch = new KeyedBatchSubscriber<>(fiber, cb, 0, TimeUnit.MILLISECONDS, keyResolver);
        counter.subscribe(batch);

        for (int i = 0; i < 10; i++) {
            counter.publish(i);
        }

        Assert.assertTrue(reset.await(10000, TimeUnit.MILLISECONDS));
        fiber.dispose();
    }

    @Test
    public void compositeChannel() throws InterruptedException {
        Channel<String> channel = new MemoryChannel<>();
        Channel<String> channel2 = new MemoryChannel<>();
        Channel<String> comp = new CompositeChannel<>(channel, channel2);
        final CountDownLatch latch = new CountDownLatch(2);
        Callback<String> sub = message -> latch.countDown();
        Fiber fiber = new ThreadFiber();
        fiber.start();
        comp.subscribe(fiber, sub);

        comp.publish("msg");
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
        fiber.dispose();

    }


    @Test
    // introduced in 1.7
    public void simpleRequestResponse() throws InterruptedException {
        Fiber req = new ThreadFiber();
        Fiber reply = new ThreadFiber();
        req.start();
        reply.start();
        RequestChannel<String, Integer> channel = new MemoryRequestChannel<>();
        Callback<Request<String, Integer>> onReq = message -> {
            assertEquals("hello", message.getRequest());
            message.reply(1);
        };
        channel.subscribe(reply, onReq);

        final CountDownLatch done = new CountDownLatch(1);
        Callback<Integer> onReply = message -> {
            assertEquals(1, message.intValue());
            done.countDown();
        };
        AsyncRequest.withOneReply(req, channel, "hello", onReply);
        assertTrue(done.await(10, TimeUnit.SECONDS));
        req.dispose();
        reply.dispose();
    }


    @Test
    public void requestReplyWithBlockingQueue() throws InterruptedException {
        Fiber fiber = new ThreadFiber();
        fiber.start();
        BlockingQueue<String> replyQueue = new ArrayBlockingQueue<>(1);
        MemoryChannel<BlockingQueue<String>> channel = new MemoryChannel<>();
        Callback<BlockingQueue<String>> replyCb = message -> {
            try {
                message.put("hello");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        channel.subscribe(fiber, replyCb);
        channel.publish(replyQueue);

        Assert.assertEquals("hello", replyQueue.poll(10, TimeUnit.SECONDS));
        fiber.dispose();
    }

}