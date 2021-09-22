package com.tvd12.ezyhttp.server.thymeleaf.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.testng.annotations.Test;
import org.thymeleaf.context.ITemplateContext;

import com.tvd12.ezyhttp.server.thymeleaf.ThymeleafMessageResolver;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodUtil;

public class ThymeleafMessageResolverTest {

	@Test
	public void resolveMessageEn() {
		// given
		ThymeleafMessageResolver sut = new ThymeleafMessageResolver("messages");
		ITemplateContext context = mock(ITemplateContext.class);
		when(context.getLocale()).thenReturn(new Locale("en", "US"));
		Class<?> origin = getClass();
		
		// when
		String welcomeMessage = sut.resolveMessage(context, origin, "home.welcome", new Object[0]);
		String greetTitle = sut.resolveMessage(context, origin, "greet.title", new Object[] {"Monkey"});
		String greetMessage = sut.resolveMessage(context, origin, "greet.message", new Object[] {"Monkey"});
		String unkown = sut.resolveMessage(context, origin, "unknown", new Object[0]);
		String foo = sut.resolveMessage(context, origin, "foo", new Object[0]);
		
		// then
		Asserts.assertEquals("DEFAULT", sut.getName());
		Asserts.assertEquals(0, sut.getOrder());
		Asserts.assertEquals("Welcome to EzyHTTP en_US", welcomeMessage);
		Asserts.assertEquals("Greet Monkey", greetTitle);
		Asserts.assertEquals("Great Monkey!", greetMessage, false);
		Asserts.assertNull(unkown);
		Asserts.assertEquals("Bar", foo);
	}
	
	@Test
	public void resolveMessageDefault() {
		// given
		ThymeleafMessageResolver sut = new ThymeleafMessageResolver("messages");
		ITemplateContext context = mock(ITemplateContext.class);
		when(context.getLocale()).thenReturn(new Locale("unkown"));
		Class<?> origin = getClass();
		
		// when
		String welcomeMessage = sut.resolveMessage(context, origin, "home.welcome", new Object[0]);
		String greetTitle = sut.resolveMessage(context, origin, "greet.title", new Object[] {"Monkey"});
		String greetMessage = sut.resolveMessage(context, origin, "greet.message", null);
		String unkown = sut.resolveMessage(context, origin, "unknown", new Object[0]);
		String foo = sut.resolveMessage(context, origin, "foo", new Object[0]);
		
		// then
		Asserts.assertEquals("Welcome to EzyHTTP", welcomeMessage);
		Asserts.assertEquals("Greet Monkey", greetTitle);
		Asserts.assertEquals("Great {0}!", greetMessage, false);
		Asserts.assertNull(unkown);
		Asserts.assertEquals("Bar", foo);
	}
	
	@Test
	public void createAbsentMessageRepresentation() {
		// given
		ThymeleafMessageResolver sut = new ThymeleafMessageResolver("messages");
		ITemplateContext context = mock(ITemplateContext.class);
		when(context.getLocale()).thenReturn(new Locale("unkown"));
		Class<?> origin = getClass();
		
		// when
		String welcomeMessage = sut.createAbsentMessageRepresentation(context, origin, "home.welcome", new Object[0]);
		
		// then
		Asserts.assertEquals("home.welcome", welcomeMessage);
	}
	
	@Test
	public void createAbsentMessageRepresentationOK() {
		// given
		ThymeleafMessageResolver sut = new ThymeleafMessageResolver("unknown");
		ITemplateContext context = mock(ITemplateContext.class);
		when(context.getLocale()).thenReturn(new Locale("unkown"));
		Class<?> origin = getClass();
		
		// when
		String welcomeMessage = sut.createAbsentMessageRepresentation(context, origin, "home.welcome", new Object[0]);
		
		// then
		Asserts.assertEquals("home.welcome", welcomeMessage);
	}
	
	@Test
	public void isFormatCandidateTest() {
		// given
		String message = "hello '0'";
		
		// when
		boolean actual = MethodUtil.invokeStaticMethod("isFormatCandidate", ThymeleafMessageResolver.class, message);
		
		// then
		Asserts.assertTrue(actual);
	}
}