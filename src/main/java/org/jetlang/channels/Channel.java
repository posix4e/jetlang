package org.jetlang.channels;

/**
 * Combined subscriber and publisher interface.
 */
public interface Channel<T> extends Subscriber<T>, Publisher<T> {
}
