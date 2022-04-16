package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.stream.EzyInputStreams;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.ezyhttp.core.net.MapDecoder;
import com.tvd12.ezyhttp.core.net.MapEncoder;

public class FormBodyConverter implements BodyConverter {

	protected final ObjectMapper objectMapper;
	
	public FormBodyConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public byte[] serialize(Object body) throws IOException {
		Map<String, Object> map = body instanceof String
				? objectMapper.readValue(((String)body), Map.class)
				: objectMapper.convertValue(body, Map.class);
		return MapEncoder.encodeToBytes(map);
	}
	
	@Override
	public <T> T deserialize(String data, Class<T> bodyType) throws IOException {
		Map<String, String> parameters = MapDecoder.decodeFromString(data);
		return objectMapper.convertValue(parameters, bodyType);
		
	}
	
	@Override
	public <T> T deserialize(BodyData data, Class<T> bodyType) {
		Map<String, String> parameters = data.getParameters();
		return objectMapper.convertValue(parameters, bodyType);
	}
	
	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> bodyType) throws IOException {
		String data = EzyInputStreams.toStringUtf8(inputStream);
		return deserialize(data, bodyType);
	}

}
