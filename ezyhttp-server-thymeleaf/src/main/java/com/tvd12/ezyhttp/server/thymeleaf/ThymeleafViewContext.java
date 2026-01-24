package com.tvd12.ezyhttp.server.thymeleaf;

import com.tvd12.ezyhttp.server.core.view.*;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ThymeleafViewContext implements ViewContext {

    private final TemplateResolver metadata;
    private final TemplateEngine templateEngine;
    private final TemplateEngine contentTemplateEngine;
    private final List<ViewDialect> viewDialects;
    private final List<ViewDecorator> viewDecorators;
    private final List<MessageProvider> messageProviders;
    private final ThymeleafMessageResolver messageResolver;
    private final AbsentMessageResolver absentMessageResolver;
    private final List<ViewTemplateInputStreamLoader> templateInputStreamLoaders;

    public ThymeleafViewContext(
        TemplateResolver metadata,
        List<ViewDialect> viewDialects,
        List<ViewDecorator> viewDecorators,
        List<MessageProvider> messageProviders,
        AbsentMessageResolver absentMessageResolver,
        List<ViewTemplateInputStreamLoader> templateInputStreamLoaders
    ) {
        this.metadata = metadata;
        this.viewDialects = viewDialects;
        this.viewDecorators = viewDecorators;
        this.messageProviders = messageProviders;
        this.absentMessageResolver = absentMessageResolver;
        this.templateInputStreamLoaders = templateInputStreamLoaders;
        this.messageResolver = createMessageResolver();
        this.templateEngine = createTemplateEngine();
        this.contentTemplateEngine = createContentTemplateEngine();
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
        IContext context = new Context(
            view.getLocale(),
            view.getVariables()
        );
        templateEngine.process(view.getTemplate(), context, response.getWriter());
    }

    @Override
    public String renderHtml(View view) {
        IContext context = new Context(
            view.getLocale(),
            view.getVariables()
        );
        return renderHtml(context, view);
    }

    @Override
    public String renderHtml(Object context, View view) {
        return contentTemplateEngine.process(
            view.getTemplate(),
            (IContext) context
        );
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
        ThymeleafClassLoaderTemplateResolver templateResolver =
            new ThymeleafClassLoaderTemplateResolver(templateInputStreamLoaders);
        TemplateMode templateMode = TemplateMode.valueOf(metadata.getTemplateMode());
        templateResolver.setTemplateMode(templateMode);
        templateResolver.setPrefix(metadata.getPrefix());
        templateResolver.setSuffix(metadata.getSuffix());
        templateResolver.setCharacterEncoding(metadata.getCharacterEncoding());
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

    private TemplateEngine createContentTemplateEngine() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        templateEngine.setMessageResolver(messageResolver);
        templateEngine.addDialect(new LayoutDialect());
        for (ViewDialect dialect : viewDialects) {
            if (dialect instanceof IDialect) {
                templateEngine.addDialect((IDialect) dialect);
            }
        }
        return templateEngine;
    }

    @Override
    public ThymeleafMessageResolver getMessageResolver() {
        return messageResolver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateEngine getContentTemplateEngine() {
        return contentTemplateEngine;
    }
}
