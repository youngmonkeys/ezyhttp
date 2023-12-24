package com.tvd12.ezyhttp.core.test.codec;

import com.tvd12.ezyhttp.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.test.assertion.Asserts;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.testng.annotations.Test;

public class BodyDeserializerTest {

    @Test
    public void commonTest() throws Exception {
        // given
        BodyDeserializer sut = new BodyDeserializer() {};

        // when
        Asserts.assertNull(sut.deserialize("", Object.class));
        Asserts.assertNull(sut.deserialize(mock(BodyData.class), Object.class));
        Asserts.assertNull(sut.deserialize(mock(InputStream.class), Object.class));
    }

    @Test
    public void deserializeToStringWithContentLengthTest() throws Exception {
        // given
        byte[] bytes = "abc".getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        int contentLength = 2;
        BodyDeserializer sut = new BodyDeserializer() {};

        // when
        String actual = sut.deserializeToString(inputStream, contentLength);

        // then
        Asserts.assertEquals("abc", actual);
    }

    @Test
    public void deserializeToStringWithoutContentLengthTest() throws Exception {
        // given
        byte[] bytes = "abc".getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        int contentLength = 0;
        BodyDeserializer sut = new BodyDeserializer() {};

        // when
        String actual = sut.deserializeToString(inputStream, contentLength);

        // then
        Asserts.assertEquals("abc", actual);
    }
}
