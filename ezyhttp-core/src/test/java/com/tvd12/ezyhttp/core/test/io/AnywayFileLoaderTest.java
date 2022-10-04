package com.tvd12.ezyhttp.core.test.io;

import java.io.File;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.io.AnywayFileLoader;
import com.tvd12.test.assertion.Asserts;

public class AnywayFileLoaderTest {

    @Test
    public void foundAFileInSystemPathTest() {
        // given
        final String filePath = "pom.xml";

        // when
        final File actual = new AnywayFileLoader().load(filePath);

        // then
        Asserts.assertEquals(actual, new File("pom.xml"));
    }

    @Test
    public void foundAFileInClassPathTest() {
        // given
        final String filePath = "AllTests.tng.xml";

        // when
        final File actual = new AnywayFileLoader().load(filePath);

        // then
        Asserts.assertTrue(actual.exists());
    }

    @Test
    public void fileNotFoundTest() {
        // given
        final String filePath = "file not found";

        // when
        final File actual = new AnywayFileLoader().load(filePath);

        // then
        Asserts.assertNull(actual);
    }
}
