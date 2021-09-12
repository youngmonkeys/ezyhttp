package com.tvd12.ezyhttp.core.test.codec;

import java.util.Arrays;

import org.testng.annotations.Test;

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
}
