package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.jackson.JacksonObjectMapperBuilder;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.ezyhttp.core.net.MapEncoder;

public class FormBodyConverter implements BodyConverter {

	protected final ObjectMapper objectMapper;
	
	public FormBodyConverter() {
		this(JacksonObjectMapperBuilder.newInstance().build());
	}
	
	public FormBodyConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public byte[] serialize(Object body) throws IOException {
		Map<String, Object> map = objectMapper.convertValue(body, Map.class);
	    byte[] bytes = MapEncoder.encodeToBytes(map);
	    return bytes;
	}
	
	@Override
	public <T> T deserialize(BodyData data, Class<T> bodyType) throws IOException {
		Map<String, String> parameters = data.getParameters();
		T body = objectMapper.convertValue(parameters, bodyType);
		return body;
	}

}
