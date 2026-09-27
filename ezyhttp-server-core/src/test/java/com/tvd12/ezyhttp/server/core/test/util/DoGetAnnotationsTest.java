package com.tvd12.ezyhttp.server.core.test.util;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.util.DoGetAnnotations;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static java.util.Arrays.asList;

public class DoGetAnnotationsTest {

    @Test
    public void getAcceptDefaultTest() throws Exception {
        // given
        DoGet annotation = getAnnotation("noAccept");

        // when
        Set<String> actual = DoGetAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf());
    }

    @Test
    public void getAcceptTrimTest() throws Exception {
        // given
        DoGet annotation = getAnnotation("acceptWithSpaces");

        // when
        Set<String> actual = DoGetAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(
            actual,
            setOf(ContentTypes.APPLICATION_JSON, ContentTypes.MULTIPART_FORM_DATA)
        );
    }

    @Test
    public void getAcceptLowerCaseTest() throws Exception {
        // given
        DoGet annotation = getAnnotation("acceptUpperCase");

        // when
        Set<String> actual = DoGetAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(
            actual,
            setOf(ContentTypes.APPLICATION_JSON, ContentTypes.MULTIPART_FORM_DATA)
        );
    }

    @Test
    public void getAcceptFilterBlankTest() throws Exception {
        // given
        DoGet annotation = getAnnotation("acceptWithBlank");

        // when
        Set<String> actual = DoGetAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf(ContentTypes.APPLICATION_JSON));
    }

    @Test
    public void getAcceptDuplicatedAfterNormalizeTest() throws Exception {
        // given
        DoGet annotation = getAnnotation("acceptDuplicated");

        // when
        Set<String> actual = DoGetAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf(ContentTypes.APPLICATION_JSON));
    }

    @Test
    public void getAcceptWithTurkishLocaleTest() throws Exception {
        // given
        DoGet annotation = getAnnotation("acceptWithLetterI");
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));

        // when
        Set<String> actual;
        try {
            actual = DoGetAnnotations.getAccept(annotation);
        } finally {
            Locale.setDefault(defaultLocale);
        }

        // then
        Asserts.assertEquals(actual, setOf("image/gif"));
    }

    private static Set<String> setOf(String... values) {
        return new HashSet<>(asList(values));
    }

    private static DoGet getAnnotation(String methodName) throws Exception {
        return InternalController.class
            .getDeclaredMethod(methodName)
            .getAnnotation(DoGet.class);
    }

    public static class InternalController {

        @DoGet("/no-accept")
        public void noAccept() {}

        @DoGet(
            value = "/accept-with-spaces",
            accept = {" application/json", "multipart/form-data  "}
        )
        public void acceptWithSpaces() {}

        @DoGet(
            value = "/accept-upper-case",
            accept = {"Application/JSON", "MULTIPART/FORM-DATA"}
        )
        public void acceptUpperCase() {}

        @DoGet(
            value = "/accept-with-blank",
            accept = {"", "   ", "application/json"}
        )
        public void acceptWithBlank() {}

        @DoGet(
            value = "/accept-duplicated",
            accept = {"application/json", " Application/Json ", "APPLICATION/JSON"}
        )
        public void acceptDuplicated() {}

        @DoGet(
            value = "/accept-with-letter-i",
            accept = "IMAGE/GIF"
        )
        public void acceptWithLetterI() {}
    }
}
