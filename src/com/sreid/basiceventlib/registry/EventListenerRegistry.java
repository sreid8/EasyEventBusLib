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

    private EventListenerRegistry INSTANCE = new EventListenerRegistry();
    private Set<IEventListener> registeredListeners = Collections.synchronizedSet(new LinkedHashSet<>());

    private EventListenerNotifyMethod mode = EventListenerNotifyMethod.BLOCKING;
    private Integer threadPoolSize = Runtime.getRuntime().availableProcessors();
    private ExecutorService threadPool = null;

    private List<BasicEvent> basicEventsProcQueue = Collections.synchronizedList(new ArrayList<>());
    private Thread regThread = new Thread();




    private EventListenerRegistry() {
    }

    public boolean registerEventListener(final IEventListener listener) {
        return this.registeredListeners.add(listener);
    }

    public boolean deregisterListener(final IEventListener listener) {
        if (isListenerRegistered(listener)) {
            return this.registeredListeners.remove(listener);
        }
        return true;
    }

    public boolean isListenerRegistered(final IEventListener listener) {
        return this.registeredListeners.contains(listener);
    }

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

    public void notifyOnNewThread(final BasicEvent event) {

    }

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

    public void setThreadPoolSize(final int size) {
        synchronized (this.threadPoolSize) {
            this.threadPoolSize = size;
        }
    }


    private boolean notifyListenersBlocking(final BasicEvent e) {
        boolean handled = true;
        for (IEventListener listener : this.registeredListeners) {
            handled = handled && listener.handleEvent(e);
        }
        return handled;
    }

    private boolean notifyListenersNewThread(final BasicEvent e) {
        for (IEventListener listener : this.registeredListeners) {
            new Thread(new ListenerRunnable(listener, e)).start();
        }
        return true;
    }

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

    private boolean notifyListenersNonBlockingLinear(final BasicEvent e) {
        if (this.regThread == null || !this.regThread.isAlive()) {
            this.regThread = new Thread(new QueueMgmtRunnable(this.basicEventsProcQueue,
                    this.registeredListeners, null));
            this.regThread.start();
        }
        this.basicEventsProcQueue.add(e);
        return true;
    }






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
                        if (threads != null) {
                            threads.execute(new ListenerRunnable(listener, temp));
                        } else {
                            listener.handleEvent(temp);
                        }
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
