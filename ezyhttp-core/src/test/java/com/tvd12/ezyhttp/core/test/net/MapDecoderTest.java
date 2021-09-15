package com.tvd12.ezyhttp.core.test.net;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.net.MapDecoder;
import com.tvd12.test.assertion.Asserts;

public class MapDecoderTest {

	@Test
	public void decodeFromBytesTest() throws Exception {
		// given
		byte[] bytes = "hello=%7Bworld%7D&foo=%3Cbar%3E".getBytes();
		
		// when
		Map<String, String> actual = MapDecoder.decodeFromBytes(bytes);
		
		// then
		Map<String, String> expectation = new HashMap<>();
		expectation.put("hello", "{world}");
		expectation.put("foo", "<bar>");
		
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void decodeEmptyString() throws Exception {
		// given
		byte[] bytes = new byte[0];
		
		// when
		Map<String, String> actual = MapDecoder.decodeFromBytes(bytes);
		
		// then
		Map<String, String> expectation = new HashMap<>();
		Asserts.assertEquals(expectation, actual);
	}
}
