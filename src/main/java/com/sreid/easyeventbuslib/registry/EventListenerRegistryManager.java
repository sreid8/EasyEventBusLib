package com.sreid.easyeventbuslib.registry;

import java.util.concurrent.ConcurrentHashMap;

/**
 * creates new instances of {@link com.sreid.easyeventbuslib.registry.EventListenerRegistry} as needed
 * and allows querying to use the same one based on a String key
 * Created by sean on 10/31/16.
 */
public class EventListenerRegistryManager {
    /** the private instance */
    private static EventListenerRegistryManager INSTANCE = new EventListenerRegistryManager();

    private ConcurrentHashMap<String, EventListenerRegistry> registries = new ConcurrentHashMap<>();

    /**
     * private ctor since there should only ever be one
     */
    private EventListenerRegistryManager() {

    }

    /**
     * gets the manager
     * @return -> the instance of the manager
     */
    public static EventListenerRegistryManager getManager() {
        return INSTANCE;
    }

    /**
     * creates a new {@link com.sreid.easyeventbuslib.registry.EventListenerRegistry registry}
     * with the ID specified. should be a unique id becasue if it is a duplicate, the original will be overwitten
     * this this new one
     * @param id -> the ID to name the new registry
     * @return -> the {@link com.sreid.easyeventbuslib.registry.EventListenerRegistry registry}
     */
    public EventListenerRegistry createRegistry(final String id) {
        EventListenerRegistry reg = new EventListenerRegistry(id);
        registries.put(id, reg);
        return registries.get(id);
    }

    /**
     * returns the {@link com.sreid.easyeventbuslib.registry.EventListenerRegistry registry} for the specified ID if
     * one exists
     * @param id -> the unique ID for the registry
     * @return -> the {@link com.sreid.easyeventbuslib.registry.EventListenerRegistry}
     */
    public EventListenerRegistry getRegistry(final String id) {
        return (this.registries.get(id) != null ? this.registries.get(id) : this.createRegistry(id));
    }

    /**
     * returns true if the id specified already has a registry associated with it, false if it does not
     * @param id -> the id to query
     * @return -> true if the id has a registry associated with it, false otherwise
     */
    public boolean idExists(final String id) {
        for (String key : this.registries.keySet()) {
            if (key.equals(id)) {
                return true;
            }
        }
        return false;
    }

}
