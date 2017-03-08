/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Michael Behlendorf
 */
public class SessionListener implements HttpSessionListener {

    private final SessionStore sessions;
    
    public SessionListener(SessionStore sessions){
        this.sessions = sessions;
    }
    
    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        sessions.addSession(hse.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
    }
    
}
