package com.cloudogu.wiki;

import com.google.common.collect.ImmutableSet;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;

public final class LocaleChoosingStrategy {

    private static final Set<String> SUPPORTED_LOCALES = ImmutableSet.of("en", "de");

    private LocaleChoosingStrategy() {}

    public static Locale getLocale(Enumeration<Locale> locales) {
        while (locales.hasMoreElements()) {
            Locale locale = locales.nextElement();
            if (isSupported(locale)) {
                return locale;
            }
        }
        return Locale.ENGLISH;
    }

    private static boolean isSupported(Locale locale) {
        return SUPPORTED_LOCALES.contains(locale.getLanguage());
    }

}
