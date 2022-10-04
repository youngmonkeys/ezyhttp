package com.tvd12.ezyhttp.core.io;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AnywayFileLoader {

    private final List<Function<String, File>> loaders =
        Arrays.asList(
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

    public File load(String filePath) {
        for (Function<String, File> loader : loaders) {
            final File file = loader.apply(filePath);
            if (file != null && file.exists()) {
                return file;
            }
        }
        return null;
    }

    private static File newFileFromUrl(URL fileUrl) {
        return fileUrl != null ? new File(fileUrl.getFile()) : null;
    }
}
