package com.tvd12.ezyhttp.server.thymeleaf;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.tvd12.ezyhttp.server.core.view.AbsentMessageResolver;
import com.tvd12.ezyhttp.server.core.view.MessageProvider;
import com.tvd12.ezyhttp.server.core.view.TemplateResolver;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.core.view.ViewDecorator;
import com.tvd12.ezyhttp.server.core.view.ViewDialect;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

public class ThymeleafViewContext implements ViewContext {

    private final TemplateResolver metadata;
	private final TemplateEngine templateEngine;
	private final List<ViewDialect> viewDialects;
	private final List<ViewDecorator> viewDecorators;
	private final List<MessageProvider> messageProviders;
	private final AbsentMessageResolver absentMessageResolver;
	
	public ThymeleafViewContext(
        TemplateResolver metadata,
        List<ViewDialect> viewDialects,
        List<ViewDecorator> viewDecorators,
        List<MessageProvider> messageProviders,
        AbsentMessageResolver absentMessageResolver
    ) {
	    this.metadata = metadata;
	    this.viewDialects = viewDialects;
	    this.viewDecorators = viewDecorators;
	    this.messageProviders = messageProviders;
	    this.absentMessageResolver = absentMessageResolver;
		this.templateEngine = createTemplateEngine();
	}
	
	@Override
	public void render(
			ServletContext servletContext,
			HttpServletRequest request,
			HttpServletResponse response, View view) throws IOException {
	    for (ViewDecorator viewDecorator : viewDecorators) {
	        viewDecorator.decorate(request, view);
	    }
		WebContext ctx = 
	            new WebContext(request, response, servletContext, view.getLocale());
		ctx.setVariables(view.getVariables());
	    templateEngine.process(view.getTemplate(), ctx, response.getWriter());
	}
	
	private TemplateEngine createTemplateEngine() {
		ClassLoaderTemplateResolver templateResolver = 
                new ClassLoaderTemplateResolver();
		TemplateMode templateMode = TemplateMode.valueOf(metadata.getTemplateMode());
        templateResolver.setTemplateMode(templateMode);
        templateResolver.setPrefix(metadata.getPrefix());
        templateResolver.setSuffix(metadata.getSuffix());
        templateResolver.setCacheTTLMs(Long.valueOf(metadata.getCacheTTLMs()));
        templateResolver.setCacheable(metadata.isCacheable());
        
        ThymeleafMessageResolver messageResolver = ThymeleafMessageResolver.builder()
                .messageLocation(metadata.getMessagesLocation())
                .messageProviders(messageProviders)
                .absentMessageResolver(absentMessageResolver)
                .build();
        
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(messageResolver);
        templateEngine.addDialect(new LayoutDialect());
        for (ViewDialect dialect : viewDialects) {
            if (dialect instanceof IDialect) {
                templateEngine.addDialect((IDialect) dialect);
            }
        }
        return templateEngine;
	}
}
