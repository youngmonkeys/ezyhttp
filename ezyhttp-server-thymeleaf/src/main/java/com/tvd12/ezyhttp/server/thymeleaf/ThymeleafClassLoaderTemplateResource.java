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
import org.thymeleaf.templateresource.ClassLoaderTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

import java.io.*;
import java.util.List;

import static com.tvd12.ezyhttp.server.thymeleaf.ThymeleafTemplateResourceUtils.*;

/**
 * <p>
 *   Implementation of {@link ITemplateResource} representing a resource accessible
 *   by a {@link ClassLoader} (i.e. living at the <em>class path</em>).
 * </p>
 * <p>
 *   Objects of this class are usually created by
 *   {@link org.thymeleaf.templateresolver.ClassLoaderTemplateResolver}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ThymeleafClassLoaderTemplateResource implements ITemplateResource {

    private final String template;
    private final String path;
    private final String characterEncoding;
    private final List<ViewTemplateInputStreamLoader> templateInputStreamLoaders;

    /**
     * <p>
     *   Create a ClassLoader-based template resource, specifying the specific class loader
     *   to be used for resolving the resource.
     * </p>
     *
     * @param template the name of template.
     * @param path the path to the template resource.
     * @param characterEncoding the character encoding to be used to read the resource.
     * @param templateInputStreamLoaders the list of template input stream loaders
     *   if template not found in the classpath.
     * @since 3.0.3
     */
    public ThymeleafClassLoaderTemplateResource(
        final String template,
        final String path,
        final String characterEncoding,
        final List<ViewTemplateInputStreamLoader> templateInputStreamLoaders
    ) {
        super();

        // Class Loader CAN be null (will apply the default sequence of class loaders
        Validate.notEmpty(path, "Resource Path cannot be null or empty");

        this.template = template;

        // Character encoding CAN be null (system default will be used)
        final String cleanPath = cleanPath(path);
        this.path = cleanPath.charAt(0) == '/'
            ? cleanPath.substring(1)
            : cleanPath;
        this.characterEncoding = characterEncoding;
        this.templateInputStreamLoaders = templateInputStreamLoaders;
    }

    @Override
    public String getDescription() {
        return this.path;
    }

    @Override
    public String getBaseName() {
        return computeBaseName(path);
    }

    @Override
    public Reader reader() throws IOException {
        InputStream inputStream = ClassLoaderUtils
            .findResourceAsStream(this.path);
        if (inputStream == null) {
            for (ViewTemplateInputStreamLoader loader : templateInputStreamLoaders) {
                inputStream = loader.load(template, path);
            }
        }
        if (inputStream == null) {
            throw new FileNotFoundException(
                String.format(
                    "ClassLoader resource \"%s\" could not be resolved",
                    this.path
                )
            );
        }
        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(
                new InputStreamReader(
                    new BufferedInputStream(inputStream),
                    this.characterEncoding
                )
            );
        }
        return new BufferedReader(
            new InputStreamReader(
                new BufferedInputStream(inputStream)
            )
        );
    }

    @Override
    public ITemplateResource relative(
        final String relativeLocation
    ) {
        Validate.notEmpty(
            relativeLocation,
            "Relative Path cannot be null or empty"
        );
        final String fullRelativeLocation = computeRelativeLocation(
            this.path,
            relativeLocation
        );
        return new ClassLoaderTemplateResource(
            null,
            fullRelativeLocation,
            this.characterEncoding
        );
    }

    @Override
    public boolean exists() {
        return ClassLoaderUtils.isResourcePresent(this.path);
    }
}
