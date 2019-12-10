package com.tvd12.ezyhttp.server.core.codec;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyhttp.server.core.constant.ContentTypes;

import lombok.Getter;
import lombok.Setter;

@Setter
public class DataConverters {

	@Getter
	protected StringDeserializer stringDeserializer;
	protected final Map<String, BodySerializer> bodySerializers;
	protected final Map<String, BodyDeserializer> bodyDeserializers;
	
	public DataConverters() {
		this.bodySerializers = new HashMap<>();
		this.bodyDeserializers = new HashMap<>();
		this.addDefaultConverter();
	}
	
	public BodySerializer getBodySerializer(String contentType) {
		BodySerializer serializer = bodySerializers.get(contentType);
		return serializer;
	}
	
	public BodyDeserializer getBodyDeserializer(String contentType) {
		BodyDeserializer deserializer = bodyDeserializers.get(contentType);
		return deserializer;
	}
	
	protected void addDefaultConverter() {
		JacksonBodyConverter bodyConverter = new JacksonBodyConverter();
		this.bodySerializers.put(ContentTypes.APPLICATION_JSON, bodyConverter);
		this.bodyDeserializers.put(ContentTypes.APPLICATION_JSON, bodyConverter);
		this.stringDeserializer = new StringDefaultDeserializer();
	}
}
