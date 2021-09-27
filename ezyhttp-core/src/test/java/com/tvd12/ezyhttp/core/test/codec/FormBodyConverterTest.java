package com.tvd12.ezyhttp.core.test.codec;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.core.codec.FormBodyConverter;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.test.assertion.Asserts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FormBodyConverterTest {

	@Test
	public void serializeStringTest() throws Exception {
		// given
		FormBodyConverter sut = new FormBodyConverter(new ObjectMapper());
		
		String body = "{\"hello\":\"world\",\"foo\":\"bar\"}";
		
		// when
		byte[] actual = sut.serialize(body);
		
		// then
		byte[] expectation = "hello=world&foo=bar".getBytes();
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void serializeMapTest() throws Exception {
		// given
		FormBodyConverter sut = new FormBodyConverter(new ObjectMapper());
		
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("hello", "world");
		body.put("foo", "bar");
		
		// when
		byte[] actual = sut.serialize(body);
		
		// then
		byte[] expectation = "hello=world&foo=bar".getBytes();
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void serializeObjectTest() throws Exception {
		// given
		FormBodyConverter sut = new FormBodyConverter(new ObjectMapper());
		
		FormRequest body = new FormRequest();
		
		// when
		byte[] actual = sut.serialize(body);
		
		// then
		byte[] expectation = "hello=world&foo=bar".getBytes();
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void deserializeFromString() throws Exception {
		// given
		FormBodyConverter sut = new FormBodyConverter(new ObjectMapper());
		String data = "hello=Hello+World&foo=Foo+Bar";
		
		// when
		FormRequest actual = sut.deserialize(data, FormRequest.class);
		
		// then
		FormRequest expectation = new FormRequest("Hello World", "Foo Bar");
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void deserializeFromBodyData() throws Exception {
		// given
		FormBodyConverter sut = new FormBodyConverter(new ObjectMapper());
		
		Map<String, String> parameters = new HashMap<>();
		parameters.put("booleanValue", "true");
		parameters.put("byteValue", "1");
		parameters.put("charValue", "a");
		parameters.put("doubleValue", "2");
		parameters.put("floatValue", "3");
		parameters.put("intValue", "4");
		parameters.put("longValue", "5");
		parameters.put("shortValue", "6");
		parameters.put("stringValue", "abc");
		
		BodyData bodyData = mock(BodyData.class);
		when(bodyData.getParameters()).thenReturn(parameters);
		
		// when
		FormRequest2 actual = sut.deserialize(bodyData, FormRequest2.class);
		
		// then
		FormRequest2 expectation = new FormRequest2(
				true, (byte)1, 'a', 2.0D, 3.0F, 4, 5L, (short)6, "abc"
		);
		Asserts.assertEquals(expectation, actual);
		verify(bodyData, times(1)).getParameters();
	}
	
	@Test
	public void deserializeFromInputStream() throws Exception {
		// given
		FormBodyConverter sut = new FormBodyConverter(new ObjectMapper());
		String data = "hello=Hello+World&foo=Foo+Bar";
		InputStream inputStream = new ByteArrayInputStream(data.getBytes());
		
		// when
		FormRequest actual = sut.deserialize(inputStream, FormRequest.class);
		
		// then
		FormRequest expectation = new FormRequest("Hello World", "Foo Bar");
		Asserts.assertEquals(expectation, actual);
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FormRequest {
		private String hello = "world";
		private String foo = "bar";
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FormRequest2 {
		private boolean booleanValue;
		private byte byteValue;
		private char charValue;
		private double doubleValue;
		private float floatValue;
		private int intValue;
		private long longValue;
		private short shortValue;
		private String stringValue;
	}
}
