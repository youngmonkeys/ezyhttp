package com.tvd12.ezyhttp.core.test.codec;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.io.EzyDates;
import com.tvd12.ezyhttp.core.codec.DefaultStringDeserializer;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;

public class SingletonStringDeserializerTest {
	
	private final DefaultStringDeserializer sut = new DefaultStringDeserializer();
	
	@Test
	public void deserializeBooleanArray() throws Exception {
		// given
		String source = "true, false";
		
		// when
		boolean[] actual = SingletonStringDeserializer
				.getInstance()
				.deserialize(source, boolean[].class);
		
		// then
		Asserts.assertEquals(new boolean[] {true, false}, actual);
	}

	@Test
	public void deserializePrimitive() throws Exception {
		// given
		boolean booleanValue = RandomUtil.randomBoolean();
		byte byteValue = RandomUtil.randomByte();
		char charValue = RandomUtil.randomChar();
		double doubleValue = RandomUtil.randomDouble();
		float floatValue = RandomUtil.randomFloat();
		int intValue = RandomUtil.randomInt();
		long longValue = RandomUtil.randomLong();
		short shortValue = RandomUtil.randomShort();
		
		// when
		boolean booleanActual = sut.deserialize(booleanValue + "", boolean.class);
		byte byteActual = sut.deserialize(byteValue + "", byte.class);
		char charActual = sut.deserialize(charValue + "", char.class);
		double doubleActual = sut.deserialize(doubleValue + "", double.class);
		float floatActual = sut.deserialize(floatValue + "", float.class);
		int intActual = sut.deserialize(intValue + "", int.class);
		long longActual = sut.deserialize(longValue + "", long.class);
		short shortActual = sut.deserialize(shortValue + "", short.class);
		
		// then
		Asserts.assertEquals(booleanActual, booleanValue);
		Asserts.assertEquals(byteActual, byteValue);
		Asserts.assertEquals(charActual, charValue);
		Asserts.assertEquals(doubleActual, doubleValue);
		Asserts.assertEquals(floatActual, floatValue);
		Asserts.assertEquals(intActual, intValue);
		Asserts.assertEquals(longActual, longValue);
		Asserts.assertEquals(shortActual, shortValue);
	}
	
	@Test
	public void deserializeWrapper() throws Exception {
		// given
		Boolean booleanValue = RandomUtil.randomBoolean();
		Byte byteValue = RandomUtil.randomByte();
		Character charValue = RandomUtil.randomChar();
		Double doubleValue = RandomUtil.randomDouble();
		Float floatValue = RandomUtil.randomFloat();
		Integer intValue = RandomUtil.randomInt();
		Long longValue = RandomUtil.randomLong();
		Short shortValue = RandomUtil.randomShort();
		String stringValue = RandomUtil.randomShortAlphabetString();
		
		// when
		Boolean booleanActual = sut.deserialize(booleanValue + "", Boolean.class);
		Byte byteActual = sut.deserialize(byteValue + "", Byte.class);
		Character charActual = sut.deserialize(charValue + "", Character.class);
		Double doubleActual = sut.deserialize(doubleValue + "", Double.class);
		Float floatActual = sut.deserialize(floatValue + "", Float.class);
		Integer intActual = sut.deserialize(intValue + "", Integer.class);
		Long longActual = sut.deserialize(longValue + "", Long.class);
		Short shortActual = sut.deserialize(shortValue + "", Short.class);
		String stringActual = sut.deserialize(stringValue, String.class);
		
		// then
		Asserts.assertEquals(booleanActual, booleanValue);
		Asserts.assertEquals(byteActual, byteValue);
		Asserts.assertEquals(charActual, charValue);
		Asserts.assertEquals(doubleActual, doubleValue);
		Asserts.assertEquals(floatActual, floatValue);
		Asserts.assertEquals(intActual, intValue);
		Asserts.assertEquals(longActual, longValue);
		Asserts.assertEquals(shortActual, shortValue);
		Asserts.assertEquals(stringActual, stringValue);
	}
	
	@Test
	public void deserializePrimitiveArray() throws Exception {
		// given
		boolean[] booleanValues = new boolean[] { RandomUtil.randomBoolean(), RandomUtil.randomBoolean() };
		byte[] byteValues = new byte[] { RandomUtil.randomByte(), RandomUtil.randomByte() };
		char[] charValues = new char[] { RandomUtil.randomChar(), RandomUtil.randomChar() };
		double[] doubleValues = new double[] { RandomUtil.randomDouble(), RandomUtil.randomDouble() };
		float[] floatValues = new float[] { RandomUtil.randomFloat(), RandomUtil.randomFloat() };
		int[] intValues = new int[] { RandomUtil.randomInt(), RandomUtil.randomInt() };
		long[] longValues = new long[] { RandomUtil.randomLong(), RandomUtil.randomLong() };
		short[] shortValues = new short[] { RandomUtil.randomShort(), RandomUtil.randomShort() };
		
		// when
		boolean[] booleanActuals = sut.deserialize(booleanValues[0] + "," + booleanValues[1], boolean[].class);
		byte[] byteActuals = sut.deserialize(byteValues[0] + "," + byteValues[1], byte[].class);
		char[] charActuals = sut.deserialize(charValues[0] + "," + charValues[1], char[].class);
		double[] doubleActuals = sut.deserialize(doubleValues[0] + "," + doubleValues[1], double[].class);
		float[] floatActuals = sut.deserialize(floatValues[0] + "," + floatValues[1], float[].class);
		int[] intActuals = sut.deserialize(intValues[0] + "," + intValues[1], int[].class);
		long[] longActuals = sut.deserialize(longValues[0] + "," + longValues[1], long[].class);
		short[] shortActuals = sut.deserialize(shortValues[0] + "," + shortValues[1], short[].class);
		
		// then
		Asserts.assertEquals(booleanActuals, booleanValues);
		Asserts.assertEquals(byteActuals, byteValues);
		Asserts.assertEquals(charActuals, charValues);
		Asserts.assertEquals(doubleActuals, doubleValues);
		Asserts.assertEquals(floatActuals, floatValues);
		Asserts.assertEquals(intActuals, intValues);
		Asserts.assertEquals(longActuals, longValues);
		Asserts.assertEquals(shortActuals, shortValues);
	}
	
	@Test
	public void deserializeWrapperArray() throws Exception {
		// given
		Boolean[] booleanValues = new Boolean[] { RandomUtil.randomBoolean(), RandomUtil.randomBoolean() };
		Byte[] byteValues = new Byte[] { RandomUtil.randomByte(), RandomUtil.randomByte() };
		Character[] charValues = new Character[] { RandomUtil.randomChar(), RandomUtil.randomChar() };
		Double[] doubleValues = new Double[] { RandomUtil.randomDouble(), RandomUtil.randomDouble() };
		Float[] floatValues = new Float[] { RandomUtil.randomFloat(), RandomUtil.randomFloat() };
		Integer[] intValues = new Integer[] { RandomUtil.randomInt(), RandomUtil.randomInt() };
		Long[] longValues = new Long[] { RandomUtil.randomLong(), RandomUtil.randomLong() };
		Short[] shortValues = new Short[] { RandomUtil.randomShort(), RandomUtil.randomShort() };
		String[] stringValues = new String[] { RandomUtil.randomShortAlphabetString(), RandomUtil.randomShortAlphabetString() };
		
		// when
		Boolean[] booleanActuals = sut.deserialize(booleanValues[0] + "," + booleanValues[1], Boolean[].class);
		Byte[] byteActuals = sut.deserialize(byteValues[0] + "," + byteValues[1], Byte[].class);
		Character[] charActuals = sut.deserialize(charValues[0] + "," + charValues[1], Character[].class);
		Double[] doubleActuals = sut.deserialize(doubleValues[0] + "," + doubleValues[1], Double[].class);
		Float[] floatActuals = sut.deserialize(floatValues[0] + "," + floatValues[1], Float[].class);
		Integer[] intActuals = sut.deserialize(intValues[0] + "," + intValues[1], Integer[].class);
		Long[] longActuals = sut.deserialize(longValues[0] + "," + longValues[1], Long[].class);
		Short[] shortActuals = sut.deserialize(shortValues[0] + "," + shortValues[1], Short[].class);
		String[] stringActuals = sut.deserialize(stringValues[0] + "," + stringValues[1], String[].class);
		
		// then
		Asserts.assertEquals(booleanActuals, booleanValues);
		Asserts.assertEquals(byteActuals, byteValues);
		Asserts.assertEquals(charActuals, charValues);
		Asserts.assertEquals(doubleActuals, doubleValues);
		Asserts.assertEquals(floatActuals, floatValues);
		Asserts.assertEquals(intActuals, intValues);
		Asserts.assertEquals(longActuals, longValues);
		Asserts.assertEquals(shortActuals, shortValues);
		Asserts.assertEquals(stringActuals, stringActuals);
	}
	
	@Test
	public void deserializeSpecialValues() throws Exception {
		// given
		Date date = RandomUtil.randomDate();
		Instant instant = RandomUtil.randomInstant();
		LocalDate localDate = RandomUtil.randomLocalDate();
		LocalTime localTime = RandomUtil.randomLocalTime();
		LocalDateTime localDateTime = RandomUtil.randomLocalDateTime();
		BigInteger bigInteger = RandomUtil.random32BitBigInteger();
		BigDecimal bigDecimal = RandomUtil.random32BitBigDecimal();
		
		// when
		Date actualDate = sut.deserialize(date.getTime() + "", Date.class);
		Instant actualInstant = sut.deserialize(instant.toEpochMilli() + "", Instant.class);
		LocalDate actualLocalDate = sut.deserialize(localDate.toString(), LocalDate.class);
		LocalTime actualLocalTime = sut.deserialize(EzyDates.format(localTime, EzyDates.TIME_PATTERN_STANDARD), LocalTime.class);
		LocalDateTime actualLocalDateTime = sut.deserialize(localDateTime.toString(), LocalDateTime.class);
		BigInteger actualBigInteger = sut.deserialize(bigInteger.toString(), BigInteger.class);
		BigDecimal actualBigDecimal = sut.deserialize(bigDecimal.toString(), BigDecimal.class, String.class);
		
		// then
		Asserts.assertEquals(date, actualDate);
		Asserts.assertEquals(instant, actualInstant);
		Asserts.assertEquals(localDate, actualLocalDate);
		Asserts.assertEquals(localTime, actualLocalTime);
		Asserts.assertEquals(localDateTime, actualLocalDateTime);
		Asserts.assertEquals(bigInteger, actualBigInteger);
		Asserts.assertEquals(bigDecimal, actualBigDecimal);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rawCollectionDeserializer() throws Exception {
		// given
		String value = "1,2,3";
		
		// when
		List<String> actualList = sut.deserialize(value, List.class, null);
		Set<String> actualSet = sut.deserialize(value, Set.class, null);
		
		// then
		Asserts.assertEquals(Lists.newArrayList("1", "2", "3"), actualList);
		Asserts.assertEquals(Sets.newHashSet("1", "2", "3"), actualSet);
		Asserts.assertEmpty(
		    SingletonStringDeserializer.getInstance()
		        .deserialize(null, List.class, String.class)
	    );
		Asserts.assertEmpty(
            SingletonStringDeserializer.getInstance()
                .deserialize(null, Set.class, String.class)
        );
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void intCollectionDeserializer() throws Exception {
		// given
		String value = "1,2,3";
		
		// when
		List<Integer> actualList = sut.deserialize(value, List.class, Integer.class);
		Set<Integer> actualSet = sut.deserialize(value, Set.class, Integer.class);
		
		// then
		Asserts.assertEquals(Lists.newArrayList(1, 2, 3), actualList);
		Asserts.assertEquals(Sets.newHashSet(1, 2, 3), actualSet);
	}
	
	@Test
	public void emptyValueDeserialize() throws Exception {
		// given
		String value = "";
		
		// when
		int[] actual = sut.deserialize(value, int[].class);
		
		// then
		Asserts.assertEquals(actual.length, 0);
	}
	
	@Test
	public void noMapper() {
		// given
		String value = RandomUtil.randomShortAlphabetString();
		
		// when
		Throwable exception = Asserts.assertThrows(() -> sut.deserialize(value, getClass()));
		
		// then
		Asserts.assertEquals(exception.getClass(), IOException.class);
	}
	
	@Test
	public void exceptionCase() {
		// given
		String value = RandomUtil.randomShortAlphabetString();
		
		// when
		Throwable exception = Asserts.assertThrows(() -> sut.deserialize(value, Long.class));
		
		// then
		Asserts.assertEquals(exception.getClass(), IOException.class);
	}
	
	@Test
	public void deserializeNullStringToPrimitive() throws Exception {
		// given
		boolean booleanActual = false;
		byte byteActual = (byte) 0;
		char charActual = '\u0000';
		double doubleActual = 0.0D;
		float floatActual = 0.0F;
		int intActual = 0;
		long longActual = 0L;
		short shortActual = (short) 0;

		// when
		boolean booleanValue = sut.deserialize(null, boolean.class);
		byte byteValue = sut.deserialize(null, byte.class);
		char charValue = sut.deserialize(null, char.class);
		double doubleValue = sut.deserialize(null, double.class);
		float floatValue = sut.deserialize(null, float.class);
		int intValue = sut.deserialize(null, int.class);
		long longValue = sut.deserialize(null, long.class);
		short shortValue = sut.deserialize(null, short.class);
		
		// then
		Asserts.assertEquals(booleanActual, booleanValue);
		Asserts.assertEquals(byteActual, byteValue);
		Asserts.assertEquals(charActual, charValue);
		Asserts.assertEquals(doubleActual, doubleValue);
		Asserts.assertEquals(floatActual, floatValue);
		Asserts.assertEquals(intActual, intValue);
		Asserts.assertEquals(longActual, longValue);
		Asserts.assertEquals(shortActual, shortValue);
	}
	
	@Test
	public void deserializeNullStringToWrapper() throws Exception {
		// given
		Boolean booleanActual = null;
		Byte byteActual = null;
		Character charActual = null;
		Double doubleActual = null;
		Float floatActual = null;
		Integer intActual = null;
		Long longActual = null;
		Short shortActual = null;
		String stringActual = null;
		
		// when
		Boolean booleanValue = sut.deserialize(null, Boolean.class);
		Byte byteValue = sut.deserialize(null, Byte.class);
		Character charValue = sut.deserialize(null, Character.class);
		Double doubleValue = sut.deserialize(null, Double.class);
		Float floatValue = sut.deserialize(null, Float.class);
		Integer intValue = sut.deserialize(null, Integer.class);
		Long longValue = sut.deserialize(null, Long.class);
		Short shortValue = sut.deserialize(null, Short.class);
		String stringValue = sut.deserialize(null, String.class);
		
		// then
		Asserts.assertEquals(booleanActual, booleanValue);
		Asserts.assertEquals(byteActual, byteValue);
		Asserts.assertEquals(charActual, charValue);
		Asserts.assertEquals(doubleActual, doubleValue);
		Asserts.assertEquals(floatActual, floatValue);
		Asserts.assertEquals(intActual, intValue);
		Asserts.assertEquals(longActual, longValue);
		Asserts.assertEquals(shortActual, shortValue);
		Asserts.assertEquals(stringActual, stringValue);
	}
	
	@Test
	public void deserializeNullStringToSpecialValues() throws Exception {
		// given
		Date date = null;
		Instant instant = null;
		LocalDate localDate = null;
		LocalTime localTime = null;
		LocalDateTime localDateTime = null;
		BigInteger bigInteger = null;
		BigDecimal bigDecimal = null;
		
		// when
		Date actualDate = sut.deserialize(null, Date.class);
		Instant actualInstant = sut.deserialize(null, Instant.class);
		LocalDate actualLocalDate = sut.deserialize(null, LocalDate.class);
		LocalTime actualLocalTime = sut.deserialize(null, LocalTime.class);
		LocalDateTime actualLocalDateTime = sut.deserialize(null, LocalDateTime.class);
		BigInteger actualBigInteger = sut.deserialize(null, BigInteger.class);
		BigDecimal actualBigDecimal = sut.deserialize(null, BigDecimal.class);
		
		// then
		Asserts.assertEquals(date, actualDate);
		Asserts.assertEquals(instant, actualInstant);
		Asserts.assertEquals(localDate, actualLocalDate);
		Asserts.assertEquals(localTime, actualLocalTime);
		Asserts.assertEquals(localDateTime, actualLocalDateTime);
		Asserts.assertEquals(bigInteger, actualBigInteger);
		Asserts.assertEquals(bigDecimal, actualBigDecimal);
	}
	
	@Test
	public void deserializeEnumTest() throws Exception {
	    MyEnum value = SingletonStringDeserializer.getInstance()
	        .deserialize("HELLO", MyEnum.class);
	    Asserts.assertEquals(value, MyEnum.HELLO);
	    Asserts.assertNull(SingletonStringDeserializer.getInstance()
            .deserialize(null, MyEnum.class));
	    Asserts.assertEquals(SingletonStringDeserializer.getInstance()
            .deserialize("hello", MyEnum.class), MyEnum.HELLO);
	    
	}
	
	public static enum MyEnum {
	    HELLO, WORLD
	}
}
