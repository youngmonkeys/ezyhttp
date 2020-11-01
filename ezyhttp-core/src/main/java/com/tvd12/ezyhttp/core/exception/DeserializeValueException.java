package com.tvd12.ezyhttp.core.exception;

import java.io.IOException;

import lombok.Getter;

@Getter
public class DeserializeValueException extends IOException {
	private static final long serialVersionUID = 351983342119059608L;

	public final String valueName;
	public final Object value;
	public final Class<?> outType;
	
	public DeserializeValueException(
			String valueName, Object value, Class<?> outType, Exception e) {
		super(
			"can't deserialize: " + valueName + 
			" from: " + valueToString(value) + 
			" to: " + outType.getSimpleName()
		);
		this.valueName = valueName;
		this.value = value;
		this.outType = outType;
	}
	
	private static String valueToString(Object value) {
		return value != null 
				? value + "(" + value.getClass().getSimpleName() + ")" 
				: "null";
	}
	
}
