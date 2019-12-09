package com.tvd12.ezyhttp.server.core.codec;

import java.io.IOException;

public interface StringDeserializer {

	<T> T deserialize(String value, Class<T> outType) throws IOException;
	
}
