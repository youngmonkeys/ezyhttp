package com.tvd12.ezyhttp.server.core.view;

import java.io.IOException;
import java.io.InputStream;

public interface ViewTemplateInputStreamLoader {

    /**
     * Load view template input stream.
     *
     * @param template the name of template.
     * @param templatePath the path of template.
     * @return the input stream of template.
     * @throws IOException when exception occurs.
     */
    InputStream load(
        String template,
        String templatePath
    ) throws IOException;

    /**
     * Get priority of the loader, smaller is run first.
     *
     * @return the priority.
     */
    int priority();
}
