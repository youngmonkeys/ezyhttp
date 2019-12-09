package com.tvd12.ezyhttp.server.core.codec;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyhttp.server.core.constant.ContentTypes;

import lombok.Getter;
import lombok.Setter;

@Setter
public class DataConverters {

	@Getter
	protected BodySerializer bodySerializer;
	@Getter
	protected StringDeserializer stringDeserializer;
	protected final Map<String, BodyDeserializer> bodyDeserializers;
	
	public DataConverters() {
		this.bodyDeserializers = new HashMap<>();
		this.addDefaultConverter();
	}
	
	public BodyDeserializer getBodyDeserializer(String contentType) {
		BodyDeserializer deserializer = bodyDeserializers.get(contentType);
		return deserializer;
	}
	
	protected void addDefaultConverter() {
		JacksonBodyConverter bodyConverter = new JacksonBodyConverter();
		this.bodySerializer = bodyConverter;
		this.bodyDeserializers.put(ContentTypes.APPLICATION_JSON, bodyConverter);
		this.stringDeserializer = new StringDefaultDeserializer();
	}
}
