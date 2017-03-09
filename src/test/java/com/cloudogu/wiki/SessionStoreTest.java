/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki;

import com.sun.management.GarbageCollectionNotificationInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.servlet.http.HttpSession;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link SessionStore}.
 *
 * @author Michael Behlendorf
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionStoreTest {

    @Mock
    public HttpSession session1;

    @Mock
    public HttpSession session2;

    public SessionStore sessions;

    @Before
    public void setUpMocks() {
        sessions = new SessionStore();
    }

    /**
     * Tests {@link SessionStore#addSession(HttpSession)}.
     */
    @Test
    public void testAddSession() {
        assertEquals(0, sessions.getSize());
        sessions.addSession(session1);
        assertEquals(1, sessions.getSize());
        sessions.addSession(session2);
        assertEquals(2, sessions.getSize());
    }

    /**
     * Tests weakness of SessionSotre with
     * {@link SessionStore#addSession(HttpSession)} and GC
     */
    @Test
    public void testWeaknessOfSessionStore() throws InterruptedException {
        
        Object gcLock = new Object();
        NotificationListener notificationListener = new NotificationListenerImpl(gcLock);

        // register our listener with all gc beans
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) gcBean;
            emitter.addNotificationListener(notificationListener, null, null);
        }

        assertEquals(0, sessions.getSize());
        sessions.addSession(session1);
        assertEquals(1, sessions.getSize());
        session1 = null;
        
        synchronized (gcLock){
            System.gc();
            // wait for notify from garbage collector through NotificationListener
            gcLock.wait(5000);
        }
        assertEquals(0, sessions.getSize());
    }

    private class NotificationListenerImpl implements NotificationListener {
        
        Object gcLock;
        
        private NotificationListenerImpl(Object gcLock) {
            this.gcLock = gcLock;
        }

        @Override
        public void handleNotification(Notification notification, Object handback) {
            if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                synchronized (gcLock) {
                    gcLock.notify();
                }
            }
        }
    }

}
