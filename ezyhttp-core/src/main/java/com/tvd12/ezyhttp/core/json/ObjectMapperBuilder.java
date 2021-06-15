package com.tvd12.ezyhttp.core.json;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.io.EzyDates;
import com.tvd12.ezyfox.jackson.JacksonObjectMapperBuilder;

public class ObjectMapperBuilder implements EzyBuilder<ObjectMapper> {

	@Override
	public ObjectMapper build() {
		return JacksonObjectMapperBuilder.newInstance()
			.build()
			.registerModule(newModule())
			.findAndRegisterModules();
	}
	
	protected Module newModule() {
		SimpleModule module = new SimpleModule("ezyhttp");
		module.addDeserializer(LocalDate.class, new LocalDateDeserialize());
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserialize());
		return module;
	}
	
	public static class LocalDateDeserialize extends StdDeserializer<LocalDate> {
		private static final long serialVersionUID = -7550269143426341730L;

		public LocalDateDeserialize() {
			super(LocalDate.class);
		}

		@Override
		public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			if(p.currentTokenId() == JsonToken.VALUE_NUMBER_INT.id()) {
				return EzyDates.millisToDateTime(p.getValueAsLong())
						.toLocalDate();
			}
			else if(p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
				return EzyDates.parseDate(p.getValueAsString()); 
			}
			else {
				throw new IOException("can deserialize value: " + p.getValueAsString() + " to LocalDate");
			}
		}
	}
	
	public static class LocalTimeDeserialize extends StdDeserializer<LocalTime> {
		private static final long serialVersionUID = -7550269143426341730L;

		public LocalTimeDeserialize() {
			super(LocalTime.class);
		}

		@Override
		public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			if(p.currentTokenId() == JsonToken.VALUE_NUMBER_INT.id()) {
				return EzyDates.millisToDateTime(p.getValueAsLong())
						.toLocalTime();
			}
			else if(p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
				return EzyDates.parseTime(p.getValueAsString()); 
			}
			else {
				throw new IOException("can deserialize value: " + p.getValueAsString() + " to LocalTime");
			}
		}
	}
	
	public static class LocalDateTimeDeserialize extends StdDeserializer<LocalDateTime> {
		private static final long serialVersionUID = -7550269143426341730L;

		public LocalDateTimeDeserialize() {
			super(LocalDateTime.class);
		}

		@Override
		public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			if(p.currentTokenId() == JsonToken.VALUE_NUMBER_INT.id()) {
				return EzyDates.millisToDateTime(p.getValueAsLong());
			}
			else if(p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
				return EzyDates.parseDateTime(p.getValueAsString()); 
			}
			else {
				throw new IOException("can deserialize value: " + p.getValueAsString() + " to LocalDateTime");
			}
		}
	}
}
