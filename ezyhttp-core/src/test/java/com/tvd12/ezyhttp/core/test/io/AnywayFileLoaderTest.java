package com.tvd12.ezyhttp.core.test.io;

import com.tvd12.ezyhttp.core.io.AnywayFileLoader;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodUtil;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.net.URLStreamHandler;

import static org.mockito.Mockito.mock;

public class AnywayFileLoaderTest {

    @Test
    public void foundAFileInSystemPathTest() {
        // given
        final String filePath = "pom.xml";

        // when
        final File actual = AnywayFileLoader.getDefault().load(filePath);

        // then
        Asserts.assertEquals(actual, new File("pom.xml"));
    }

    @Test
    public void foundAFileInClassPathTest() {
        // given
        final String filePath = "AllTests.tng.xml";

        // when
        final File actual = AnywayFileLoader.getDefault().load(filePath);

        // then
        Asserts.assertTrue(actual.exists());
    }

    @Test
    public void fileNotFoundTest() {
        // given
        final String filePath = "file not found";

        // when
        final File actual = AnywayFileLoader.getDefault().load(filePath);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void fileNotFoundInvalidUriTest() {
        // given
        final String filePath = "file not found";

        // when
        final File actual = AnywayFileLoader.getDefault().load(filePath);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void newFileFromUrlByUriNotFoundTest() throws Exception {
        // given
        URL url = new File("file not found").toURI().toURL();
        AnywayFileLoader sut = AnywayFileLoader.getDefault();

        // when
        final File actual = MethodUtil.invokeMethod(
            "newFileFromUrl",
            sut,
            url
        );

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void newFileFromUrlByInvalidUriTest() throws Exception {
        // given
        URL context = new URL("http://hello");
        URLStreamHandler handler = mock(URLStreamHandler.class);
        URL url = new URL(context, "http://hello", handler);
        AnywayFileLoader sut = AnywayFileLoader.getDefault();

        // when
        final File actual = MethodUtil.invokeMethod(
            "newFileFromUrl",
            sut,
            url
        );

        // then
        Asserts.assertNull(actual);
    }
}
