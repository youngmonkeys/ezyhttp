package com.tvd12.ezyhttp.server.core.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.io.EzyDataConverter;

public class StringDefaultDeserializer implements StringDeserializer {

	protected final Map<Class<?>, StringMapper> mappers; 
	
	public StringDefaultDeserializer() {
		this.mappers = defaultMappers();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(String value, Class<T> outType) throws IOException {
		try {
			StringMapper mapper = mappers.get(outType);
			T answer = (T) mapper.apply(value);
			return answer;
		}
		catch (Exception e) {
			throw new IOException("can't deserialize value: " + value + " to: " + outType.getName(), e);
		}
	}
	
	protected Map<Class<?>, StringMapper> defaultMappers() {
		Map<Class<?>, StringMapper> map = new HashMap<>();
		map.put(String.class, v -> v);
		map.put(boolean.class, v -> Boolean.valueOf(v));
		map.put(byte.class, v -> Byte.valueOf(v));
		map.put(char.class, v -> EzyDataConverter.stringToChar(v));
		map.put(double.class, v -> Double.valueOf(v));
		map.put(float.class, v -> Float.valueOf(v));
		map.put(int.class, v -> Integer.valueOf(v));
		map.put(long.class, v -> Long.valueOf(v));
		map.put(short.class, v -> Short.valueOf(v));

		map.put(Boolean.class, v -> Boolean.valueOf(v));
		map.put(Byte.class, v -> Byte.valueOf(v));
		map.put(Character.class, v -> EzyDataConverter.stringToChar(v));
		map.put(Double.class, v -> Double.valueOf(v));
		map.put(Float.class, v -> Float.valueOf(v));
		map.put(Integer.class, v -> Integer.valueOf(v));
		map.put(Long.class, v -> Long.valueOf(v));
		map.put(Short.class, v -> Short.valueOf(v));
		
		return map;
	}

}
