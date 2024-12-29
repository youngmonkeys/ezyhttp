package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyhttp.server.core.annotation.EzyConfigurationAfterApplicationReady;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class EzyConfigurationAfterApplicationReadyAnnotations {

    private EzyConfigurationAfterApplicationReadyAnnotations() {}

    public static List<Object> sortConfigurationAfterApplicationReadyObjects(
        Collection<Object> objects
    ) {
        List<Object> list = new ArrayList<>(objects);
        list.sort(newConfigurationAfterApplicationReadyComparator());
        return list;
    }

    private static Comparator<Object> newConfigurationAfterApplicationReadyComparator() {
        return Comparator.comparingInt(
            EzyConfigurationAfterApplicationReadyAnnotations::getPriority
        );
    }

    private static int getPriority(Object object) {
        return getPriority(
            object
                .getClass()
                .getAnnotation(EzyConfigurationAfterApplicationReady.class)
        );
    }

    private static int getPriority(
        EzyConfigurationAfterApplicationReady annotation
    ) {
        int priority = 0;
        if (annotation != null) {
            priority = annotation.priority();
        }
        return priority;
    }
}
