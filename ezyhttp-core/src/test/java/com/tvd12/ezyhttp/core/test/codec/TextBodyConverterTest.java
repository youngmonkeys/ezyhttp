package com.tvd12.ezyhttp.core.test.codec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.codec.TextBodyConverter;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;

public class TextBodyConverterTest {

    @Test
    public void serializeTest() throws Exception {
        // given
        TextBodyConverter sut = new TextBodyConverter();
        
        String body = RandomUtil.randomShortAlphabetString();
        
        // when
        byte[] actual = sut.serialize(body);
        
        // then
        Asserts.assertEquals(body.getBytes(), actual);
    }
    
    @Test
    public void serializeFailedDueToException() {
        // given
        TextBodyConverter sut = new TextBodyConverter();
        
        // when
        Throwable exception = Asserts.assertThrows(() -> sut.serialize(null));
        
        // then
        Asserts.assertEquals(exception.getClass(), IOException.class);
    }
    
    @Test
    public void deserializeTest() throws Exception {
        // given
        TextBodyConverter sut = new TextBodyConverter();
        
        String data = RandomUtil.randomShortAlphabetString();
        
        // when
        String actual = sut.deserialize(data, String.class);
        
        // then
        Asserts.assertEquals(data, actual);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void deserializeMapTest() throws Exception {
        // given
        TextBodyConverter sut = new TextBodyConverter();
        
        String data = "{\"hello\":\"world\"}";
        
        // when
        Map<String, String> actual = sut.deserialize(data, Map.class);
        
        // then
        Asserts.assertEquals(Collections.singletonMap("hello", "world"), actual, false);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void deserializeBodyDataTest() throws Exception {
        // given
        TextBodyConverter sut = new TextBodyConverter();
        
        String data = "{\"hello\":\"world\"}";
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        
        BodyData bodyData = mock(BodyData.class);
        when(bodyData.getInputStream()).thenReturn(inputStream);
        
        // when
        Map<String, String> actual = sut.deserialize(bodyData, Map.class);
        
        // then
        Asserts.assertEquals(Collections.singletonMap("hello", "world"), actual, false);
        
        verify(bodyData, times(1)).getInputStream();
    }
}
