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
 * Util methods to handle resources.
 * 
 * @author Sebastian Sdorra
 */
public class WikiResources {

    private static final Logger LOG = LoggerFactory.getLogger(WikiResources.class);
    
    private WikiResources() {
    }

    /**
     * Returns path to resource. The path is calculated relative to the {@link WikiResources} class.
     * 
     * @param name name of resource
     * 
     * @return resource path
     */
    public static String path(String name) {
        return path(WikiResources.class, name);
    }

    /**
     * Returns path to resource. The path is calculated relative to the given context class.
     * 
     * @param contextClass context class
     * @param name name of resource
     * 
     * @return resource path
     */
    public static String path(Class contextClass, String name) {
        return contextClass.getPackage().getName().replace('.', '/') + "/" + name;
    }
    
    /**
     * Calculates the path path for the resource and reads the resource. {@link WikiResources} class is used as context
     * class.
     * 
     * @param name name of resource
     * 
     * @return content of resource
     */
    public static String read(String name){
        return read(WikiResources.class, name);
    }

    /**
     * Calculates the path path for the resource and reads the resource.
     * 
     * @param contextClass context class
     * @param name name of resource
     * 
     * @return content of resource
     */    
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
