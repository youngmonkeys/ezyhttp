package com.tvd12.ezyhttp.core.test.json;

import java.util.Collections;
import java.util.Map;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tvd12.ezyhttp.core.json.DoubleArrayDeserializer;
import com.tvd12.test.assertion.Asserts;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class DoubleArrayDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    public void testWithString() {
        // given
        Map<String, String> map = Collections.singletonMap("value", "1,2,3");
        
        // when
        Value value = objectMapper.convertValue(map, Value.class);
        
        // then
        Asserts.assertEquals(value, new Value(new Double[] { 1.0D, 2.0D, 3.0D }));
    }
    
    @Test
    public void testWithArray() {
        // given
        Map<String, double[]> map = Collections.singletonMap("value", new double[] { 1, 2 ,3 });
        
        // when
        Value value = objectMapper.convertValue(map, Value.class);
        
        // then
        Asserts.assertEquals(value, new Value(new Double[] { 1.0D, 2.0D, 3.0D }));
    }
    
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Value {
        
        @JsonDeserialize(using = DoubleArrayDeserializer.class)
        public Double[] value;
    }
}
