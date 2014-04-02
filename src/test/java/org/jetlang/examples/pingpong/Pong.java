package org.jetlang.examples.pingpong;

import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;

public class Pong {

    private final PingPongChannels channels;
    private final Fiber consumer;

    public Pong(PingPongChannels channels, Fiber fiber) {
        this.channels = channels;
        this.consumer = fiber;
    }

    public void start() {
        Callback<Integer> onReceive = message -> {
            channels.Pong.publish(message);
            if (message % 1000 == 0)
                System.out.println("message = " + message);
        };
        channels.Ping.subscribe(consumer, onReceive);

        Callback<Void> onStop = message -> consumer.dispose();
        channels.Stop.subscribe(consumer, onStop);
        consumer.start();
    }
}