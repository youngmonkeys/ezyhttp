package com.tvd12.ezyhttp.server.thymeleaf.test;

import static org.mockito.Mockito.*;

import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.view.TemplateResolver;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.thymeleaf.ThymeleafViewContextBuilder;
import com.tvd12.test.assertion.Asserts;

public class ThymeleafViewContextTest {

	@Test
	public void test() throws Exception {
		// given
		TemplateResolver resolver = TemplateResolver.builder()
				.build();
		ViewContext viewContext = new ThymeleafViewContextBuilder()
				.templateResolver(resolver)
				.build();
		
		ServletContext servletContext = mock(ServletContext.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		View view = View.builder()
				.template("index.html")
				.build();
		
		// when
		viewContext.render(servletContext, request, response, view);
		
		// then
		Asserts.assertNotNull(viewContext);
	}
}
