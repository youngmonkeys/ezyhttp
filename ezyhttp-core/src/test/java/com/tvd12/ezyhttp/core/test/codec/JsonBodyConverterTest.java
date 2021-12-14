package com.tvd12.ezyhttp.core.test.codec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.core.codec.JsonBodyConverter;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.test.assertion.Asserts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class JsonBodyConverterTest {

	@Test
	public void serializeStringTest() throws Exception {
		// given
		JsonBodyConverter sut = new JsonBodyConverter(new ObjectMapper());
		
		String body = "{\"hello\":\"world\",\"foo\":\"bar\"}";
		
		// when
		byte[] actual = sut.serialize(body);
		System.out.println(new String(actual));
		
		// then
		Asserts.assertEquals(body.getBytes(), actual);
	}
	
	@Test
	public void serializeMapTest() throws Exception {
		// given
		JsonBodyConverter sut = new JsonBodyConverter(new ObjectMapper());
		
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("hello", "world");
		body.put("foo", "bar");
		
		// when
		byte[] actual = sut.serialize(body);
		
		// then
		byte[] expectation = "{\"hello\":\"world\",\"foo\":\"bar\"}".getBytes();
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void serializeObjectTest() throws Exception {
		// given
		JsonBodyConverter sut = new JsonBodyConverter(new ObjectMapper());
		
		JsonRequest body = new JsonRequest();
		
		// when
		byte[] actual = sut.serialize(body);
		
		// then
		byte[] expectation = "{\"hello\":\"world\",\"foo\":\"bar\"}".getBytes();
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void deserializeStringTest() throws Exception {
		// given
		JsonBodyConverter sut = new JsonBodyConverter(new ObjectMapper());
		
		String data = "{\"hello\":\"world\",\"foo\":\"bar\"}";
		
		// when
		JsonRequest actual = sut.deserialize(data, JsonRequest.class);
		
		// then
		JsonRequest expectation = new JsonRequest();
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void deserializeFromBodyData() throws Exception {
		// given
		JsonBodyConverter sut = new JsonBodyConverter(new ObjectMapper());
		
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
		
		byte[] bytes = new ObjectMapper().writeValueAsBytes(parameters);
		
		BodyData bodyData = mock(BodyData.class);
		when(bodyData.getInputStream()).thenReturn(new ByteArrayInputStream(bytes));
		
		// when
		JsonRequest2 actual = sut.deserialize(bodyData, JsonRequest2.class);
		
		// then
		JsonRequest2 expectation = new JsonRequest2(
				true, (byte)1, 'a', 2.0D, 3.0F, 4, 5L, (short)6, "abc"
		);
		Asserts.assertEquals(expectation, actual);
		verify(bodyData, times(1)).getInputStream();
	}
	
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JsonRequest {
		private String hello = "world";
		private String foo = "bar";
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JsonRequest2 {
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
