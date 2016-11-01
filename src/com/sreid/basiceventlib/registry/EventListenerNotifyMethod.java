package com.sreid.basiceventlib.registry;

import com.sreid.basiceventlib.BasicEvent;

/**
 * Created by sean on 10/29/16.
 */
public enum EventListenerNotifyMethod {
    /** Default. This will call the notify method
     * of each {@link com.sreid.basiceventlib.interfaces.IEventListener Listener} on the same
     * thread that the {@link com.sreid.basiceventlib.registry.EventListenerRegistry#notifyListeners(BasicEvent)}
     * method was called on, blocking the caller from continuing
     */
    BLOCKING,
    /**
     * This will call the notify method
     * of each {@link com.sreid.basiceventlib.interfaces.IEventListener Listener} on a new thread each time.
     * This will NOT black the calling thread from continuing and will ensure that most events are handled in parallel,
     * but the cost of starting a new thread for each event will be quite high for systems with high event traffic
     * or a large number of listeners as the number of active threads can easily get to a high value.
     */
    NEW_THREAD,
    /**
     * A thread pool will be used to handle the events. The size of the Thread Pool can be set
     * with the {@link com.sreid.basiceventlib.registry.EventListenerRegistry#setThreadPoolSize(int)} method, but will
     * default to the number of threads the CPU can execute simultaneously. This is normally equal to the number of
     * cores your CPU has, but on some CPUs (like CPUs with Hyperthreading), it may be more than the number of cores.
     */
    THREAD_POOL,
    /**
     * The same thing as THREAD_POOL, but with a thread pool size of 1, forcing all the events to be called linearly,
     * but without blocking the notifier.
     */
    NON_BLOCKING_LINEAR

}
