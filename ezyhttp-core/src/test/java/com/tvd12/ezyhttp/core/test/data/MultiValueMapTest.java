package com.tvd12.ezyhttp.core.test.data;

import java.util.*;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.test.assertion.Asserts;

public class MultiValueMapTest {

    @Test
    public void ofTest() {
        // given
        Map<String, List<String>> map = new HashMap<>();
        map.put("1", Arrays.asList("hello", "world"));
        map.put("2", Arrays.asList("foo", "bar"));
        map.put("3", Collections.emptyList());

        // when
        MultiValueMap sut = MultiValueMap.of(map);

        // then
        Asserts.assertEquals(map.keySet(), sut.keySets());
        Asserts.assertNull(sut.getValue("nothing"));
        Asserts.assertNull(sut.getValue("3"));
        Asserts.assertEquals("hello", sut.getValue("1"));
        Asserts.assertEquals("foo", sut.getValue("2"));
        Asserts.assertEquals("foo", sut.getValue("2", "def"));
        Asserts.assertEquals("def", sut.getValue("3", "def"));
        Asserts.assertEquals(Arrays.asList("hello", "world"), sut.getValues("1"));
        Asserts.assertTrue(sut.getValues("unknow").isEmpty());
    }

    @Test
    public void toMapTest() {
        // given
        Map<String, Object> values = new HashMap<>();
        values.put("a", null);
        values.put("b", "");
        values.put("c", 100);
        values.put("d", "good");
        values.put("e", true);
        TreeMap<String, Object> cookieValues = new TreeMap<>();
        cookieValues.put("accessToken", "123abc");
        cookieValues.put("hello", "world");
        MultiValueMap sut = MultiValueMap.builder()
            .setValue("1", "hello")
            .setValues("2", Arrays.asList("foo", "bar"))
            .setValues("3", Collections.emptyList())
            .setValues("m", values)
            .setValues("Cookie", cookieValues)
            .build();

        // when
        Map<String, String> actual1 = sut.toMap();

        // then
        Map<String, String> expectation = new HashMap<>();
        expectation.put("1", "hello");
        expectation.put("2", "foo;bar");
        expectation.put("m", "a;b=;c=100;d=good;e=true");
        expectation.put("Cookie", "accessToken=123abc;hello=world");
        Asserts.assertEquals(expectation, actual1);
    }

    @Test
    public void getValueMapTest() {
        // given
        Map<String, Object> values = new HashMap<>();
        values.put("a", null);
        values.put("b", "");
        values.put("c", 100);
        values.put("d", "good");
        values.put("e", true);
        MultiValueMap sut = MultiValueMap.builder()
            .setValue("1", "hello")
            .setValues("2", Arrays.asList("foo", "bar"))
            .setValues("3", Collections.emptyList())
            .setValues("m", values)
            .setValue("z", "")
            .build();

        // when
        Map<String, String> actual1 = sut.getValueMap("m");
        Map<String, String> actual2 = sut.getValueMap("1");
        Map<String, String> actual3 = sut.getValueMap("2");
        Map<String, String> actual4 = sut.getValueMap("z");

        // then
        Map<String, Object> expectation1 = new HashMap<>();
        expectation1.put("a", null);
        expectation1.put("b", "");
        expectation1.put("c", "100");
        expectation1.put("d", "good");
        expectation1.put("e", "true");
        Asserts.assertEquals(actual1, expectation1);

        Map<String, String> expectation2 = new HashMap<>();
        expectation2.put("hello", null);
        Asserts.assertEquals(actual2, expectation2);

        Map<String, String> expectation3 = new HashMap<>();
        expectation3.put("foo", null);
        expectation3.put("bar", null);
        Asserts.assertEquals(actual3, expectation3);
        Asserts.assertEquals(new HashMap<>(), actual4);
    }
}
