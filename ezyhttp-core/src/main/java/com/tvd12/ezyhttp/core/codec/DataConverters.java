package com.tvd12.ezyhttp.core.codec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.util.BodyConvertAnnotations;

import lombok.Getter;

public class DataConverters implements EzyDestroyable {

	@Getter
	protected StringDeserializer stringDeserializer;
	protected final BodyConverter defaultBodyConverter;
	protected final Map<String, BodySerializer> bodySerializers;
	protected final Map<String, BodyDeserializer> bodyDeserializers;
	
	public DataConverters(ObjectMapper objectMapper) {
		this.bodySerializers = new HashMap<>();
		this.bodyDeserializers = new HashMap<>();
		this.defaultBodyConverter = new TextBodyConverter();
		this.addDefaultConverter(objectMapper);
	}
	
	public BodySerializer getBodySerializer(String contentType) {
		BodySerializer serializer = bodySerializers.get(contentType);
		if(serializer == null) {
			String realContentType = ContentTypes.getContentType(contentType);
			serializer = bodySerializers.get(realContentType);
		}
		if(serializer == null) {
			serializer = defaultBodyConverter;
		}
		return serializer;
	}
	
	public BodyDeserializer getBodyDeserializer(String contentType) {
		BodyDeserializer deserializer = bodyDeserializers.get(contentType);
		if(deserializer == null) {
			String realContentType = ContentTypes.getContentType(contentType);
			deserializer = bodyDeserializers.get(realContentType);
		}
		if(deserializer == null) {
			deserializer = defaultBodyConverter;
		}
		return deserializer;
	}
	
	public void addBodyConverters(List<?> converters) {
		for(Object converter : converters)
			addBodyConverter(converter);
	}
	
	public void addBodyConverter(Object converter) {
		if(converter instanceof BodySerializer) {
			String contentType = BodyConvertAnnotations.getContentType(converter);
			this.bodySerializers.put(contentType, (BodySerializer) converter);
		}
		if(converter instanceof BodyDeserializer) {
			String contentType = BodyConvertAnnotations.getContentType(converter);
			this.bodyDeserializers.put(contentType, (BodyDeserializer) converter);
		}
	}
	
	public void addBodyConverter(String contentType, Object converter) {
		if(converter instanceof BodySerializer) {
			this.bodySerializers.put(contentType, (BodySerializer) converter);
		}
		if(converter instanceof BodyDeserializer) {
			this.bodyDeserializers.put(contentType, (BodyDeserializer) converter);
		}
	}
	
	public void addBodyConverters(Map<String, Object> converterByContentType) {
		for(Entry<String, Object> e : converterByContentType.entrySet()) {
			addBodyConverter(e.getKey(), e.getValue());
		}
	}
	
	public void setStringConverter(Object converter) {
		if(converter instanceof StringDeserializer)
			this.stringDeserializer = (StringDeserializer) converter;
	}
	
	public void setStringConverters(List<?> converters) {
		for(Object converter : converters)
			setStringConverter(converter);
	}
	
	protected void addDefaultConverter(ObjectMapper objectMapper) {
		JsonBodyConverter jsonBodyConverter = new JsonBodyConverter(objectMapper);
		FormBodyConverter formBodyConverter = new FormBodyConverter(objectMapper);
		this.bodySerializers.put(ContentTypes.APPLICATION_JSON, jsonBodyConverter);
		this.bodySerializers.put(ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED, formBodyConverter);
		this.bodySerializers.put(ContentTypes.TEXT_PLAIN, defaultBodyConverter);
		this.bodyDeserializers.put(ContentTypes.APPLICATION_JSON, jsonBodyConverter);
		this.bodyDeserializers.put(ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED, formBodyConverter);
		this.bodyDeserializers.put(ContentTypes.MULTIPART_FORM_DATA, formBodyConverter);
		this.stringDeserializer = new DefaultStringDeserializer();
		this.bodyDeserializers.put(ContentTypes.TEXT_PLAIN, defaultBodyConverter);
		this.bodyDeserializers.put(ContentTypes.TEXT_HTML, defaultBodyConverter);
		this.bodyDeserializers.put(ContentTypes.TEXT_HTML_UTF8, defaultBodyConverter);
	}
	
	@Override
	public void destroy() {
		this.bodySerializers.clear();
		this.bodyDeserializers.clear();
	}
}
