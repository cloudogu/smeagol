/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Michael Behlendorf
 */
public class SessionStore {
    Set<HttpSession> sessionStore;
    
    public SessionStore(){
        sessionStore = Collections.newSetFromMap(new WeakHashMap());
    }
    
    public boolean addSession(HttpSession session){
        return sessionStore.add(session);
    }
    
    public Iterator<HttpSession> getAll(){
        return sessionStore.iterator();
    }
}
