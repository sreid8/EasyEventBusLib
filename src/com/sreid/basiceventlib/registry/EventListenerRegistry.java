package com.sreid.basiceventlib.registry;

import com.sreid.basiceventlib.BasicEvent;
import com.sreid.basiceventlib.interfaces.IEventListener;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An EventListenerRegistry
 * This class manages the registrations of listeners and sending those registered listeners events
 * Created by sean on 10/23/16.
 */
public final class EventListenerRegistry {

    /** this static instance */
    private static EventListenerRegistry INSTANCE = new EventListenerRegistry();

    /** the set of registered listeners */
    private Set<IEventListener> registeredListeners = Collections.synchronizedSet(new LinkedHashSet<>());

    /** the method that the listeners are notified by. default to BLOCKING */
    private EventListenerNotifyMethod mode = EventListenerNotifyMethod.BLOCKING;
    /** the size of the thread pool, if used */
    private Integer threadPoolSize = Runtime.getRuntime().availableProcessors();
    /** the thread pool */
    private ExecutorService threadPool = null;

    /** the queue for the events as they come in */
    private List<BasicEvent> basicEventsProcQueue = Collections.synchronizedList(new ArrayList<>());
    /** the thread owned by this registry. either for processing the queue or for non-blocking linear event notifying */
    private Thread regThread = new Thread();


    /**
     * gets the instance of the listener
     * @return -> the {@link com.sreid.basiceventlib.registry.EventListenerRegistry instance}
     */
    public static EventListenerRegistry getDefault() {
        return INSTANCE;
    }

    /**
     * private ctor. might want to make package private for a factory impl
     */
    private EventListenerRegistry() {
    }

    /**
     * registers a {@link com.sreid.basiceventlib.interfaces.IEventListener listener} with the registry
     * @param listener -> a {@link com.sreid.basiceventlib.interfaces.IEventListener listener} that
     *                 will be registered for event notifications
     * @return -> true if it was registered, false otherwise
     */
    public boolean registerEventListener(final IEventListener listener) {
        return this.registeredListeners.add(listener);
    }

    /**
     * removes a {@link com.sreid.basiceventlib.interfaces.IEventListener listener} from this registry
     * @param listener -> the {@link com.sreid.basiceventlib.interfaces.IEventListener listener} that should be removed
     * @return -> true if listener was removed or never in the list, false if error
     */
    public boolean deregisterListener(final IEventListener listener) {
        if (isListenerRegistered(listener)) {
            return this.registeredListeners.remove(listener);
        }
        return true;
    }

    /**
     * checks to see if the {@link com.sreid.basiceventlib.interfaces.IEventListener listener} is registered
     * @param listener -> {@link com.sreid.basiceventlib.interfaces.IEventListener listener} to query
     * @return -> true if registered, false if not
     */
    public boolean isListenerRegistered(final IEventListener listener) {
        return this.registeredListeners.contains(listener);
    }

    /**
     * notifies the registered {@link com.sreid.basiceventlib.interfaces.IEventListener listeners}
     * via the method the registry is configured to use
     * @see com.sreid.basiceventlib.registry.EventListenerNotifyMethod
     * @param event -> the {@link com.sreid.basiceventlib.BasicEvent event}
     * @return -> true if the method was executed, false otherwise. true does not guarantee that the
     *              {@link com.sreid.basiceventlib.interfaces.IEventListener#handleEvent(BasicEvent)} method has
     *              finished or provide any information about its current status
     */
    public boolean notifyListeners(final BasicEvent event) {
        boolean handled = false;
        switch (this.mode) {
            case BLOCKING:
                handled = notifyListenersBlocking(event);
                break;
            case NEW_THREAD:
                handled = notifyListenersNewThread(event);
                break;
            case THREAD_POOL:
                handled = notifyListenersThreadPool(event);
                break;
            case NON_BLOCKING_LINEAR:
                handled = notifyListenersNonBlockingLinear(event);
                break;
            default:
                break;
        }
        return handled;
    }

    /**
     * sets the {@link com.sreid.basiceventlib.registry.EventListenerNotifyMethod method} that this registry will
     * use to notify the {@link com.sreid.basiceventlib.interfaces.IEventListener listeners}. if the method is
     * {@link com.sreid.basiceventlib.registry.EventListenerNotifyMethod#THREAD_POOL THREAD_POOL}, the thread pool is
     * created with the default size (num supported CPU threads) or the size set via
     * {@link EventListenerRegistry#setThreadPoolSize(int)}
     * @param method -> the {@link com.sreid.basiceventlib.registry.EventListenerNotifyMethod method} to use
     */
    public void setNotifyMethod(EventListenerNotifyMethod method) {
        //need to save the registered listeners and give them to the new instance...
        //maybe I don't need a brand new Registry for this. Idk.
        synchronized (this) {
            mode = method;
            if (method == EventListenerNotifyMethod.THREAD_POOL) {
                threadPool = Executors.newFixedThreadPool(threadPoolSize);
            }
        }
    }

    /**
     * sets the size of the thread pool the {@link com.sreid.basiceventlib.registry.EventListenerNotifyMethod#THREAD_POOL}
     * notify methos
     * @param size -> an integer to define the size of the thread pool
     */
    public void setThreadPoolSize(final int size) {
        synchronized (this.threadPoolSize) {
            this.threadPoolSize = size;
        }
    }

    /**
     * notifies the listeners via the BLOCKING method
     * @param e -> the BasicEvent
     * @return -> true if the events all were handled
     */
    private boolean notifyListenersBlocking(final BasicEvent e) {
        boolean handled = true;
        for (IEventListener listener : this.registeredListeners) {
            handled = handled && listener.handleEvent(e);
        }
        return handled;
    }

    /**
     * notifies the listeners via the NEW_THREAD method
     * @param e -> the BasicEvent
     * @return -> true in all cases
     */
    private boolean notifyListenersNewThread(final BasicEvent e) {
        for (IEventListener listener : this.registeredListeners) {
            new Thread(new ListenerRunnable(listener, e)).start();
        }
        return true;
    }

    /**
     * notifies the listeners via the THREAD_POOL method
     * @param e -> the BasicEvent
     * @return -> true in all cases
     */
    private boolean notifyListenersThreadPool(final BasicEvent e) {
        if (this.regThread == null || !this.regThread.isAlive()) {
            this.regThread = new Thread(new QueueMgmtRunnable(this.basicEventsProcQueue,
                    this.registeredListeners,
                    this.threadPool));
            this.regThread.start();
        }
        this.basicEventsProcQueue.add(e);
        return true;
    }

    /**
     * notifies the listeners via the NON_BLOCKING_LINEAR method
     * @param e -> the BasicEvent
     * @return -> true in all cases
     */
    private boolean notifyListenersNonBlockingLinear(final BasicEvent e) {
        if (this.regThread == null || !this.regThread.isAlive()) {
            this.regThread = new Thread(new QueueMgmtRunnable(this.basicEventsProcQueue,
                    this.registeredListeners, Executors.newFixedThreadPool(1)));
            this.regThread.start();
        }
        this.basicEventsProcQueue.add(e);
        return true;
    }

    /**
     * a private class to handle notifying a listener on a thread separate from the thread that is sending the Event
     */
    private class ListenerRunnable implements Runnable {
        private IEventListener listener = null;
        private BasicEvent event = null;

        public ListenerRunnable(IEventListener pListener, BasicEvent e) {
            this.listener = pListener;
            this.event = e;
        }

        @Override
        public void run() {
            this.listener.handleEvent(event);
        }
    }

    /**
     * a private class to handle the management of the event queue and execute the listener's handler
     */
    private class QueueMgmtRunnable implements Runnable {
        private List<BasicEvent> eventQueue = null;
        private Set<IEventListener> listeners = null;
        private ExecutorService threads = null;


        public  QueueMgmtRunnable(List<BasicEvent> queue, Set<IEventListener> listen, ExecutorService pool) {
            this.eventQueue = queue;
            this.listeners = listen;
            this.threads = pool;
        }

        @Override
        public void run() {
            while (true) {
                while (eventQueue.size() > 0) {
                    BasicEvent temp = eventQueue.get(0);
                    eventQueue.remove(0);
                    for (IEventListener listener : listeners) {
                        threads.execute(new ListenerRunnable(listener, temp));
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
