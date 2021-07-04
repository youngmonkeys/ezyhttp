package com.tvd12.ezyhttp.server.thymeleaf;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.tvd12.ezyhttp.server.core.view.TemplateResolver;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;

public class ThymeleafViewContext implements ViewContext {

	private final TemplateEngine templateEngine;
	
	public ThymeleafViewContext(TemplateResolver templateResolver) {
		this.templateEngine = createTemplateEngine(templateResolver);
	}
	
	@Override
	public void render(
			ServletContext servletContext,
			HttpServletRequest request,
			HttpServletResponse response, View view) throws IOException {
		WebContext ctx = 
	            new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariables(view.getVariables());
	    templateEngine.process(view.getTemplate(), ctx, response.getWriter());
	}
	
	private TemplateEngine createTemplateEngine(TemplateResolver data) {
		ClassLoaderTemplateResolver templateResolver = 
                new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.valueOf(data.getTemplateMode()));
        templateResolver.setPrefix(data.getPrefix());
        templateResolver.setSuffix(data.getSuffix());
        templateResolver.setCacheTTLMs(Long.valueOf(data.getCacheTTLMs()));
        templateResolver.setCacheable(data.isCacheable());
        
        ThymeleafMessageResolver messageResolver = 
        		new ThymeleafMessageResolver(data.getMessagesLocation());
        
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(messageResolver);
        return templateEngine;
	}
}
