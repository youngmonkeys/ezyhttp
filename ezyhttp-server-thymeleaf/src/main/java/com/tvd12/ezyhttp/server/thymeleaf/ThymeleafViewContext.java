package com.tvd12.ezyhttp.server.thymeleaf;

import com.tvd12.ezyhttp.server.core.view.*;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ThymeleafViewContext implements ViewContext {

    private final TemplateResolver metadata;
    private final TemplateEngine templateEngine;
    private final List<ViewDialect> viewDialects;
    private final List<ViewDecorator> viewDecorators;
    private final List<MessageProvider> messageProviders;
    private final ThymeleafMessageResolver messageResolver;
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
        this.messageResolver = createMessageResolver();
        this.templateEngine = createTemplateEngine();
    }

    @Override
    public void render(
        ServletContext servletContext,
        HttpServletRequest request,
        HttpServletResponse response,
        View view
    ) throws IOException {
        for (ViewDecorator viewDecorator : viewDecorators) {
            viewDecorator.decorate(request, view);
        }
        WebContext ctx = new WebContext(
            request,
            response,
            servletContext,
            view.getLocale()
        );
        ctx.setVariables(view.getVariables());
        templateEngine.process(view.getTemplate(), ctx, response.getWriter());
    }

    @Override
    public String resolveMessage(
        Locale locale,
        String key,
        Object... parameters
    ) {
        String answer = messageResolver.resolveMessage(
            locale,
            key,
            parameters
        );
        if (answer == null) {
            answer = messageResolver.createAbsentMessageRepresentation(
                locale,
                key,
                parameters
            );
        }
        return answer;
    }

    private ThymeleafMessageResolver createMessageResolver() {
        return ThymeleafMessageResolver.builder()
            .messageLocation(metadata.getMessagesLocation())
            .messageProviders(messageProviders)
            .absentMessageResolver(absentMessageResolver)
            .build();
    }

    private TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver =
            new ClassLoaderTemplateResolver();
        TemplateMode templateMode = TemplateMode.valueOf(metadata.getTemplateMode());
        templateResolver.setTemplateMode(templateMode);
        templateResolver.setPrefix(metadata.getPrefix());
        templateResolver.setSuffix(metadata.getSuffix());
        templateResolver.setCacheTTLMs((long) metadata.getCacheTTLMs());
        templateResolver.setCacheable(metadata.isCacheable());

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
