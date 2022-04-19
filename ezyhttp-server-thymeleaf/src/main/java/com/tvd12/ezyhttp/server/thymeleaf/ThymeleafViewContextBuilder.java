package com.tvd12.ezyhttp.server.thymeleaf;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.core.view.ViewContextBuilder;

@EzySingleton
public class ThymeleafViewContextBuilder extends ViewContextBuilder {

    @Override
    public ViewContext build() {
        return new ThymeleafViewContext(
            templateResolver,
            viewDialects,
            viewDecorators,
            messageProviders,
            absentMessageResolver
        );
    }

}
