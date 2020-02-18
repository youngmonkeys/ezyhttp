package com.tvd12.ezyhttp.core.codec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.util.BodyConvertAnnotations;

import lombok.Getter;
import lombok.Setter;

public class DataConverters {

	@Setter
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
		if(serializer == null) {
			String realContentType = ContentTypes.getContentType(contentType);
			serializer = bodySerializers.get(realContentType);
		}
		return serializer;
	}
	
	public BodyDeserializer getBodyDeserializer(String contentType) {
		BodyDeserializer deserializer = bodyDeserializers.get(contentType);
		if(deserializer == null) {
			String realContentType = ContentTypes.getContentType(contentType);
			deserializer = bodyDeserializers.get(realContentType);
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
		else if(converter instanceof BodyDeserializer) {
			String contentType = BodyConvertAnnotations.getContentType(converter);
			this.bodyDeserializers.put(contentType, (BodyDeserializer) converter);
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
	
	protected void addDefaultConverter() {
		JsonBodyConverter jsonBodyConverter = new JsonBodyConverter();
		FormBodyConverter formBodyConverter = new FormBodyConverter();
		TextBodyConverter textBodyConverter = new TextBodyConverter();
		this.bodySerializers.put(ContentTypes.APPLICATION_JSON, jsonBodyConverter);
		this.bodyDeserializers.put(ContentTypes.APPLICATION_JSON, jsonBodyConverter);
		this.bodySerializers.put(ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED, formBodyConverter);
		this.bodyDeserializers.put(ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED, formBodyConverter);
		this.stringDeserializer = new DefaultStringDeserializer();
		this.bodyDeserializers.put(ContentTypes.TEXT_PLAIN, textBodyConverter);
		this.bodyDeserializers.put(ContentTypes.TEXT_HTML, textBodyConverter);
		this.bodyDeserializers.put(ContentTypes.TEXT_HTML_UTF8, textBodyConverter);
	}
}
