package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.jackson.JacksonObjectMapperBuilder;
import com.tvd12.ezyhttp.core.data.BodyData;

public class JsonBodyConverter implements BodyConverter {

	protected final ObjectMapper objectMapper;
	
	public JsonBodyConverter() {
		this(JacksonObjectMapperBuilder.newInstance().build());
	}
	
	public JsonBodyConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@Override
	public byte[] serialize(Object body) throws IOException {
		byte[] bytes = objectMapper.writeValueAsBytes(body);
		return bytes;
	}
	
	@Override
	public <T> T deserialize(String data, Class<T> bodyType) throws IOException {
		T body = objectMapper.readValue(data, bodyType);
		return body;
	}
	
	@Override
	public <T> T deserialize(BodyData data, Class<T> bodyType) throws IOException {
		InputStream inputStream = data.getInputStream();
		T body = objectMapper.readValue(inputStream, bodyType);
		return body;
	}
	
	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> bodyType) throws IOException {
		T body = objectMapper.readValue(inputStream, bodyType);
		return body;
	}

}
