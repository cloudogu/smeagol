/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki;

import com.cloudogu.wiki.scmm.NotifyServlet;
import com.cloudogu.wiki.scmm.ScmWikiProvider;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jgit.api.errors.GitAPIException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link NotifyServlet}.
 * 
 * @author Michael Behlendorf
 */
@RunWith(MockitoJUnitRunner.class)
public class NotifyServletTest {
    
    @Mock
    private SessionStore sessionStore;
    
    @Mock
    private ScmWikiProvider wikiProvider;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private HttpSession session2;
    
    
    NotifyServlet servlet;
    
    
    private final static String REPOSITORY = "TestRepo";
    private final static String WRONG_REPOSITORY = "Wrong";
    private final static String BRANCH = "TestBranch";
    private final static String BRANCH2 = "TestBranch2";
    private final static String ACCOUNT = "com.cloudogu.wiki.Account";
    private Account account;
            
    @Before
    public void setUpMocks(){
        account = new Account("name", "displayName", "mail");
        Set<HttpSession> sessions = new HashSet<>();
        sessions.add(session);
        sessions.add(session2);
        
        Set<String> attributes = ImmutableSet.of(ACCOUNT, "servlet."+REPOSITORY+"/"+BRANCH+"_de");
        Enumeration<String> attributesEnum = java.util.Collections.enumeration(attributes);
        when(session.getAttributeNames()).thenReturn(attributesEnum);
        when(session.getAttribute(ACCOUNT)).thenReturn(account);
        
        servlet = new NotifyServlet(sessionStore, wikiProvider);

        when(sessionStore.getAll()).thenReturn(sessions.iterator());
        
    }
    
    /*
    * tests with one servlet entry
    */
    @Test
    public void testNotifyGet() throws IOException, ServletException, GitAPIException {
        when(request.getParameter("id")).thenReturn(REPOSITORY);
        when(request.getMethod()).thenReturn("GET");
        
        File directory = new File("home",(REPOSITORY+"/"+BRANCH));
        when(wikiProvider.getRepositoryDirectory(REPOSITORY+"/"+BRANCH)).thenReturn(directory);

        servlet.service(request, response);

        verify(wikiProvider).pullChanges(account, directory, BRANCH);
    }
    
    /*
    * tests with two sessions. The expected behaviour is that pullChanges will be called
    * only once since both sessions link to the same wiki.
    */
    @Test
    public void testNotifyGetSameDirectory() throws IOException, ServletException, GitAPIException {

        when(request.getParameter("id")).thenReturn(REPOSITORY);
        when(request.getMethod()).thenReturn("GET");
        
        Set<String> attributes = ImmutableSet.of(ACCOUNT, "servlet."+REPOSITORY+"/"+BRANCH);
        Enumeration<String> attributesEnum = java.util.Collections.enumeration(attributes);
        when(session2.getAttributeNames()).thenReturn(attributesEnum);
        when(session2.getAttribute(ACCOUNT)).thenReturn(account);
        
        File directory = new File("home",(REPOSITORY+"/"+BRANCH));
        when(wikiProvider.getRepositoryDirectory(REPOSITORY+"/"+BRANCH)).thenReturn(directory);

        servlet.service(request, response);
        
        
        verify(wikiProvider).pullChanges(account, directory, BRANCH);
    }
    
    /*
    * tests with two sessions. The expected behaviour is that pullChanges will be called
    * twice since we got two different branches for the called REPOSITORY.
    */
    @Test
    public void testNotifyGetDifferentBranches() throws IOException, ServletException, GitAPIException {
        when(request.getParameter("id")).thenReturn(REPOSITORY);
        when(request.getMethod()).thenReturn("GET");
        
        Set<String> attributes = ImmutableSet.of(ACCOUNT, "servlet."+REPOSITORY+"/"+BRANCH2);
        Enumeration<String> attributesEnum = java.util.Collections.enumeration(attributes);
        when(session2.getAttributeNames()).thenReturn(attributesEnum);
        when(session2.getAttribute(ACCOUNT)).thenReturn(account);
        
        File directory = new File("home",(REPOSITORY+"/"+BRANCH));
        when(wikiProvider.getRepositoryDirectory(REPOSITORY+"/"+BRANCH)).thenReturn(directory);

        servlet.service(request, response);
        
        
        verify(wikiProvider, times(2)).pullChanges(any(Account.class), any(File.class), anyString());
    }
    
    /*
    * tests with the wrong repository
    */
    @Test
    public void testNotifyGetWrongRepository() throws IOException, ServletException, GitAPIException {
        when(request.getParameter("id")).thenReturn(WRONG_REPOSITORY);
        when(request.getMethod()).thenReturn("GET");
        
        File directory = new File("home",(REPOSITORY+"/"+BRANCH));
        when(wikiProvider.getRepositoryDirectory(REPOSITORY+"/"+BRANCH)).thenReturn(directory);

        servlet.service(request, response);
        
        
        verify(wikiProvider, times(0)).pullChanges(any(Account.class), any(File.class), anyString());
    }
    
}
