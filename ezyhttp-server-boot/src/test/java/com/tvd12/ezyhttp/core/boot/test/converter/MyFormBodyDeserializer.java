package com.tvd12.ezyhttp.core.boot.test.converter;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.core.annotation.BodyConvert;
import com.tvd12.ezyhttp.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.data.BodyData;

@BodyConvert(ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED)
public class MyFormBodyDeserializer implements BodyDeserializer {

	protected final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public <T> T deserialize(BodyData data, Class<T> bodyType) throws IOException {
		Map<String, String> params = data.getParameters();
		T answer = objectMapper.convertValue(params, bodyType);
		return answer;
	}
	
}
