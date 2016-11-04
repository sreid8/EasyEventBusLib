package com.sreid.easyeventbuslib.test.event;

import com.sreid.easyeventbuslib.event.Event;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by sean on 11/1/16.
 */
public class EventTest {

    private enum TestEnum{
        TEST
    }

    @Test
    public void testEvent() {
        Event e = new Event(this);
        assertEquals("Source should equal this", this, e.getSource());
        e.setState(TestEnum.TEST);
        assertEquals("State should be TEST", TestEnum.TEST, e.getState());

    }
}
