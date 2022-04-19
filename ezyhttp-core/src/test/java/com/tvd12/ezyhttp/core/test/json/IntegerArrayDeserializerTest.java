package com.tvd12.ezyhttp.core.test.json;

import java.util.Collections;
import java.util.Map;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tvd12.ezyhttp.core.json.IntegerArrayDeserializer;
import com.tvd12.test.assertion.Asserts;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class IntegerArrayDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testWithString() {
        // given
        Map<String, String> map = Collections.singletonMap("value", "1,2,3");

        // when
        Value value = objectMapper.convertValue(map, Value.class);

        // then
        Asserts.assertEquals(value, new Value(new Integer[]{1, 2, 3}));
    }

    @Test
    public void testWithArray() {
        // given
        Map<String, double[]> map = Collections.singletonMap("value", new double[]{1, 2, 3});

        // when
        Value value = objectMapper.convertValue(map, Value.class);

        // then
        Asserts.assertEquals(value, new Value(new Integer[]{1, 2, 3}));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Value {

        @JsonDeserialize(using = IntegerArrayDeserializer.class)
        public Integer[] value;
    }
}
