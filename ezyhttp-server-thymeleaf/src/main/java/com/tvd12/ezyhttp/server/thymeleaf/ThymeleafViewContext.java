package com.tvd12.ezyhttp.server.thymeleaf;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;

@EzySingleton
public class ThymeleafViewContext implements ViewContext {

	private final TemplateEngine templateEngine;
	
	public ThymeleafViewContext() {
		this.templateEngine = createTemplateEngine();
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
	
	private TemplateEngine createTemplateEngine() {
		ClassLoaderTemplateResolver templateResolver = 
                new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        templateResolver.setCacheable(true);
        
        ThymeleafMessageResolver messageResolver = 
        		new ThymeleafMessageResolver("messages");
        
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(messageResolver);
        return templateEngine;
	}
}
