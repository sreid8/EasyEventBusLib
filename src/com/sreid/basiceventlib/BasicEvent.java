package com.sreid.basiceventlib;

import java.util.EventObject;

/**
 * Must be subclassed for the specific Enum types that are required
 * Created by sean on 10/23/16.
 */
public abstract class BasicEvent extends EventObject {

    private Enum currentValue;

    public BasicEvent(Object source) {
        super(source);
    }

    public BasicEvent(Object source, Enum eventValue) {
        super(source);
        this.currentValue = eventValue;
    }

    public void setCurrentState(Enum eventValue) {
        this.currentValue = eventValue;
    }

    public Enum getCurrentValue() {
        return this.currentValue;
    }

}
