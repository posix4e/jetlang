package org.jetlang.core;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.junit.Assert;
import org.junit.Test;

public class SynchronousDisposingExecutorTest {

    @Test
    public void dispose() {
        SynchronousDisposingExecutor exec = new SynchronousDisposingExecutor();
        Channel<String> channel = new MemoryChannel<>();
        channel.subscribe(exec, Assert::fail);
        exec.dispose();
        channel.publish("shouldn't be received");
    }
}
