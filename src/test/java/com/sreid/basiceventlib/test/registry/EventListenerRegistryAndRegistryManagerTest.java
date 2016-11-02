package com.sreid.basiceventlib.test.registry;

import com.sreid.basiceventlib.event.Event;
import com.sreid.basiceventlib.interfaces.IEventListener;
import com.sreid.basiceventlib.registry.EventListenerNotifyMethod;
import com.sreid.basiceventlib.registry.EventListenerRegistry;
import com.sreid.basiceventlib.registry.EventListenerRegistryManager;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by sean on 11/1/16.
 */
public class EventListenerRegistryAndRegistryManagerTest {

    @Test
    public void testEventListenerRegistryAndRegistryManager() {
        EventListenerRegistryManager mgr = EventListenerRegistryManager.getManager();
        assertNotEquals("Manager should not be null", null, mgr);

        IEventListener listener = new IEventListener() {
            @Override
            public boolean handleEvent(Event event) {
                if (event.getState() == TestEnum.TEST) {
                    return true;
                }
                return false;
            }
        };



        EventListenerRegistry reg = mgr.createRegistry(this.getClass().getCanonicalName());
        assertNotEquals("Created registry should not be null", null, reg);
        reg.setNotifyMethod(EventListenerNotifyMethod.BLOCKING);

        assertEquals("Reg ID should be this test class name", this.getClass().getCanonicalName(), reg.getId());

        testRegistry(reg, listener);

        //create new registry for NEW_THREAD
        reg = mgr.createRegistry(this.getClass().getCanonicalName());
        assertNotEquals("Created registry should not be null", null, reg);
        reg.setNotifyMethod(EventListenerNotifyMethod.NEW_THREAD);

        testRegistry(reg, listener);

        //create new registry for THREAD_POOL
        reg = mgr.createRegistry(this.getClass().getCanonicalName());
        assertNotEquals("Created registry should not be null", null, reg);
        reg.setNotifyMethod(EventListenerNotifyMethod.THREAD_POOL);

        testRegistry(reg, listener);

        reg = mgr.createRegistry(this.getClass().getCanonicalName());
        assertNotEquals("Created registry should not be null", null, reg);
        reg.setNotifyMethod(EventListenerNotifyMethod.NON_BLOCKING_LINEAR);

        testRegistry(reg, listener);
    }

    public void testRegistry(EventListenerRegistry reg, IEventListener listener) {
        //Test the BLOCKING notify method
        assertEquals("Register should return TRUE", true, reg.registerEventListener(listener));

        assertEquals("Notify with TEST enum type should return TRUE",
                true,
                reg.notifyListeners(new Event(this, TestEnum.TEST)));

        assertEquals("isListenerRegistered should return true", true, reg.isListenerRegistered(listener));


        assertEquals("deregisterListener should return true", true, reg.deregisterListener(listener));
    }

}
