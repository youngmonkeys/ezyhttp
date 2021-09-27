package com.tvd12.ezyhttp.server.thymeleaf.test;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.view.TemplateResolver;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.core.view.ViewContextBuilder;
import com.tvd12.ezyhttp.server.thymeleaf.ThymeleafViewContextBuilder;
import com.tvd12.test.assertion.Asserts;

public class ThymeleafViewContextBuilderTest {

	@Test
	public void test() {
		// given
		TemplateResolver resolver = TemplateResolver.builder()
				.build();
		ViewContextBuilder sut = new ThymeleafViewContextBuilder()
				.templateResolver(resolver);
		
		// when
		ViewContext viewContext = sut.build();
		
		// then
		Asserts.assertNotNull(viewContext);
	}
}
