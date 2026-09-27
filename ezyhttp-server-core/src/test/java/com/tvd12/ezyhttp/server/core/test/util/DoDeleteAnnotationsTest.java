package com.tvd12.ezyhttp.server.core.test.util;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.annotation.DoDelete;
import com.tvd12.ezyhttp.server.core.util.DoDeleteAnnotations;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static java.util.Arrays.asList;

public class DoDeleteAnnotationsTest {

    @Test
    public void getAcceptDefaultTest() throws Exception {
        // given
        DoDelete annotation = getAnnotation("noAccept");

        // when
        Set<String> actual = DoDeleteAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf());
    }

    @Test
    public void getAcceptTrimTest() throws Exception {
        // given
        DoDelete annotation = getAnnotation("acceptWithSpaces");

        // when
        Set<String> actual = DoDeleteAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(
            actual,
            setOf(ContentTypes.APPLICATION_JSON, ContentTypes.MULTIPART_FORM_DATA)
        );
    }

    @Test
    public void getAcceptLowerCaseTest() throws Exception {
        // given
        DoDelete annotation = getAnnotation("acceptUpperCase");

        // when
        Set<String> actual = DoDeleteAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(
            actual,
            setOf(ContentTypes.APPLICATION_JSON, ContentTypes.MULTIPART_FORM_DATA)
        );
    }

    @Test
    public void getAcceptFilterBlankTest() throws Exception {
        // given
        DoDelete annotation = getAnnotation("acceptWithBlank");

        // when
        Set<String> actual = DoDeleteAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf(ContentTypes.APPLICATION_JSON));
    }

    @Test
    public void getAcceptDuplicatedAfterNormalizeTest() throws Exception {
        // given
        DoDelete annotation = getAnnotation("acceptDuplicated");

        // when
        Set<String> actual = DoDeleteAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf(ContentTypes.APPLICATION_JSON));
    }

    @Test
    public void getAcceptWithTurkishLocaleTest() throws Exception {
        // given
        DoDelete annotation = getAnnotation("acceptWithLetterI");
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));

        // when
        Set<String> actual;
        try {
            actual = DoDeleteAnnotations.getAccept(annotation);
        } finally {
            Locale.setDefault(defaultLocale);
        }

        // then
        Asserts.assertEquals(actual, setOf("image/gif"));
    }

    private static Set<String> setOf(String... values) {
        return new HashSet<>(asList(values));
    }

    private static DoDelete getAnnotation(String methodName) throws Exception {
        return InternalController.class
            .getDeclaredMethod(methodName)
            .getAnnotation(DoDelete.class);
    }

    public static class InternalController {

        @DoDelete("/no-accept")
        public void noAccept() {}

        @DoDelete(
            value = "/accept-with-spaces",
            accept = {" application/json", "multipart/form-data  "}
        )
        public void acceptWithSpaces() {}

        @DoDelete(
            value = "/accept-upper-case",
            accept = {"Application/JSON", "MULTIPART/FORM-DATA"}
        )
        public void acceptUpperCase() {}

        @DoDelete(
            value = "/accept-with-blank",
            accept = {"", "   ", "application/json"}
        )
        public void acceptWithBlank() {}

        @DoDelete(
            value = "/accept-duplicated",
            accept = {"application/json", " Application/Json ", "APPLICATION/JSON"}
        )
        public void acceptDuplicated() {}

        @DoDelete(
            value = "/accept-with-letter-i",
            accept = "IMAGE/GIF"
        )
        public void acceptWithLetterI() {}
    }
}
