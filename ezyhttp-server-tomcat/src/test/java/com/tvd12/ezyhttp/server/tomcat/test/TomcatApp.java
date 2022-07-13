package com.tvd12.ezyhttp.server.tomcat.test;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.util.EzyDirectories;
import com.tvd12.ezyhttp.server.core.ApplicationContext;
import com.tvd12.ezyhttp.server.core.ApplicationContextBuilder;
import com.tvd12.ezyhttp.server.core.EzyHttpApplication;
import com.tvd12.ezyhttp.server.core.annotation.ComponentClasses;
import com.tvd12.ezyhttp.server.tomcat.TomcatApplicationBootstrap;
import org.testng.annotations.Test;

import java.io.File;

@ComponentClasses(TomcatApplicationBootstrap.class)
public class TomcatApp {
    
    public static void main(String[] args) throws Exception {
        EzyHttpApplication.start(TomcatApp.class);
    }
    
    @Test
    public void testEnable() throws Exception {
        // given
        System.setProperty(EzyBeanContext.ACTIVE_PROFILES_KEY, "enable");
        
        // when
        EzyHttpApplication app = EzyHttpApplication.start(TomcatApp.class);
        
        // then
        app.stop();
    }
    
    @Test
    public void testDisable() throws Exception {
        // given
        System.setProperty(EzyBeanContext.ACTIVE_PROFILES_KEY, "disable");
        
        // when
        EzyHttpApplication app = EzyHttpApplication.start(TomcatApp.class);
        
        // then
        app.stop();
    }

    @Test
    public void testWithContextPath() throws Exception {
        // given
        System.setProperty("server.context.path", ".");

        // when
        EzyHttpApplication app = EzyHttpApplication.start(TomcatApp.class);

        // then
        app.stop();
    }

    @Test
    public void testWithMultiPathLocationIsNotExist() throws Exception {
        // given
        File folder = new File("target/multipart_location");
        if (folder.exists()) {
            EzyDirectories.deleteFolder(folder);
        }
        ApplicationContext applicationContext = new ApplicationContextBuilder()
            .scan("com.tvd12.ezyhttp.server.tomcat.test")
            .build();
        EzyHttpApplication application = new EzyHttpApplication(applicationContext);
        TomcatApplicationBootstrap bootstrap = application
            .getApplicationContext()
            .getSingleton(TomcatApplicationBootstrap.class);
        bootstrap.setMultipartLocation(folder.getAbsolutePath());

        // when
        application.start();

        // then
        application.stop();
    }

    @Test
    public void testWithMultiPathLocationIsNull() throws Exception {
        // given
        ApplicationContext applicationContext = new ApplicationContextBuilder()
            .scan("com.tvd12.ezyhttp.server.tomcat.test")
            .build();
        EzyHttpApplication application = new EzyHttpApplication(applicationContext);
        TomcatApplicationBootstrap bootstrap = application
            .getApplicationContext()
            .getSingleton(TomcatApplicationBootstrap.class);
        bootstrap.setMultipartLocation(null);

        // when
        application.start();

        // then
        application.stop();
    }
}
