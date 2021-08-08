package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.io.EzyDataConverter;
import com.tvd12.ezyfox.io.EzyDates;
import com.tvd12.ezyfox.io.EzyStrings;

public class DefaultStringDeserializer implements StringDeserializer {

	protected final Map<Class<?>, StringMapper> mappers; 
	
	public DefaultStringDeserializer() {
		this.mappers = defaultMappers();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(String value, Class<T> outType) throws IOException {
		StringMapper mapper = mappers.get(outType);
		if(mapper == null)
			throw new IOException("has no deserializer for: " + outType.getName());
		try {
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
		
		map.put(String[].class, v -> stringToStringArray(v));
		map.put(List.class, v -> stringToList(v));
		map.put(Set.class, v -> stringToSet(v));
		
		map.put(boolean[].class, v -> stringToPrimitiveBoolean(v));
		map.put(byte[].class, v -> stringToPrimitiveByte(v));
		map.put(double[].class, v -> stringToPrimitiveDouble(v));
		map.put(float[].class, v -> stringToPrimitiveFloat(v));
		map.put(int[].class, v -> stringToPrimitiveInteger(v));
		map.put(long[].class, v -> stringToPrimitiveLong(v));
		map.put(short[].class, v -> stringToPrimitiveShort(v));
		
		map.put(Boolean[].class, v -> stringToWrapperBoolean(v));
		map.put(Byte[].class, v -> stringToWrapperByte(v));
		map.put(Double[].class, v -> stringToWrapperDouble(v));
		map.put(Float[].class, v -> stringToWrapperFloat(v));
		map.put(Integer[].class, v -> stringToWrapperInteger(v));
		map.put(Long[].class, v -> stringToWrapperLong(v));
		map.put(Short[].class, v -> stringToWrapperShort(v));
		
		map.put(Date.class, v -> new Date(Long.valueOf(v)));
		map.put(Instant.class, v -> Instant.ofEpochMilli(Long.valueOf(v)));
		map.put(LocalDate.class, v -> EzyDates.parseDate(v));
		map.put(LocalTime.class, v -> EzyDates.parseTime(v));
		map.put(LocalDateTime.class, v -> EzyDates.parseDateTime(v));
		map.put(BigInteger.class, v -> new BigInteger(v));
		map.put(BigDecimal.class, v -> new BigDecimal(v));
		return map;
	}

	// =============== array, collection ===============
	protected String[] stringToStringArray(String value) {
		if(EzyStrings.isEmpty(value))
			return new String[0];
		String[] array = value.split(",");
		return array;
	}
	
	protected List<String> stringToList(String value) {
		return Lists.newArrayList(stringToStringArray(value));
	}
	
	protected Set<String> stringToSet(String value) {
		return Sets.newHashSet(stringToStringArray(value));
	}
	
	// =============== primitive array ===============
	protected boolean[] stringToPrimitiveBoolean(String value) {
		String[] array = stringToStringArray(value);
		boolean[] answer = new boolean[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Boolean.valueOf(array[i]);
		return answer;
	}
	
	protected byte[] stringToPrimitiveByte(String value) {
		String[] array = stringToStringArray(value);
		byte[] answer = new byte[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Byte.valueOf(array[i]);
		return answer;
	}

	protected double[] stringToPrimitiveDouble(String value) {
		String[] array = stringToStringArray(value);
		double[] answer = new double[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Double.valueOf(array[i]);
		return answer;
	}
	
	protected float[] stringToPrimitiveFloat(String value) {
		String[] array = stringToStringArray(value);
		float[] answer = new float[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Float.valueOf(array[i]);
		return answer;
	}
	
	protected int[] stringToPrimitiveInteger(String value) {
		String[] array = stringToStringArray(value);
		int[] answer = new int[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Integer.valueOf(array[i]);
		return answer;
	}
	
	protected long[] stringToPrimitiveLong(String value) {
		String[] array = stringToStringArray(value);
		long[] answer = new long[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Long.valueOf(array[i]);
		return answer;
	}
	
	protected short[] stringToPrimitiveShort(String value) {
		String[] array = stringToStringArray(value);
		short[] answer = new short[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Short.valueOf(array[i]);
		return answer;
	}
	
	// =============== wrapper array ===============
	protected Boolean[] stringToWrapperBoolean(String value) {
		String[] array = stringToStringArray(value);
		Boolean[] answer = new Boolean[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Boolean.valueOf(array[i]);
		return answer;
	}
	
	protected Byte[] stringToWrapperByte(String value) {
		String[] array = stringToStringArray(value);
		Byte[] answer = new Byte[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Byte.valueOf(array[i]);
		return answer;
	}

	protected Double[] stringToWrapperDouble(String value) {
		String[] array = stringToStringArray(value);
		Double[] answer = new Double[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Double.valueOf(array[i]);
		return answer;
	}
	
	protected Float[] stringToWrapperFloat(String value) {
		String[] array = stringToStringArray(value);
		Float[] answer = new Float[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Float.valueOf(array[i]);
		return answer;
	}
	
	protected Integer[] stringToWrapperInteger(String value) {
		String[] array = stringToStringArray(value);
		Integer[] answer = new Integer[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Integer.valueOf(array[i]);
		return answer;
	}
	
	protected Long[] stringToWrapperLong(String value) {
		String[] array = stringToStringArray(value);
		Long[] answer = new Long[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Long.valueOf(array[i]);
		return answer;
	}
	
	protected Short[] stringToWrapperShort(String value) {
		String[] array = stringToStringArray(value);
		Short[] answer = new Short[array.length];
		for(int i = 0 ; i < array.length ; ++i)
			answer[i] = Short.valueOf(array[i]);
		return answer;
	}
	
}
