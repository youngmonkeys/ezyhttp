package com.tvd12.ezyhttp.core.test.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.ezyhttp.core.annotation.BodyConvert;
import com.tvd12.ezyhttp.core.codec.BodyConverter;
import com.tvd12.ezyhttp.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.core.codec.BodySerializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.codec.JsonBodyConverter;
import com.tvd12.ezyhttp.core.codec.StringDeserializer;
import com.tvd12.ezyhttp.core.codec.TextBodyConverter;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.test.assertion.Asserts;

public class DataConvertersTest {

	@Test
	public void getBodySerializerNormalTest() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		String contentType = ContentTypes.APPLICATION_JSON;
		
		// when
		BodySerializer actual = sut.getBodySerializer(contentType);
		
		// then
		Asserts.assertEquals(JsonBodyConverter.class, actual.getClass());
	}
	
	@Test
	public void getBodySerializerWithUTF8Test() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		String contentType = ContentTypes.TEXT_HTML_UTF8;
		
		// when
		BodySerializer actual = sut.getBodySerializer(contentType);
		
		// then
		Asserts.assertEquals(TextBodyConverter.class, actual.getClass());
	}
	
	@Test
	public void getBodySerializerDefaultTest() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		String contentType = "unknown";
		
		// when
		BodySerializer actual = sut.getBodySerializer(contentType);
		
		// then
		Asserts.assertEquals(TextBodyConverter.class, actual.getClass());
	}
	
	@Test
	public void getBodyDeserializerNormalTest() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		String contentType = ContentTypes.APPLICATION_JSON;
		
		// when
		BodyDeserializer actual = sut.getBodyDeserializer(contentType);
		
		// then
		Asserts.assertEquals(JsonBodyConverter.class, actual.getClass());
	}
	
	@Test
	public void getBodyDeserializerWithUTF8Test() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		String contentType = ContentTypes.TEXT_HTML_UTF8;
		
		// when
		BodyDeserializer actual = sut.getBodyDeserializer(contentType);
		
		// then
		Asserts.assertEquals(TextBodyConverter.class, actual.getClass());
	}
	
	@Test
	public void getBodyDeserializerDefaultTest() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		String contentType = "unknown";
		
		// when
		BodyDeserializer actual = sut.getBodyDeserializer(contentType);
		
		// then
		Asserts.assertEquals(TextBodyConverter.class, actual.getClass());
	}
	
	@Test
	public void addBodyConvertersTest() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		List<?> bodyConverters = Lists.newArrayList(
				new JsonConverter(),
				new Object()
		);
		
		// when
		sut.addBodyConverters(bodyConverters);
		
		// then
		Asserts.assertEquals(
				sut.getBodyDeserializer(ContentTypes.APPLICATION_JSON), 
				bodyConverters.get(0)
		);
		Asserts.assertEquals(
				sut.getBodySerializer(ContentTypes.APPLICATION_JSON), 
				bodyConverters.get(0)
		);
	}
	
	@Test
	public void addBodyConverterMapTest() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		Map<String, Object> bodyConverters = new HashMap<>();
		bodyConverters.put(ContentTypes.APPLICATION_JSON, new JsonConverter());
		bodyConverters.put(ContentTypes.TEXT_HTML, new Object());
		
		// when
		sut.addBodyConverters(bodyConverters);
		
		// then
		Asserts.assertEquals(
				sut.getBodyDeserializer(ContentTypes.APPLICATION_JSON), 
				bodyConverters.get(ContentTypes.APPLICATION_JSON)
		);
		Asserts.assertEquals(
				sut.getBodySerializer(ContentTypes.APPLICATION_JSON), 
				bodyConverters.get(ContentTypes.APPLICATION_JSON)
		);
		
		Asserts.assertNotEquals(
				sut.getBodyDeserializer(ContentTypes.TEXT_HTML), 
				bodyConverters.get(ContentTypes.TEXT_HTML)
		);
		Asserts.assertNotEquals(
				sut.getBodySerializer(ContentTypes.TEXT_HTML), 
				bodyConverters.get(ContentTypes.TEXT_HTML)
		);
	}
	
	@Test
	public void setStringConvertersTest() {
		// given
		DataConverters sut = new DataConverters(new ObjectMapper());
		List<?> converters = Lists.newArrayList(
			new MyStringDeserializer(),
			new Object()
		);
		
		// when
		sut.setStringConverters(converters);
		
		// then
		Asserts.assertEquals(converters.get(0), sut.getStringDeserializer());
		
	}
	
	@BodyConvert(ContentTypes.APPLICATION_JSON)
	private static class JsonConverter implements BodyConverter {

		@Override
		public byte[] serialize(Object body) throws IOException {
			return null;
		}
	}
	
	private static class MyStringDeserializer implements StringDeserializer {
		@Override
		public <T> T deserialize(String value, Class<T> outType, Class<?> genericType) throws IOException {
			return null;
		}
		
	}
}
