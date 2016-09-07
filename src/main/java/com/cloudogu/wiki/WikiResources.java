/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra
 */
public class WikiResources {

    private static final Logger LOG = LoggerFactory.getLogger(WikiResources.class);
    
    private WikiResources() {
    }

    public static String path(String name) {
        return path(WikiResources.class, name);
    }
    
    public static String path(Class contextClass, String name) {
        return contextClass.getPackage().getName().replace('.', '/') + "/" + name;
    }
    
    public static String read(String name){
        return read(WikiResources.class, name);
    }
    
    public static String read(Class contextClass, String name){
        String path = path(contextClass, name);
        LOG.trace("load script {}", path);
        try {
            return Resources.toString(Resources.getResource(path), Charsets.UTF_8);
        } catch (IOException ex){
            throw Throwables.propagate(ex);
        }
    }
    
}
