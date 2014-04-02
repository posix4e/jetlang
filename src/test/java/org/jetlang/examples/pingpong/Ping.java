package org.jetlang.examples.pingpong;

import org.jetlang.channels.BatchSubscriber;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class Ping {

    private final PingPongChannels channels;
    private final Fiber consumer;
    private int total;

    public Ping(PingPongChannels channels, Fiber fiber, int total) {
        this.channels = channels;
        this.consumer = fiber;
        this.total = total;
    }

    public void start() {
        Callback<Integer> onReceive = message -> {
            if (total > 0) {
                publishPing();
            } else {
                channels.Stop.publish(null);
                consumer.dispose();
            }
        };
        channels.Pong.subscribe(consumer, onReceive);
        consumer.start();

        //send first ping from ping fiber. The first ping could have been published from the main
        // thread as well, but in this case we'll use the ping fiber to be consistent.
        Runnable firstPing = this::publishPing;
        consumer.execute(firstPing);

        Callback<List<Integer>> onBatch = message -> {
            //consume all messages.
        };
        BatchSubscriber<Integer> sub = new BatchSubscriber<>(consumer, onBatch, 0, TimeUnit.MILLISECONDS);
        channels.Ping.subscribe(consumer, sub);

    }

    private void publishPing() {
        total--;
        channels.Ping.publish(total);
    }
}
