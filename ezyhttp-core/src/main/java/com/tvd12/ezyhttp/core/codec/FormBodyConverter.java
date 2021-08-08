package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.stream.EzyInputStreams;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;
import com.tvd12.ezyhttp.core.net.MapDecoder;
import com.tvd12.ezyhttp.core.net.MapEncoder;

public class FormBodyConverter implements BodyConverter {

	protected final ObjectMapper objectMapper;
	
	public FormBodyConverter() {
		this(new ObjectMapperBuilder().build());
	}
	
	public FormBodyConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public byte[] serialize(Object body) throws IOException {
		if(body instanceof String)
			return ((String)body).getBytes(StandardCharsets.UTF_8);
		Map<String, Object> map = objectMapper.convertValue(body, Map.class);
	    byte[] bytes = MapEncoder.encodeToBytes(map);
	    return bytes;
	}
	
	@Override
	public <T> T deserialize(String data, Class<T> bodyType) throws IOException {
		Map<String, String> parameters = MapDecoder.decodeFromString(data);
		T body = objectMapper.convertValue(parameters, bodyType);
		return body;
		
	}
	
	@Override
	public <T> T deserialize(BodyData data, Class<T> bodyType) throws IOException {
		Map<String, String> parameters = data.getParameters();
		T body = objectMapper.convertValue(parameters, bodyType);
		return body;
	}
	
	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> bodyType) throws IOException {
		String data = EzyInputStreams.toStringUtf8(inputStream);
		return deserialize(data, bodyType);
	}

}
