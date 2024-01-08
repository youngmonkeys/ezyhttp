package com.tvd12.ezyhttp.core.test.net;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.net.PathVariables;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.performance.Performance;
import com.tvd12.test.reflect.MethodInvoker;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class PathVariablesTest extends BaseTest {

    @Test
    public void test() {
        String uri11 = "/api/v1/customer/{name}/create";
        String check11 = "/api/v1/customer/dung/create";
        List<Entry<String, String>> variables = PathVariables.getVariables(uri11, check11);
        System.out.println(variables);
        long time = Performance.create()
            .test(() -> PathVariables.getVariables(uri11, check11))
            .getTime();
        System.out.println(time);
    }

    @Test
    public void testWithEncodedUri() {
        // given
        String uri11 = "/api/v1/customer/{name}/create";
        String check11 = "/api/v1/customer/Hello%20World/create";

        // when
        List<Entry<String, String>> actual = PathVariables.getVariables(
            uri11,
            check11
        );

        // then
        System.out.println(actual);
        Asserts.assertEquals(
            actual.stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue)),
            EzyMapBuilder.mapBuilder()
                .put("name", "Hello World")
                .build()
        );
    }

    @Test
    public void isPathVariableTest() {
        Asserts.assertFalse(PathVariables.isPathVariable("{a"));
        Asserts.assertFalse(PathVariables.isPathVariable("a"));
        Asserts.assertFalse(PathVariables.isPathVariable("a}"));
    }

    @Test
    public void anyPathTest() {
        // given
        String template = "/market/items/{projectName}/java/docs/*";
        String uri = "/market/items/hello/java/docs/world/index.html";

        // when
        List<Entry<String, String>> actual = PathVariables.getVariables(
            template,
            uri
        );

        // then
        Asserts.assertEquals(
            actual.stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue)),
            EzyMapBuilder.mapBuilder()
                .put("projectName", "hello")
                .put("*", "world/index.html")
                .build()
        );
    }

    @Test
    public void anyPathComplexTest() {
        // given
        String template = "/versions/{ezyplatformVersion}/java-docs/{moduleName}/*";
        String uri = "/versions/0.0.2/java-docs/ezyplatform-web/org/youngmonkeys/Hello%20World/web/package-frame.html";

        // when
        List<Entry<String, String>> actual = PathVariables.getVariables(
            template,
            uri
        );

        // then
        Asserts.assertEquals(
            actual.stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue)),
            EzyMapBuilder.mapBuilder()
                .put("ezyplatformVersion", "0.0.2")
                .put("moduleName", "ezyplatform-web")
                .put("*", "org/youngmonkeys/Hello World/web/package-frame.html")
                .build()
        );
    }

    @Test
    public void decodeUriPathValueCaseExceptionTest() {
        // given
        Object actual = MethodInvoker.create()
            .staticClass(PathVariables.class)
            .method("decodeUriPathValue")
            .param(String.class, null)
            .invoke();

        Asserts.assertNull(actual);
    }
}
