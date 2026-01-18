package com.tvd12.ezyhttp.server.thymeleaf.test;

import com.tvd12.ezyhttp.server.core.view.ViewTemplateInputStreamLoader;
import com.tvd12.ezyhttp.server.thymeleaf.ThymeleafClassLoaderTemplateResource;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThymeleafClassLoaderTemplateResourceTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void constructorWithEmptyPath() {
        // given
        String template = "index";
        String path = "";

        // when
        new ThymeleafClassLoaderTemplateResource(
            template,
            path,
            null,
            Collections.emptyList()
        );

        // then
        // expect exception
    }

    @Test
    public void getDescriptionAndBaseName() {
        // given
        ThymeleafClassLoaderTemplateResource sut =
            new ThymeleafClassLoaderTemplateResource(
                "index",
                "/templates/./index.html",
                null,
                Collections.emptyList()
            );

        // when
        String description = sut.getDescription();
        String baseName = sut.getBaseName();

        // then
        Asserts.assertEquals("templates/index.html", description);
        Asserts.assertEquals("index", baseName);
    }

    @Test
    public void readerWithClasspathResourceAndEncoding() throws Exception {
        // given
        ThymeleafClassLoaderTemplateResource sut =
            new ThymeleafClassLoaderTemplateResource(
                "index",
                "templates/index.html",
                StandardCharsets.UTF_8.name(),
                Collections.emptyList()
            );

        // when
        Reader reader = sut.reader();
        String content = readAll(reader);

        // then
        Asserts.assertNotNull(content);
        Asserts.assertTrue(content.contains("EzyHTTP"));
    }

    @Test
    public void readerWithInputStreamLoader() throws Exception {
        // given
        ViewTemplateInputStreamLoader loader = mock(ViewTemplateInputStreamLoader.class);
        when(
            loader.load(
                "custom",
                "templates/custom.html"
            )
        ).thenReturn(
            new ByteArrayInputStream(
                "Hello Loader".getBytes(StandardCharsets.UTF_8)
            )
        );

        ThymeleafClassLoaderTemplateResource sut =
            new ThymeleafClassLoaderTemplateResource(
                "custom",
                "templates/custom.html",
                null,
                Collections.singletonList(loader)
            );

        // when
        Reader reader = sut.reader();
        String content = readAll(reader);

        // then
        Asserts.assertEquals("Hello Loader", content);
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void readerWithMissingResource() throws Exception {
        // given
        ThymeleafClassLoaderTemplateResource sut =
            new ThymeleafClassLoaderTemplateResource(
                "missing",
                "templates/missing.html",
                null,
                Collections.emptyList()
            );

        // when
        sut.reader();

        // then
        // expect exception
    }

    @Test
    public void relativeAndExists() {
        // given
        ThymeleafClassLoaderTemplateResource sut =
            new ThymeleafClassLoaderTemplateResource(
                "index",
                "templates/index.html",
                null,
                Collections.emptyList()
            );
        ThymeleafClassLoaderTemplateResource missing =
            new ThymeleafClassLoaderTemplateResource(
                "missing",
                "templates/missing.html",
                null,
                Collections.emptyList()
            );

        // when
        ITemplateResource relative = sut.relative("partials/header.html");
        boolean exists = sut.exists();
        boolean missingExists = missing.exists();

        // then
        Asserts.assertTrue(relative != null);
        Asserts.assertTrue(exists);
        Asserts.assertTrue(!missingExists);
    }

    private String readAll(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[256];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, read);
        }
        reader.close();
        return builder.toString();
    }
}
