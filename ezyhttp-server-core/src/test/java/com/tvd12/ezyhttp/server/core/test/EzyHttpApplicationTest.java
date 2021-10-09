package com.tvd12.ezyhttp.server.core.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyhttp.server.core.ApplicationContext;
import com.tvd12.ezyhttp.server.core.EzyHttpApplication;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.resources.ResourceResolver;
import com.tvd12.ezyhttp.server.core.test.service.UserService;
import com.tvd12.ezyhttp.server.core.view.ViewContextBuilder;
import com.tvd12.test.assertion.Asserts;

public class EzyHttpApplicationTest {

    @AfterMethod
    public void postTest() {
        ComponentManager.getInstance().destroy();
    }
    
	@Test
	public void test() throws Exception {
		// given
		EzyHttpApplication sut = EzyHttpApplication.start(EzyHttpApplicationTest.class);
		ApplicationContext applicationContext = sut.getApplicationContext();
		EzyBeanContext beanContext = applicationContext.getBeanContext();
		
		// when
		int actualOneProp = beanContext.getProperty("one", int.class);
		boolean managementEnable = beanContext.getProperty("management.enable", boolean.class);
		UserService userService = beanContext.getSingleton(UserService.class);
		ViewContextBuilder viewContextBuilder = beanContext.getSingleton(ViewContextBuilder.class);
		ResourceResolver resourceResolver = beanContext.getSingleton(ResourceResolver.class);
		ResourceDownloadManager resourceDownloadManager = beanContext.getSingleton(ResourceDownloadManager.class);
		
		// then
		Asserts.assertEquals(1, actualOneProp);
		Asserts.assertTrue(managementEnable);
		Asserts.assertNotNull(userService);
		Asserts.assertNotNull(viewContextBuilder);
		Asserts.assertNotNull(resourceDownloadManager);
		Asserts.assertEquals(4, resourceResolver.getResources().size());
		sut.stop();
	}
	
	@Test
	public void startWith2Params() throws Exception {
		// given
		EzyHttpApplication sut = EzyHttpApplication.start(
				EzyHttpApplicationTest.class,
				getClass()
		);
		ApplicationContext applicationContext = sut.getApplicationContext();
		EzyBeanContext beanContext = applicationContext.getBeanContext();
		
		// when
		int actualOneProp = beanContext.getProperty("one", int.class);
		boolean managementEnable = beanContext.getProperty("management.enable", boolean.class);
		UserService userService = applicationContext.getSingleton(UserService.class);
		ViewContextBuilder viewContextBuilder = beanContext.getSingleton(ViewContextBuilder.class);
		ResourceResolver resourceResolver = beanContext.getSingleton(ResourceResolver.class);
		ResourceDownloadManager resourceDownloadManager = beanContext.getSingleton(ResourceDownloadManager.class);
		List<Object> bootstraps = applicationContext.getSingletons(ApplicationBootstrap.class);
		
		// then
		Asserts.assertEquals(1, actualOneProp);
		Asserts.assertTrue(managementEnable);
		Asserts.assertNotNull(userService);
		Asserts.assertNotNull(viewContextBuilder);
		Asserts.assertNotNull(resourceDownloadManager);
		Asserts.assertEquals(4, resourceResolver.getResources().size());
		Asserts.assertFalse(bootstraps.isEmpty());
		sut.stop();
	}
	
	@Test
	public void testFailedDueToNoApplicationEntry() {
		// given
	    ApplicationContext context = mock(ApplicationContext.class);
	    EzyHttpApplication sut = new EzyHttpApplication(context);
	    
		// when
		Throwable e = Asserts.assertThrows(() -> sut.start());
		
		// then
		Asserts.assertThat(e).isEqualsType(IllegalStateException.class);
		verify(context, times(1)).getAnnotatedSingleton(ApplicationBootstrap.class);
	}
	
	@Test
    public void testDisablePrintBanner() throws Exception {
        // given
	    System.setProperty(EzyBeanContext.ACTIVE_PROFILES_KEY, "disable");
        EzyHttpApplication sut = EzyHttpApplication.start(EzyHttpApplicationTest.class);
        System.setProperty(EzyBeanContext.ACTIVE_PROFILES_KEY, "enable");
        ApplicationContext applicationContext = sut.getApplicationContext();
        EzyBeanContext beanContext = applicationContext.getBeanContext();
        
        // when
        boolean bannerPrintable = beanContext.getProperty("banner.printable", boolean.class);
        
        // then
        Asserts.assertFalse(bannerPrintable);
        sut.stop();
    }
}
