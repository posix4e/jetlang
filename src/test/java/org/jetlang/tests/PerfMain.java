package org.jetlang.tests;

import org.jetlang.channels.ChannelTest;

public class PerfMain {

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(30000);
        ChannelTest tests = new ChannelTest();
        tests.pointToPointPerfTestWithPool();
        tests.pointToPointPerfTestWithThread();
    }
}
