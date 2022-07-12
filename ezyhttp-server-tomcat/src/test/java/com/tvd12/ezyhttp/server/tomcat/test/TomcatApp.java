package com.tvd12.ezyhttp.server.tomcat.test;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyhttp.server.core.EzyHttpApplication;
import com.tvd12.ezyhttp.server.core.annotation.ComponentClasses;
import com.tvd12.ezyhttp.server.tomcat.TomcatApplicationBootstrap;
import org.testng.annotations.Test;

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
}
