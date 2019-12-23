package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.tvd12.ezyhttp.core.data.BodyData;

public interface BodyDeserializer {
	
	default <T> T deserialize(
			String data, Class<T> bodyType) throws IOException {
		return null;
	}

	default <T> T deserialize(
			BodyData data, Class<T> bodyType) throws IOException {
		return null;
	}
	
	default <T> T deserialize(
			InputStream inputStream, Class<T> bodyType) throws IOException {
		return null;
	}
	
	default String deserializeToString(
			InputStream inputStream, int contentLength) throws IOException {
		byte[] bytes = new byte[contentLength];
		int readBytes = inputStream.read(bytes);
		return new String(bytes, 0, readBytes, StandardCharsets.UTF_8);
	}
}
