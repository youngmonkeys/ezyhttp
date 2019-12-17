package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.io.InputStream;

import com.tvd12.ezyhttp.core.data.BodyData;

public interface BodyDeserializer {

	<T> T deserialize(BodyData data, Class<T> bodyType) throws IOException;
	
	default <T> T deserialize(
			InputStream inputStream, Class<T> bodyType) throws IOException {
		return null;
	}
}
