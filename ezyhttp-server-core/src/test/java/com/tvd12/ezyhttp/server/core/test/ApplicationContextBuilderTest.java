package com.tvd12.ezyhttp.server.core.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.bean.EzyPropertiesMap;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.reflect.EzyReflection;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.ApplicationContext;
import com.tvd12.ezyhttp.server.core.ApplicationContextBuilder;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.resources.ResourceResolver;
import com.tvd12.ezyhttp.server.core.test.event.EventService;
import com.tvd12.ezyhttp.server.core.test.event.SourceService;
import com.tvd12.ezyhttp.server.core.test.service.UserService;
import com.tvd12.ezyhttp.server.core.view.AbsentMessageResolver;
import com.tvd12.ezyhttp.server.core.view.MessageProvider;
import com.tvd12.ezyhttp.server.core.view.TemplateResolver;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.core.view.ViewContextBuilder;
import com.tvd12.ezyhttp.server.core.view.ViewDecorator;
import com.tvd12.ezyhttp.server.core.view.ViewDialect;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodInvoker;

public class ApplicationContextBuilderTest {

    @AfterMethod
    public void postTest() {
        ComponentManager.getInstance().destroy();
    }
    
	@Test
	public void test() {
		// given
	    EzyBeanContext internalBeanContext = EzyBeanContext.builder()
	            .addSingleton(new InternalSingleton1())
	            .build();
		Properties properties = new Properties();
		properties.put("b", 2);
		ApplicationContext applicationContext = new ApplicationContextBuilder()
			.scan(
				"com.tvd12.ezyhttp.server.core.test.component", 
				"com.tvd12.ezyhttp.server.core.test.config", 
				"com.tvd12.ezyhttp.server.core.test.controller"
			)
			.scan(
				Arrays.asList(
					"com.tvd12.ezyhttp.server.core.test.reflect", 
					"com.tvd12.ezyhttp.server.core.test.request",
					"com.tvd12.ezyhttp.server.core.test.resources",
					"com.tvd12.ezyhttp.server.core.test.service"
				)
			)
			.addComponentClasses(Arrays.asList(SourceService.class))
			.addPropertiesSources(Arrays.asList("application3.yaml"))
			.addPropertiesSource("application-enable.yaml")
			.addProperty("a", "1")
			.addProperties(properties)
			.addProperties(Collections.singletonMap("c", "3"))
			.beanContext(internalBeanContext)
			.addSingleton(new InternalSingleton2())
			.addSingleton(Collections.singletonMap("internalSingleton3", new InternalSingleton3()))
			.addSingleton(mock(AbsentMessageResolver.class))
			.build();
		
		EzyBeanContext beanContext = applicationContext.getBeanContext();
		
		// when
		int actualOneProp = beanContext.getProperty("one", int.class);
		boolean managementEnable = beanContext.getProperty("management.enable", boolean.class);
		Boolean resourceEnable = beanContext.getProperty("resources.enable", boolean.class);
		Boolean resourceUploadEnable = beanContext.getProperty("resources.upload.enable", boolean.class);
		UserService userService = beanContext.getSingleton(UserService.class);
		ViewContextBuilder viewContextBuilder = beanContext.getSingleton(ViewContextBuilder.class);
		ResourceResolver resourceResolver = beanContext.getSingleton(ResourceResolver.class);
		ResourceDownloadManager resourceDownloadManager = beanContext.getSingleton(ResourceDownloadManager.class);
		Set<String> packagesToScan = beanContext.getPackagesToScan();
		EventService eventService = beanContext.getSingleton(EventService.class);
		SourceService sourceService = beanContext.getSingleton(SourceService.class);
		String helloValue = beanContext.getProperty("hello", String.class);
		InternalSingleton1 internalSingleton1 = beanContext.getBeanCast(InternalSingleton1.class);
		InternalSingleton2 internalSingleton2 = beanContext.getBeanCast(InternalSingleton2.class);
		InternalSingleton3 internalSingleton3 = beanContext.getBeanCast(InternalSingleton3.class);
		
		
		// then
		Asserts.assertEquals(1, actualOneProp);
		Asserts.assertTrue(managementEnable);
		Asserts.assertTrue(resourceEnable);
		Asserts.assertTrue(resourceUploadEnable);
		Asserts.assertNotNull(userService);
		Asserts.assertNotNull(viewContextBuilder);
		Asserts.assertNotNull(resourceDownloadManager);
		Asserts.assertEquals(4, resourceResolver.getResources().size());
		Asserts.assertNotNull(eventService);
		Asserts.assertNotNull(sourceService);
		Asserts.assertNotNull(helloValue);
		Asserts.assertEquals("1", beanContext.getProperty("a", String.class));
		Asserts.assertEquals(2, beanContext.getProperty("b", int.class));
		Asserts.assertEquals("3", beanContext.getProperty("c", String.class));
		Asserts.assertEquals("3", applicationContext.getProperty("c", String.class));
		Asserts.assertEquals(
			packagesToScan,
			Sets.newHashSet(
			    "com.tvd12.ezyhttp.server",
			    "com.tvd12.ezyhttp.server.core.test",
				"com.tvd12.ezyhttp.server.core.test.component", 
				"com.tvd12.ezyhttp.server.core.test.config", 
				"com.tvd12.ezyhttp.server.core.test.controller",
				"com.tvd12.ezyhttp.server.core.test.event",
				"com.tvd12.ezyhttp.server.core.test.api",
				"com.tvd12.ezyhttp.server.core.test.reflect", 
				"com.tvd12.ezyhttp.server.core.test.request",
				"com.tvd12.ezyhttp.server.core.test.resources",
				"com.tvd12.ezyhttp.server.core.test.service"
			)
		);
		Asserts.assertNotNull(internalSingleton1);
		Asserts.assertNotNull(internalSingleton2);
		Asserts.assertNotNull(internalSingleton3);
		System.out.println(applicationContext);
		applicationContext.destroy();
	}
	
	@Test
	public void testNotEnable() {
		// given
		System.setProperty(EzyBeanContext.ACTIVE_PROFILES_KEY, "disable");
		ApplicationContext applicationContext = new ApplicationContextBuilder()
			.scan("i_dont_know")
			.build();
		System.setProperty(EzyBeanContext.ACTIVE_PROFILES_KEY, "enable");
		
		EzyBeanContext beanContext = applicationContext.getBeanContext();
		
		// when
		Boolean managementEnable = beanContext.getProperty("management.enable", boolean.class);
		Boolean resourceEnable = beanContext.getProperty("resources.enable", boolean.class);
		Boolean resourceUploadEnable = beanContext.getProperty("resources.upload.enable", boolean.class);
		ResourceResolver resourceResolver = beanContext.getSingleton(ResourceResolver.class);
		ResourceDownloadManager resourceDownloadManager = beanContext.getSingleton(ResourceDownloadManager.class);
		Set<String> packagesToScan = beanContext.getPackagesToScan();
		
		
		// then
		Asserts.assertFalse(managementEnable);
		Asserts.assertFalse(resourceEnable);
		Asserts.assertFalse(resourceUploadEnable);
		Asserts.assertNull(resourceResolver);
		Asserts.assertNull(resourceDownloadManager);
		Asserts.assertEquals(
	        Sets.newHashSet(
                "i_dont_know",
                "com.tvd12.ezyhttp.server",
                "com.tvd12.ezyhttp.server.core.test",
                "com.tvd12.ezyhttp.server.core.test.config", 
                "com.tvd12.ezyhttp.server.core.test.event",
                "com.tvd12.ezyhttp.server.core.test.api"), 
	        packagesToScan);
		applicationContext.destroy();
	}
	
	@Test
	public void buildFailedDueToPackageToScans() {
		// given
		ApplicationContextBuilder builder = new ApplicationContextBuilder();
		
		// when
		Throwable e = Asserts.assertThrows(builder::build);
		
		// then
		Asserts.assertThat(e).isEqualsType(IllegalStateException.class);
	}
	
	@Test
	public void getPropertiesMapNull() {
		// given
		EzyReflection reflection = mock(EzyReflection.class);
		when(reflection.getExtendsClass(EzyPropertiesMap.class)).thenReturn(null);
		
		ApplicationContextBuilder sut = new ApplicationContextBuilder();
		
		// when
		EzyPropertiesMap actual = MethodInvoker.create()
				.object(sut)
				.method("getPropertiesMap")
				.param(EzyReflection.class, reflection)
				.invoke(EzyPropertiesMap.class);
		
		// then
		Asserts.assertNull(actual);
	}
	
	@Test
	public void buildViewContextViewContextNotNull() {
		// given
		EzyBeanContext beanContext = mock(EzyBeanContext.class);
		ViewContext viewContext = mock(ViewContext.class);
		when(beanContext.getSingleton(ViewContext.class)).thenReturn(viewContext);
		
		EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
		when(beanContext.getSingletonFactory()).thenReturn(singletonFactory);
		
		ApplicationContextBuilder sut = new ApplicationContextBuilder();
		
		// when
		ViewContext actual = MethodInvoker.create()
				.object(sut)
				.method("buildViewContext")
				.param(EzyBeanContext.class, beanContext)
				.invoke(ViewContext.class);
		
		// then
		Asserts.assertEquals(viewContext, actual);
		
		verify(beanContext, times(1)).getSingleton(ViewContext.class);
		verify(beanContext, times(1)).getSingletonFactory();
		verify(singletonFactory, times(1)).addSingleton(viewContext);
	}
	
	@Test
	public void buildViewContextViewContextBuilderIsNull() {
		// given
		EzyBeanContext beanContext = mock(EzyBeanContext.class);
		
		ApplicationContextBuilder sut = new ApplicationContextBuilder();
		
		// when
		ViewContext actual = MethodInvoker.create()
				.object(sut)
				.method("buildViewContext")
				.param(EzyBeanContext.class, beanContext)
				.invoke(ViewContext.class);
		
		// then
		Asserts.assertNull(actual);
		
		verify(beanContext, times(1)).getSingleton(ViewContext.class);
		verify(beanContext, times(1)).getSingleton(ViewContextBuilder.class);
	}
	
	@Test
	public void buildViewContextTemplateResolverNotNullIsNull() {
		// given
		EzyBeanContext beanContext = mock(EzyBeanContext.class);
		
		ViewContextBuilder viewContextBuilder = mock(ViewContextBuilder.class);
		when(beanContext.getSingleton(ViewContextBuilder.class)).thenReturn(viewContextBuilder);
		
		TemplateResolver templateResolver = mock(TemplateResolver.class);
		when(beanContext.getSingleton(TemplateResolver.class)).thenReturn(templateResolver);
		
		when(viewContextBuilder.templateResolver(templateResolver)).thenReturn(viewContextBuilder);
		
		ViewContext viewContext = mock(ViewContext.class);
		when(viewContextBuilder.build()).thenReturn(viewContext);
		
		EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
		when(beanContext.getSingletonFactory()).thenReturn(singletonFactory);
		
		ViewDialect viewDialect = mock(ViewDialect.class);
		List<ViewDialect> viewDialects = Arrays.asList(viewDialect);
		when(beanContext.getSingletonsOf(ViewDialect.class)).thenReturn(viewDialects);
		when(viewContextBuilder.viewDialects(viewDialects)).thenReturn(viewContextBuilder);
		
		ViewDecorator viewDecorator = mock(ViewDecorator.class);
		List<ViewDecorator> viewDecorators = Arrays.asList(viewDecorator);
		when(beanContext.getSingletonsOf(ViewDecorator.class)).thenReturn(viewDecorators);
		when(viewContextBuilder.viewDecorators(viewDecorators)).thenReturn(viewContextBuilder);
		
		MessageProvider messageProvider = mock(MessageProvider.class);
		List<MessageProvider> messageProviders = Arrays.asList(messageProvider);
		when(beanContext.getSingletonsOf(MessageProvider.class)).thenReturn(messageProviders);
		when(viewContextBuilder.messageProviders(messageProviders)).thenReturn(viewContextBuilder);
		
		AbsentMessageResolver absentMessageResolver = mock(AbsentMessageResolver.class);
		when(beanContext.getSingleton(AbsentMessageResolver.class)).thenReturn(absentMessageResolver);
		when(viewContextBuilder.absentMessageResolver(absentMessageResolver)).thenReturn(viewContextBuilder);
		
		ApplicationContextBuilder sut = new ApplicationContextBuilder();
		
		// when
		ViewContext actual = MethodInvoker.create()
				.object(sut)
				.method("buildViewContext")
				.param(EzyBeanContext.class, beanContext)
				.invoke(ViewContext.class);
		
		// then
		Asserts.assertEquals(viewContext, actual);
		
		verify(beanContext, times(1)).getSingleton(ViewContext.class);
		verify(beanContext, times(1)).getSingleton(ViewContextBuilder.class);
		verify(beanContext, times(1)).getSingleton(TemplateResolver.class);
		verify(beanContext, times(1)).getSingletonsOf(ViewDialect.class);
		verify(beanContext, times(1)).getSingletonsOf(ViewDecorator.class);
		verify(beanContext, times(1)).getSingleton(AbsentMessageResolver.class);
		verify(beanContext, times(1)).getSingletonsOf(MessageProvider.class);
		verify(viewContextBuilder, times(1)).templateResolver(templateResolver);
		verify(viewContextBuilder, times(1)).viewDialects(viewDialects);
		verify(viewContextBuilder, times(1)).viewDecorators(viewDecorators);
		verify(viewContextBuilder, times(1)).messageProviders(messageProviders);
		verify(viewContextBuilder, times(1)).absentMessageResolver(absentMessageResolver);
		verify(viewContextBuilder, times(1)).build();
		verify(singletonFactory, times(1)).addSingleton(viewContext);
	}
	
	@Test
	public void getResourceResolverNotNull() {
		// given
		EzyBeanContext beanContext = mock(EzyBeanContext.class);
		ResourceResolver resolver = mock(ResourceResolver.class);
		when(beanContext.getSingleton(ResourceResolver.class)).thenReturn(resolver);
		
		ApplicationContextBuilder sut = new ApplicationContextBuilder();
		
		// when
		ResourceResolver actual = MethodInvoker.create()
				.object(sut)
				.method("getResourceResolver")
				.param(EzyBeanContext.class, beanContext)
				.invoke(ResourceResolver.class);
		
		// then
		Asserts.assertEquals(resolver, actual);
		
		verify(beanContext, times(1)).getSingleton(ResourceResolver.class);
	}
	
	private static class InternalSingleton1  {}
	
	private static class InternalSingleton2  {}
	
	private static class InternalSingleton3  {}
}
