/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2025 Thymeleaf (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package com.tvd12.ezyhttp.server.thymeleaf;

import com.tvd12.ezyhttp.server.core.view.ViewTemplateInputStreamLoader;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresource.ClassLoaderTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *   Implementation of {@link ITemplateResolver} that extends {@link AbstractConfigurableTemplateResolver}
 *   and creates {@link ClassLoaderTemplateResource} instances for template resources.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely rewritten in Thymeleaf 3.0.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public class ThymeleafClassLoaderTemplateResolver
    extends AbstractConfigurableTemplateResolver {

    private final List<ViewTemplateInputStreamLoader> templateInputStreamLoaders;

    public ThymeleafClassLoaderTemplateResolver(
        List<ViewTemplateInputStreamLoader> templateInputStreamLoaders
    ) {
        this.templateInputStreamLoaders = templateInputStreamLoaders
            .stream()
            .sorted(Comparator.comparingInt(ViewTemplateInputStreamLoader::priority))
            .collect(Collectors.toList());
    }

    @Override
    protected ITemplateResource computeTemplateResource(
        final IEngineConfiguration configuration,
        final String ownerTemplate,
        final String template,
        final String resourceName,
        final String characterEncoding,
        final Map<String, Object> templateResolutionAttributes
    ) {
        return new ThymeleafClassLoaderTemplateResource(
            template,
            resourceName,
            characterEncoding,
            templateInputStreamLoaders
        );
    }
}
