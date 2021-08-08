package com.tvd12.ezyhttp.core.test.codec;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
import com.tvd12.test.assertion.Asserts;

public class SingletonStringDeserializerTest {
	
	@Test
	public void serializeBooleanArray() throws Exception {
		// given
		String source = "true, false";
		
		// when
		boolean[] actual = SingletonStringDeserializer
				.getInstance()
				.deserialize(source, boolean[].class);
		
		// then
		Asserts.assertEquals(new boolean[] {true, false}, actual);
	}

}
