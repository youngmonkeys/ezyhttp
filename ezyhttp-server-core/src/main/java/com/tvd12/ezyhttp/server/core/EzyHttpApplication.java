package com.tvd12.ezyhttp.server.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyStartable;
import com.tvd12.ezyfox.util.EzyStoppable;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.util.BannerPrinter;

import lombok.Getter;

public class EzyHttpApplication
        extends EzyLoggable
        implements EzyStartable, EzyStoppable {

    @Getter
    protected final ApplicationContext applicationContext;

    public EzyHttpApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static EzyHttpApplication start(Class<?> bootstrapClass) throws Exception {
        String basePackage = bootstrapClass.getPackage().getName();
        return start(basePackage, bootstrapClass);
    }

    public static EzyHttpApplication start(
            Class<?> bootstrapClass, Class<?>... componentClasses) throws Exception {
        String basePackage = bootstrapClass.getPackage().getName();
        Set<Class<?>> classSet = new HashSet<>();
        classSet.add(bootstrapClass);
        classSet.addAll(Arrays.asList(componentClasses));
        Class<?>[] classArray = classSet.toArray(new Class[0]);
        return start(basePackage, classArray);
    }

    public static EzyHttpApplication start(
            String basePackage,
            Class<?>... componentClasses) throws Exception {
        ApplicationContext applicationContext
                = createApplicationContext(basePackage, componentClasses);
        EzyHttpApplication application = new EzyHttpApplication(applicationContext);
        application.start();
        return application;
    }

    @Override
    public void start() throws Exception {
        ApplicationEntry entry = applicationContext
                .getAnnotatedSingleton(ApplicationBootstrap.class);
        if (entry == null) {
            throw new IllegalStateException("Failed to start application, " +
                    "the ApplicationEntry not found, " +
                    "let's use EzyHttpApplicationBootstrap.start(...)");
        }
        entry.init();
        entry.start();
        boolean printBanner = applicationContext.getProperty("banner.printable", boolean.class,
                true);
        if (printBanner) {
            logger.info("\n{}\n", new BannerPrinter().getBannerText());
        }
    }

    protected static ApplicationContext
        createApplicationContext(String basePackage, Class<?>... componentClasses) {
        return new ApplicationContextBuilder()
                .scan(basePackage)
                .addComponentClasses(componentClasses)
                .build();
    }

    @Override
    public void stop() {
        applicationContext.destroy();
    }

}
