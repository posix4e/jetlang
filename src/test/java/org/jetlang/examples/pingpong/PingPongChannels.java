package org.jetlang.examples.pingpong;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;

public class PingPongChannels {

    public final Channel<Integer> Ping = new MemoryChannel<>();
    public final Channel<Integer> Pong = new MemoryChannel<>();
    public final Channel<Void> Stop = new MemoryChannel<>();
}
