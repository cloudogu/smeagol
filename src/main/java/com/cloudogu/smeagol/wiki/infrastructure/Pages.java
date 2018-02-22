package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Path;

import java.io.File;

public class Pages {

    private static final String EXTENSION = ".md";

    private Pages() {
    }

    public static String filepath(Path path) {
        return path.getValue().concat(EXTENSION);
    }

    public static boolean isPage(File file) {
        return file.isFile() && isPageFilename(file.getName());
    }

    private static boolean isPageFilename(String filename) {
        return filename.endsWith(EXTENSION);
    }

    public static Path path(Path parent, File file) {
        return parent.childFile(name(file));
    }

    private static String name(File file) {
        return name(file.getName());
    }

    private static String name(String filename) {
        return filename.substring(0, filename.length() - EXTENSION.length());
    }

}
