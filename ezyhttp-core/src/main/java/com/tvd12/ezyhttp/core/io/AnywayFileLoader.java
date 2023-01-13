package com.tvd12.ezyhttp.core.io;

import com.tvd12.ezyfox.function.EzyExceptionFunction;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AnywayFileLoader {

    private final List<Function<String, File>> loaders = Arrays.asList(
        File::new,
        filePath -> newFileFromUrl(
            getClass().getResource(filePath)
        ),
        filePath -> newFileFromUrl(
            getClass().getClassLoader().getResource(filePath)
        ),
        filePath -> newFileFromUrl(
            getClass().getResource('/' + filePath)
        ),
        filePath -> newFileFromUrl(
            getClass().getClassLoader().getResource('/' + filePath)
        ),
        filePath -> newFileFromUrl(
            Thread.currentThread().getContextClassLoader().getResource(filePath)
        ),
        filePath -> newFileFromUrl(
            Thread.currentThread().getContextClassLoader().getResource('/' + filePath)
        )
    );

    private final List<EzyExceptionFunction<URL, File>> fileCreators = Arrays.asList(
        url -> new File(url.getFile()),
        url -> new File(url.toURI())
    );

    private static final AnywayFileLoader DEFAULT = new AnywayFileLoader();

    public static AnywayFileLoader getDefault() {
        return DEFAULT;
    }

    public File load(String filePath) {
        for (Function<String, File> loader : loaders) {
            final File file = loader.apply(filePath);
            if (file != null && file.exists()) {
                return file;
            }
        }
        return null;
    }

    private File newFileFromUrl(URL fileUrl) {
        if (fileUrl == null) {
            return null;
        }
        for (EzyExceptionFunction<URL, File> creator : fileCreators) {
            try {
                File file = creator.apply(fileUrl);
                if (file.exists()) {
                    return file;
                }
            } catch (Exception ignored) {
                // do nothing
            }
        }
        return null;
    }
}
