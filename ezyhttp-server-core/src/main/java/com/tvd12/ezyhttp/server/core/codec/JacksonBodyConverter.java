package com.tvd12.ezyhttp.server.core.codec;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.jackson.JacksonObjectMapperBuilder;

public class JacksonBodyConverter implements BodyConverter {

	protected final ObjectMapper objectMapper;
	
	public JacksonBodyConverter() {
		this(JacksonObjectMapperBuilder.newInstance().build());
	}
	
	public JacksonBodyConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@Override
	public byte[] serialize(Object body) throws IOException {
		byte[] bytes = objectMapper.writeValueAsBytes(body);
		return bytes;
	}
	
	@Override
	public <T> T deserialize(byte[] bytes, Class<T> bodyType) throws IOException {
		T body = objectMapper.readValue(bytes, bodyType);
		return body;
	}
	
	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> bodyType) throws IOException {
		T body = objectMapper.readValue(inputStream, bodyType);
		return body;
	}

}
