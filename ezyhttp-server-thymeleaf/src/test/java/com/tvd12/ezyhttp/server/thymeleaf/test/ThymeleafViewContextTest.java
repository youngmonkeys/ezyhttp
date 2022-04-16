package com.tvd12.ezyhttp.server.thymeleaf.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

import com.tvd12.ezyhttp.server.core.view.TemplateResolver;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.core.view.ViewDecorator;
import com.tvd12.ezyhttp.server.core.view.ViewDialect;
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
	
	@Test
    public void renderWithViewDecorator() throws Exception {
        // given
        TemplateResolver resolver = TemplateResolver.builder()
                .build();
        
        ViewDecorator viewDecorator = mock(ViewDecorator.class);
        
        ViewContext viewContext = new ThymeleafViewContextBuilder()
                .templateResolver(resolver)
                .viewDecorators(Collections.singletonList(viewDecorator))
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
        verify(viewDecorator, times(1)).decorate(request, view);
    }
	
	@Test
    public void renderWithViewDialect() throws Exception {
        // given
        TemplateResolver resolver = TemplateResolver.builder()
                .build();
        
        ViewContext viewContext = new ThymeleafViewContextBuilder()
                .templateResolver(resolver)
                .viewDialects(Collections.singletonList(new VewHelloDialect()))
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
	
    @Test
    public void renderWithDialectButNotViewDialect() throws Exception {
        // given
        TemplateResolver resolver = TemplateResolver.builder()
                .build();
        
        ViewDialect viewDialect = mock(ViewDialect.class);
        ViewContext viewContext = new ThymeleafViewContextBuilder()
                .templateResolver(resolver)
                .viewDialects(Collections.singletonList(viewDialect))
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
	
	private static class VewHelloDialect 
	        extends HelloDialect 
	        implements ViewDialect {}
	
	
	private static class HelloDialect extends AbstractProcessorDialect {

	    public HelloDialect() {
	        super(
                "Hello Dialect",    // Dialect name
                "hello",            // Dialect prefix (hello:*)
                1000                // Dialect precedence
            );
	    }

	    
	    public Set<IProcessor> getProcessors(final String dialectPrefix) {
	        final Set<IProcessor> processors = new HashSet<>();
	        processors.add(new SayToAttributeTagProcessor(dialectPrefix));
	        return processors;
	    }
	}
	
	private static class SayToAttributeTagProcessor extends AbstractAttributeTagProcessor {

	    private static final String ATTR_NAME = "sayto";
	    private static final int PRECEDENCE = 10000;


	    public SayToAttributeTagProcessor(final String dialectPrefix) {
	        super(
	            TemplateMode.HTML, // This processor will apply only to HTML mode
	            dialectPrefix,     // Prefix to be applied to name for matching
	            null,              // No tag name: match any tag name
	            false,             // No prefix to be applied to tag name
	            ATTR_NAME,         // Name of the attribute that will be matched
	            true,              // Apply dialect prefix to attribute name
	            PRECEDENCE,        // Precedence (inside dialect's precedence)
	            true
            );             // Remove the matched attribute afterwards
	    }


	    protected void doProcess(
	            final ITemplateContext context, final IProcessableElementTag tag,
	            final AttributeName attributeName, final String attributeValue,
	            final IElementTagStructureHandler structureHandler) {

	        structureHandler.setBody(
	                "Hello, " + HtmlEscape.escapeHtml5(attributeValue) + "!", false);
	    }
	}
}
