package com.tvd12.ezyhttp.server.core.test.util;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.annotation.DoHead;
import com.tvd12.ezyhttp.server.core.util.DoHeadAnnotations;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static java.util.Arrays.asList;

public class DoHeadAnnotationsTest {

    @Test
    public void getURIByValueTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("uriByValue");

        // when
        String actual = DoHeadAnnotations.getURI(annotation);

        // then
        Asserts.assertEquals(actual, "/value");
    }

    @Test
    public void getURIValuePriorityTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("uriByValueAndUri");

        // when
        String actual = DoHeadAnnotations.getURI(annotation);

        // then
        Asserts.assertEquals(actual, "/value");
    }

    @Test
    public void getURIByUriTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("uriByUri");

        // when
        String actual = DoHeadAnnotations.getURI(annotation);

        // then
        Asserts.assertEquals(actual, "/uri");
    }

    @Test
    public void getURIEmptyTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("noUri");

        // when
        String actual = DoHeadAnnotations.getURI(annotation);

        // then
        Asserts.assertEquals(actual, "");
    }

    @Test
    public void getResponseTypeDefaultTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("noUri");

        // when
        String actual = DoHeadAnnotations.getResponseType(annotation);

        // then
        Asserts.assertEquals(actual, ContentTypes.APPLICATION_JSON);
    }

    @Test
    public void getResponseTypeCustomTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("responseTypeTextPlain");

        // when
        String actual = DoHeadAnnotations.getResponseType(annotation);

        // then
        Asserts.assertEquals(actual, ContentTypes.TEXT_PLAIN);
    }

    @Test
    public void getAcceptDefaultTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("uriByValue");

        // when
        Set<String> actual = DoHeadAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf());
    }

    @Test
    public void getAcceptTrimTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("acceptWithSpaces");

        // when
        Set<String> actual = DoHeadAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(
            actual,
            setOf(ContentTypes.APPLICATION_JSON, ContentTypes.MULTIPART_FORM_DATA)
        );
    }

    @Test
    public void getAcceptLowerCaseTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("acceptUpperCase");

        // when
        Set<String> actual = DoHeadAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(
            actual,
            setOf(ContentTypes.APPLICATION_JSON, ContentTypes.MULTIPART_FORM_DATA)
        );
    }

    @Test
    public void getAcceptFilterBlankTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("acceptWithBlank");

        // when
        Set<String> actual = DoHeadAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf(ContentTypes.APPLICATION_JSON));
    }

    @Test
    public void getAcceptDuplicatedAfterNormalizeTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("acceptDuplicated");

        // when
        Set<String> actual = DoHeadAnnotations.getAccept(annotation);

        // then
        Asserts.assertEquals(actual, setOf(ContentTypes.APPLICATION_JSON));
    }

    @Test
    public void getAcceptWithTurkishLocaleTest() throws Exception {
        // given
        DoHead annotation = getAnnotation("acceptWithLetterI");
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));

        // when
        Set<String> actual;
        try {
            actual = DoHeadAnnotations.getAccept(annotation);
        } finally {
            Locale.setDefault(defaultLocale);
        }

        // then
        Asserts.assertEquals(actual, setOf("image/gif"));
    }

    private static Set<String> setOf(String... values) {
        return new HashSet<>(asList(values));
    }

    private static DoHead getAnnotation(String methodName) throws Exception {
        return InternalController.class
            .getDeclaredMethod(methodName)
            .getAnnotation(DoHead.class);
    }

    public static class InternalController {

        @DoHead("/value")
        public void uriByValue() {}

        @DoHead(value = "/value", uri = "/uri")
        public void uriByValueAndUri() {}

        @DoHead(uri = "/uri")
        public void uriByUri() {}

        @DoHead
        public void noUri() {}

        @DoHead(
            value = "/response-type-text-plain",
            responseType = ContentTypes.TEXT_PLAIN
        )
        public void responseTypeTextPlain() {}

        @DoHead(
            value = "/accept-with-spaces",
            accept = {" application/json", "multipart/form-data  "}
        )
        public void acceptWithSpaces() {}

        @DoHead(
            value = "/accept-upper-case",
            accept = {"Application/JSON", "MULTIPART/FORM-DATA"}
        )
        public void acceptUpperCase() {}

        @DoHead(
            value = "/accept-with-blank",
            accept = {"", "   ", "application/json"}
        )
        public void acceptWithBlank() {}

        @DoHead(
            value = "/accept-duplicated",
            accept = {"application/json", " Application/Json ", "APPLICATION/JSON"}
        )
        public void acceptDuplicated() {}

        @DoHead(
            value = "/accept-with-letter-i",
            accept = "IMAGE/GIF"
        )
        public void acceptWithLetterI() {}
    }
}
