package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;

import com.tvd12.ezyhttp.core.data.BodyData;

public interface BodyDeserializer {

	<T> T deserialize(BodyData data, Class<T> bodyType) throws IOException;
	
}
