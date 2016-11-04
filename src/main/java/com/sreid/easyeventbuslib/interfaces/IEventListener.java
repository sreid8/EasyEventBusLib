package com.sreid.easyeventbuslib.interfaces;


import com.sreid.easyeventbuslib.event.Event;

/**
 * Classes implement this interface to subscribe to events
 * Created by sean on 10/23/16.
 */
public interface IEventListener {

    /**
     * Responds as wanted to the event.
     * This method is blocking, so it should attempt to return as quickly as possible
     * and long-running responses should be on their own thread
     * @param event -> the event
     * @return -> a boolean to describe whether the event was adequately handled
     */
    boolean handleEvent(Event event);

}
