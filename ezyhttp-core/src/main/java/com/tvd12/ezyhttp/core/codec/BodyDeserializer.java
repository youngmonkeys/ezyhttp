package com.tvd12.ezyhttp.core.codec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface BodyDeserializer {

	<T> T deserialize(InputStream inputStream, Class<T> bodyType) throws IOException;
	
	default <T> T deserialize(byte[] bytes, Class<T> bodyType) throws IOException {
		InputStream inputStream = new ByteArrayInputStream(bytes);
		T body = deserialize(inputStream, bodyType);
		return body;
	}
	
}
