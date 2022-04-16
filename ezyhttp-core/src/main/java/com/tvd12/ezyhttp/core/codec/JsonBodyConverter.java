package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.core.data.BodyData;

public class JsonBodyConverter implements BodyConverter {

	protected final ObjectMapper objectMapper;
	
	public JsonBodyConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@Override
	public byte[] serialize(Object body) throws IOException {
	    return body instanceof String
	        ? ((String)body).getBytes(StandardCharsets.UTF_8)
	        : objectMapper.writeValueAsBytes(body);
	}
	
	@Override
	public <T> T deserialize(String data, Class<T> bodyType) throws IOException {
		return objectMapper.readValue(data, bodyType);
	}
	
	@Override
	public <T> T deserialize(BodyData data, Class<T> bodyType) throws IOException {
		InputStream inputStream = data.getInputStream();
		return deserialize(inputStream, bodyType);
	}
	
	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> bodyType) throws IOException {
		return objectMapper.readValue(inputStream, bodyType);
	}

}
