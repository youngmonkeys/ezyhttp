package com.tvd12.ezyhttp.client.test.request;

import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.Headers;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.*;

public class RequestEntityTest {

    @Test
    public void createByHeadersAndBody() {
        // given
        Map<String, List<String>> headers = new HashMap<>();
        String body = RandomUtil.randomShortAlphabetString();

        // when
        RequestEntity sut = new RequestEntity(headers, body);

        // then
        Asserts.assertEquals(new MultiValueMap(headers), sut.getHeaders());
        Asserts.assertEquals(body, sut.getBody());
    }

    @Test
    public void commonTest() {
        // given
        String body = RandomUtil.randomShortAlphabetString();

        // when
        RequestEntity sut = RequestEntity.of(body)
            .header("1", (String) null)
            .header("1", "hello")
            .header("1", "world")
            .header("2", Arrays.asList("foo", "bar"))
            .headers(Collections.singletonMap("3", "monkey"))
            .contentType(ContentTypes.APPLICATION_JSON)
            .build();

        // then
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("1", Arrays.asList("hello", "world"));
        headers.put("2", Arrays.asList("foo", "bar"));
        headers.put("3", Collections.singletonList("monkey"));
        headers.put(Headers.CONTENT_TYPE, Collections.singletonList(ContentTypes.APPLICATION_JSON));
        Asserts.assertEquals(new MultiValueMap(headers), sut.getHeaders());
        Asserts.assertEquals(body, sut.getBody());

        Asserts.assertEquals(ContentTypes.APPLICATION_JSON, sut.getHeader(Headers.CONTENT_TYPE));
        Asserts.assertNull(sut.getHeader("unknown"));
        Asserts.assertEquals(ContentTypes.APPLICATION_JSON, sut.getContentType());
        System.out.println(sut);
    }

    @Test
    public void emptyHeadersTest() {
        // given
        RequestEntity sut = new RequestEntity((Map<String, List<String>>) null, null);

        // when
        // then
        Asserts.assertNull(sut.getHeader("unknown"));
        Asserts.assertEquals(ContentTypes.APPLICATION_JSON, sut.getContentType());
        System.out.println(sut);
    }

    @Test
    public void buildWithBodyNull() {
        // given
        RequestEntity sut = RequestEntity.builder()
            .body(null)
            .build();

        // when
        // then
        Asserts.assertNull(sut.getHeader("unknown"));
        Asserts.assertEquals(ContentTypes.APPLICATION_JSON, sut.getContentType());
        System.out.println(sut);
    }

    @Test
    public void buildMultiMapBody() {
        // given
        Map<String, List<String>> data = new HashMap<>();
        data.put("1", Arrays.asList("hello", "world"));
        data.put("2", Arrays.asList("foo", "bar"));
        data.put("3", Collections.singletonList("monkey"));
        data.put(Headers.CONTENT_TYPE, Collections.singletonList(ContentTypes.APPLICATION_JSON));
        RequestEntity sut = RequestEntity.builder()
            .body(new MultiValueMap(data))
            .build();

        // when
        // then
        Map<String, String> expectation = new HashMap<>();
        expectation.put("1", "hello;world");
        expectation.put("2", "foo;bar");
        expectation.put("3", "monkey");
        expectation.put(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
        Asserts.assertEquals(expectation, sut.getBody());
        System.out.println(sut);
    }
}
