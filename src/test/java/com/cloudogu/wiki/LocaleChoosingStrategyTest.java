package com.cloudogu.wiki;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleChoosingStrategyTest {

    @Test
    public void getLocale() {
        assertEquals(Locale.ENGLISH, choose(Locale.ENGLISH));
        assertEquals(Locale.GERMAN, choose(Locale.GERMAN));
        assertEquals(Locale.GERMANY, choose(Locale.GERMANY));
        assertEquals(Locale.ENGLISH, choose(Locale.ITALIAN));
        assertEquals(Locale.GERMAN, choose(Locale.ITALIAN, Locale.GERMAN));
    }

    private Locale choose(Locale ...locales) {
        return LocaleChoosingStrategy.getLocale(createLocalesEnumeration(locales));
    }

    private Enumeration<Locale> createLocalesEnumeration(Locale ...locales) {
        List<Locale> localeList = Lists.newArrayList(locales);
        return Collections.enumeration(localeList);
    }

}