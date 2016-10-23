package com.sreid.basiceventlib.registry;

import com.sreid.basiceventlib.BasicEvent;
import com.sreid.basiceventlib.interfaces.IEventListener;

import java.util.*;

/**
 * An EventListenerRegistry
 * This class manages the registrations of listeners and sending those registered listeners events
 * Created by sean on 10/23/16.
 */
public final class EventListenerRegistry {

    private EventListenerRegistry INSTANCE = new EventListenerRegistry();
    private Set<IEventListener> registeredListeners = Collections.synchronizedSet(new LinkedHashSet<>());

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
        boolean handled = true;
        for (IEventListener listener : this.registeredListeners) {
            handled = handled && listener.handleEvent(event);
        }
        return handled;
    }

}
