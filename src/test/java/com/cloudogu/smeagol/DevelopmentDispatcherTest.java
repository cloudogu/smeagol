package com.cloudogu.smeagol;

import org.junit.Test;

import static org.junit.Assert.*;

public class DevelopmentDispatcherTest {

    private DevelopmentDispatcher dispatcher = new DevelopmentDispatcher("http://hitchhiker.com");

    @Test
    public void testNeedsToBeDispatched() {
        assertFalse(dispatcher.needsToBeDispatched("/api/v1/authc"));
        assertTrue(dispatcher.needsToBeDispatched("/locales/de/translations.json"));
        assertTrue(dispatcher.needsToBeDispatched("/static/bundle.js"));
        assertTrue(dispatcher.needsToBeDispatched("/favicon.ico"));
        assertTrue(dispatcher.needsToBeDispatched("/index.html"));
        assertTrue(dispatcher.needsToBeDispatched("/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/master/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/feature%20one/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/feature%20one/docs/Home"));
    }

}