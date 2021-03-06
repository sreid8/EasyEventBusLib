package com.sreid.easyeventbuslib.event;

import java.util.EventObject;

/**
 * A basic Event class that provides the ability to keep track of the source of the event
 * Created by sean on 11/1/16.
 */
public class Event extends EventObject {

    protected Enum state;

    public Event(final Object source) {
        super(source);
    }

    public Event(final Object source, final Enum pState) {
        super(source);
        this.state = pState;
    }

    public void setState(final Enum pState) {
        this.state = pState;
    }

    public Enum getState() {
        return this.state;
    }
}
