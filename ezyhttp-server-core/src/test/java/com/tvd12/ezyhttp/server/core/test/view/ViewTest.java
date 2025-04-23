package com.tvd12.ezyhttp.server.core.test.view;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class ViewTest {

    @Test
    public void test() {
        // given
        View sut = View.builder()
            .template("index.html")
            .locale(Locale.ENGLISH)
            .locale("vi")
            .contentType(ContentTypes.TEXT_HTML_UTF8)
            .addHeader("foo", "bar")
            .addHeader("hello", "world")
            .addHeaders(Collections.singletonMap("header", "headerValue"))
            .addCookie(new Cookie("cookie1", "cookie1Value"))
            .addCookie(new Cookie("cookie2", "cookie2Value"))
            .addCookie("cookie2", "cookie3Value", "/path")
            .addVariable("variable1", "variable1Value")
            .addVariable("variable2", "variable2Value")
            .appendToVariable("list", "a")
            .appendValueToVariable("list", "b")
            .appendValuesToVariable("list", new String[] {"c", "d"})
            .appendValuesToVariable("list", Arrays.asList("e", "f"))
            .addVariables(null)
            .build();

        // when
        sut.setVariable("setValue", "value");
        sut.setVariables(Collections.singletonMap("mapKey", "mapValue"));
        sut.appendToVariable("list", "g");
        sut.appendValueToVariable("list", "h");
        sut.appendValueToVariable("list", (Object) null);
        sut.appendValuesToVariable("list", new String[] {"i", "j"});
        sut.appendValuesToVariable("list", Arrays.asList("k", "l"));
        sut.setVariables(null);
        sut.putKeyValueToVariable("zzz", null, null);
        sut.putKeyValueToVariable("zzz", "hello", null);

        // then
        Asserts.assertEquals("index.html", sut.getTemplate());
        Asserts.assertEquals(new Locale("vi"), sut.getLocale());
        Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getContentType());
        Asserts.assertEquals(
            EzyMapBuilder.mapBuilder()
                .put("foo", "bar")
                .put("hello", "world")
                .put("header", "headerValue")
                .build(),
            sut.getHeaders());
        Asserts.assertEquals("variable1Value", sut.getVariable("variable1"));
        Asserts.assertEquals("variable2Value", sut.getVariable("variable2"));
        Asserts.assertEquals(
            EzyMapBuilder.mapBuilder()
                .put("variable1", "variable1Value")
                .put("variable2", "variable2Value")
                .put("setValue", "value")
                .put("list", Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"))
                .put("mapKey", "mapValue")
                .build(),
            sut.getVariables());
        Asserts.assertEquals(sut.getVariable("setValue"), "value");
        Asserts.assertEquals(
            sut.getVariable("list"),
            Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"),
            false
        );
        Asserts.assertTrue(sut.containsVariable("variable1"));
        Asserts.assertFalse(sut.containsVariable("i don't know"));
        Asserts.assertEquals(sut.getCookies().get(2).getPath(), "/path");
        Asserts.assertEquals(sut.getVariable("mapKey"), "mapValue");
    }

    @Test
    public void ofTest() {
        // given
        View sut = View.of("home.html");
        sut.setLocale(Locale.CANADA);

        // when
        // then
        Asserts.assertEquals("home.html", sut.getTemplate());
        Asserts.assertEquals(sut.getLocale(), Locale.CANADA);
    }

    @Test
    public void setTemplateAndContentTypeTest() {
        // given
        View sut = View.builder()
            .template("home.html")
            .contentType(ContentTypes.TEXT_PLAIN)
            .build();

        // when
        sut.setTemplate("world");
        sut.setContentType(ContentTypes.TEXT_HTML_UTF8);

        // then
        Asserts.assertEquals("world", sut.getTemplate());
        Asserts.assertEquals(sut.getContentType(), ContentTypes.TEXT_HTML_UTF8);
    }

    @Test
    public void createFailedDueToTemplateIsNull() {
        // given
        // when
        Throwable e = Asserts.assertThrows(() -> View.builder().build());

        // then
        Asserts.assertThat(e).isEqualsType(NullPointerException.class);
    }

    @Test
    public void putKeyValuesToVariableBuilderTest() {
        // given
        Map<String, Object> map = EzyMapBuilder.mapBuilder()
            .put("a", "1")
            .put("b", 2)
            .toMap();
        View sut = View.builder()
            .template("abc")
            .putKeyValuesToVariable("hello", map)
            .build();

        // when
        // then
        Asserts.assertEquals(sut.getVariable("hello"), map);
    }

    @Test
    public void putKeyValuesToVariableTest() {
        // given
        Map<String, Object> map = EzyMapBuilder.mapBuilder()
            .put("a", "1")
            .put("b", 2)
            .toMap();
        View sut = View.builder()
            .template("abc")
            .build();
        sut.putKeyValuesToVariable("hello", map);

        // when
        // then
        Asserts.assertEquals(sut.getVariable("hello"), map);
    }

    @Test
    public void putKeyValuesToVariableByBuilderTest() {
        // given
        View sut = View.builder()
            .template("abc")
            .putKeyValueToVariable("hello", "a", "1")
            .putKeyValueToVariable("hello", "b", 2)
            .build();

        // when
        // then
        Map<String, Object> map = EzyMapBuilder.mapBuilder()
            .put("a", "1")
            .put("b", 2)
            .toMap();
        Asserts.assertEquals(sut.getVariable("hello"), map);
    }

    @Test
    public void setVariableIfAbsentTest() {
        // given
        View sut = View.builder()
            .template("abc")
            .addVariable("hello", "world")
            .build();

        // when
        sut.setVariableIfAbsent("hello", "Other");

        // then
        Asserts.assertEquals(sut.getVariable("hello"), "world");
    }

    @Test
    public void setVariableIfAbsentWithSupplierTest() {
        // given
        View sut = View.builder()
            .template("abc")
            .build();

        // when
        sut.setVariableIfAbsent("hello", () -> "world");

        // then
        Asserts.assertEquals(sut.getVariable("hello"), "world");
    }

    @Test
    public void setVariableIfAbsentWithSupplierButExistTest() {
        // given
        View sut = View.builder()
            .template("abc")
            .addVariable("hello", "world")
            .build();

        // when
        sut.setVariableIfAbsent("hello", () -> "Other");

        // then
        Asserts.assertEquals(sut.getVariable("hello"), "world");
    }
}
