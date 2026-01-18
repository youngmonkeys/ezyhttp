package com.tvd12.ezyhttp.server.thymeleaf;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class ThymeleafTemplateResourceUtilsTest {

    @Test
    public void cleanPathNullEmptyAndSimple() {
        // given
        String nullPath = null;
        String emptyPath = "";
        String simplePath = "templates\\home.html";
        String plainPath = "a/b/c";

        // when
        String nullResult = ThymeleafTemplateResourceUtils.cleanPath(nullPath);
        String emptyResult = ThymeleafTemplateResourceUtils.cleanPath(emptyPath);
        String simpleResult = ThymeleafTemplateResourceUtils.cleanPath(simplePath);
        String plainResult = ThymeleafTemplateResourceUtils.cleanPath(plainPath);

        // then
        Asserts.assertNull(nullResult);
        Asserts.assertEquals("", emptyResult);
        Asserts.assertEquals("templates/home.html", simpleResult);
        Asserts.assertEquals("a/b/c", plainResult);
    }

    @Test
    public void cleanPathRelativeWithDotsAndDoubleSlash() {
        // given
        String path = "a/./b/../c//d/..";

        // when
        String result = ThymeleafTemplateResourceUtils.cleanPath(path);

        // then
        Asserts.assertEquals("a/c", result);
    }

    @Test
    public void cleanPathSimpleWithoutDotOrDoubleSlash() {
        // given
        String path = "foo/bar";

        // when
        String result = ThymeleafTemplateResourceUtils.cleanPath(path);

        // then
        Asserts.assertEquals("foo/bar", result);
    }

    @Test
    public void cleanPathRootBasedWithParentSegments() {
        // given
        String path = "/a/b/../c";
        String pathWithLeadingParents = "/../a";

        // when
        String result = ThymeleafTemplateResourceUtils.cleanPath(path);
        String resultWithParents = ThymeleafTemplateResourceUtils.cleanPath(pathWithLeadingParents);

        // then
        Asserts.assertEquals("/a/c", result);
        Asserts.assertEquals("/../a", resultWithParents);
    }

    @Test
    public void cleanPathWithUnixPathContainsDoubleSplash() {
        // given
        String path = "foo//bar";

        // when
        String result = ThymeleafTemplateResourceUtils.cleanPath(path);

        // then
        Asserts.assertEquals("foo/bar", result);
    }

    @Test
    public void cleanPathContainsNoBackText() {
        // given
        String path = "foo//bar/.";

        // when
        String result = ThymeleafTemplateResourceUtils.cleanPath(path);

        // then
        Asserts.assertEquals("foo/bar", result);
    }

    @Test
    public void computeRelativeLocationWithSeparator() {
        // given
        String location = "templates/index.html";
        String relative = "fragments/header.html";
        String absoluteRelative = "/fragments/footer.html";

        // when
        String result = ThymeleafTemplateResourceUtils.computeRelativeLocation(location, relative);
        String absoluteResult = ThymeleafTemplateResourceUtils.computeRelativeLocation(location, absoluteRelative);

        // then
        Asserts.assertEquals("templates/fragments/header.html", result);
        Asserts.assertEquals("templates/fragments/footer.html", absoluteResult);
    }

    @Test
    public void computeRelativeLocationWithoutSeparator() {
        // given
        String location = "index.html";
        String relative = "fragments/header.html";

        // when
        String result = ThymeleafTemplateResourceUtils.computeRelativeLocation(location, relative);

        // then
        Asserts.assertEquals("fragments/header.html", result);
    }

    @Test
    public void computeBaseNameNullEmptyAndTrailingSlash() {
        // given
        String nullPath = null;
        String emptyPath = "";
        String trailingSlash = "folder/";
        String rootPath = "/";

        // when
        String nullResult = ThymeleafTemplateResourceUtils.computeBaseName(nullPath);
        String emptyResult = ThymeleafTemplateResourceUtils.computeBaseName(emptyPath);
        String trailingResult = ThymeleafTemplateResourceUtils.computeBaseName(trailingSlash);
        String rootResult = ThymeleafTemplateResourceUtils.computeBaseName(rootPath);

        // then
        Asserts.assertNull(nullResult);
        Asserts.assertNull(emptyResult);
        Asserts.assertEquals("folder", trailingResult);
        Asserts.assertNull(rootResult);
    }

    @Test
    public void computeBaseNameWithSlashAndDot() {
        // given
        String path = "templates/index.html";
        String hiddenPath = "templates/.hidden";

        // when
        String result = ThymeleafTemplateResourceUtils.computeBaseName(path);
        String hiddenResult = ThymeleafTemplateResourceUtils.computeBaseName(hiddenPath);

        // then
        Asserts.assertEquals("index", result);
        Asserts.assertEquals(".hidden", hiddenResult);
    }

    @Test
    public void computeBaseNameWithoutSlash() {
        // given
        String path = "archive.tar.gz";

        // when
        String result = ThymeleafTemplateResourceUtils.computeBaseName(path);

        // then
        Asserts.assertEquals("archive.tar", result);
    }
}
